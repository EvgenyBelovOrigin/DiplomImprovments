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
import ru.netology.nework.dao.PostDao
import ru.netology.nework.dao.PostRemoteKeyDao
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.MediaUpload
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.model.AttachmentModel
import ru.netology.nework.repository.Repository
import ru.netology.nework.utils.MediaLifecycleObserver
import ru.netology.nework.utils.SingleLiveEvent
import ru.netology.nmedia.auth.AppAuth
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorJob = null,
    authorAvatar = null,
    content = "",
    published = "2024-07-02T21:34:44.562Z",
    coords = null,
    link = null,
    mentionIds = emptyList(),
    mentionedMe = false,
    likeOwnerIds = emptyList(),
    likedByMe = false,
    attachment = null,
    users = emptyMap(),
    ownedByMe = false,
    isPlayingAudio = false,
    isPlayingAudioPaused = false

)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: Repository,
    private val appAuth: AppAuth,
    private val dao: PostDao,
    private val remoteDao: PostRemoteKeyDao,
) : ViewModel() {
    //POSTS

    val data: Flow<PagingData<Post>> = appAuth.authState
        .flatMapLatest { token ->
            repository.posts.map { pagingData ->
                pagingData.map { post ->
                    post.copy(ownedByMe = post.authorId == token?.id)
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

    val edited = MutableLiveData(empty)

    private val noAttachment = AttachmentModel()

    private val _attachment = MutableLiveData<AttachmentModel>(noAttachment)
    val attachment: LiveData<AttachmentModel>
        get() = _attachment

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    fun playAudio(post: Post) {
        if (!post.isPlayingAudio) {
            try {
                viewModelScope.launch {
                    dao.makeAllIsNotPlaying()
                    dao.insert(PostEntity.fromDto(post.copy(isPlayingAudio = true)))
                    _refreshAdapter.value = Unit
                    if (post.isPlayingAudioPaused) {
                        dao.makeAllIsNotPaused()
                        MediaLifecycleObserver.mediaContinuePlay()
                    } else {
                        MediaLifecycleObserver.mediaStop()
                        post.attachment?.let { MediaLifecycleObserver.mediaPlay(it.url) }
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
                        PostEntity.fromDto(
                            post.copy(
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

    fun likeById(post: Post) {
        if (appAuth.authState.value?.id == 0) {
            _requestSignIn.value = Unit
        } else {
            viewModelScope.launch {
                try {
                    if (!post.likedByMe) {
                        repository.likeById(post)
                    } else {
                        repository.disLikeById(post)
                    }
                } catch (e: Exception) {
                    _onLikeError.value = post.id
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
        edited.value = empty
    }

    fun changeContent(content: String, link: String) {
        val text = content.trim()
        val web = link.trim()
        if (_attachment.value == noAttachment) {
            edited.value = edited.value?.copy(
                content = text, link = web, attachment = null //todo to think about it
            )
        }
        if (edited.value?.content == text && edited.value?.link == web) {
            return
        }
        edited.value = edited.value?.copy(
            content = text, link = web
        )
    }

    fun save() {
        edited.value?.let {
            viewModelScope.launch {
                try {
                    _attachment.value?.file?.let { file ->
                        _attachment.value!!.attachmentType?.let { it1 ->
                            repository.saveWithAttachment(
                                it,
                                MediaUpload(file),
                                it1
                            )
                        }
                    } ?: repository.save(it)
                    edited.value = empty
                    _attachment.value = noAttachment
                    _postCreated.value = Unit

                } catch (e: Exception) {
                    throw e
                }
            }
        }
    }

    fun removePostById(id: Int) {

        viewModelScope.launch {
            try {
                repository.removePostById(id)
            } catch (e: Exception) {
                _onDeleteError.value = Unit
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }


}
