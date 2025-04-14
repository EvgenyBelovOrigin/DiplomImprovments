package ru.netology.nework.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nework.dao.EventDao
import ru.netology.nework.dao.EventRemoteKeyDao
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.dto.MediaUpload
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.model.AttachmentModel
import ru.netology.nework.repository.Repository
import ru.netology.nework.utils.MediaLifecycleObserver
import ru.netology.nework.utils.SingleLiveEvent
import ru.netology.nmedia.auth.AppAuth
import java.io.File
import javax.inject.Inject

private val empty = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorJob = null,
    authorAvatar = null,
    content = "",
    datetime = "",
    published = "2024-07-02T21:34:44.562Z",
    coords = null,
    type = EventType.ONLINE,
    likeOwnerIds = emptyList(),
    likedByMe = false,
    speakerIds = emptyList(),
    participantsIds = emptyList(),
    participatedByMe = false,
    attachment = null,
    link = null,
    users = emptyMap(),
    ownedByMe = false,
    isPlayingAudio = false,
    isPlayingAudioPaused = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: Repository,
    private val appAuth: AppAuth,
    private val dao: EventDao,
    private val remoteDao: EventRemoteKeyDao,
) : ViewModel() {

    val data: Flow<PagingData<Event>> = appAuth.authState
        .flatMapLatest { token ->
            repository.events.map { pagingData ->
                pagingData.map { event ->
                    event.copy(ownedByMe = event.authorId == token?.id)
                }
            }
        }.flowOn(Dispatchers.Default)

    private val _refreshAdapter = SingleLiveEvent<Unit>()
    val refreshAdapter: SingleLiveEvent<Unit>
        get() = _refreshAdapter

    private val _onLikeError = SingleLiveEvent<Int>()
    val onLikeError: LiveData<Int>
        get() = _onLikeError

    private val _onDeleteError = SingleLiveEvent<Unit>()
    val onDeleteError: LiveData<Unit>
        get() = _onDeleteError

    private val _requestSignIn = SingleLiveEvent<Unit>()
    val requestSignIn: LiveData<Unit>
        get() = _requestSignIn

    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Event>
        get() = _edited

    private val noAttachment = AttachmentModel()

    private val _attachment = MutableLiveData(noAttachment)
    val attachment: LiveData<AttachmentModel>
        get() = _attachment

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    fun playAudio(event: Event) {
        if (!event.isPlayingAudio) {
            try {
                viewModelScope.launch {
                    dao.makeAllIsNotPlaying()
                    dao.insert(EventEntity.fromDto(event.copy(isPlayingAudio = true)))
                    _refreshAdapter.value = Unit
                    if (event.isPlayingAudioPaused) {
                        dao.makeAllIsNotPaused()
                        MediaLifecycleObserver.mediaContinuePlay()
                    } else {
                        MediaLifecycleObserver.mediaStop()
                        event.attachment?.let { MediaLifecycleObserver.mediaPlay(it.url) }
                    }
                }
            } catch (e: Exception) {
                throw e
            }
        } else {
            try {
                viewModelScope.launch {
                    dao.makeAllIsNotPlaying()
                    dao.insert(
                        EventEntity.fromDto(
                            event.copy(
                                isPlayingAudioPaused = true,
                                isPlayingAudio = false
                            )
                        )
                    )
                    MediaLifecycleObserver.mediaPause()
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun clearPlayAudio() {
        try {
            viewModelScope.launch {
                dao.makeAllIsNotPaused()
                dao.makeAllIsNotPlaying()
                MediaLifecycleObserver.mediaStop()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun likeById(event: Event) {
        if (appAuth.authState.value?.id == 0) {
            _requestSignIn.value = Unit
        } else {
            viewModelScope.launch {
                try {
                    if (!event.likedByMe) {
                        repository.likeEventById(event)
                    } else {
                        repository.disLikeEventById(event)
                    }
                } catch (e: Exception) {
                    _onLikeError.value = event.id
                    throw e
                }

            }

        }
    }

    fun daoClearAll() {
        try {
            viewModelScope.launch {
                dao.clear()
                remoteDao.clear()

            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun updateAttachment(url: String?, uri: Uri?, file: File?, attachmentType: AttachmentType?) {
        _attachment.value =
            AttachmentModel(url = null, uri = uri, file = file, attachmentType = attachmentType)
    }

    fun clearAttachment() {
        _attachment.value = noAttachment
    }

    fun clearEdited() {
        _edited.value = empty
    }

    fun changeContent(content: String, link: String) {
        val text = content.trim()
        val web = link.trim()
        if (_attachment.value == noAttachment) {
            _edited.value = edited.value?.copy(
                content = text, link = web, attachment = null //todo to think about it
            )
        }
        if (edited.value?.content == text && edited.value?.link == web) {
            return
        }
        _edited.value = edited.value?.copy(
            content = text, link = web
        )
    }

    fun save() {
        edited.value?.let {
            viewModelScope.launch {
                try {
                    _attachment.value?.file?.let { file ->
                        _attachment.value!!.attachmentType?.let { type ->
                            repository.saveEventWithAttachment(
                                it,
                                MediaUpload(file),
                                type
                            )
                        }
                    } ?: repository.saveEvent(it)
                    _edited.value = empty
                    _attachment.value = noAttachment
                    _eventCreated.value = Unit

                } catch (e: Exception) {
                    throw e
                }
            }
        }
    }

    fun removeEventById(id: Int) {

        viewModelScope.launch {
            try {
                repository.removeEventById(id)
            } catch (e: Exception) {
                _onDeleteError.value = Unit
            }
        }
    }

    fun edit(event: Event) {
        _edited.value = event
    }

    fun setTypeOfEventOnline() {
        _edited.value = _edited.value?.copy(type = EventType.ONLINE)
    }

    fun setTypeOfEventOffline() {
        _edited.value = _edited.value?.copy(type = EventType.OFFLINE)
    }

    fun changeEventDateTime(utc: String) {
        _edited.value = _edited.value?.copy(datetime = utc)
    }


}