package ru.netology.nework.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.insertSeparators
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
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.UserAvatar
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.model.AttachmentModel
import ru.netology.nework.repository.Repository
import ru.netology.nework.utils.MediaLifecycleObserver
import ru.netology.nework.utils.SingleLiveEvent
import ru.netology.nmedia.auth.AppAuth
import java.io.File
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WallViewModel @Inject constructor(
    private val repository: Repository,
    private val appAuth: AppAuth,
    private val dao: PostDao,
    private val remoteDao: PostRemoteKeyDao,

    ) : ViewModel() {
    //POSTS

    val data: Flow<PagingData<FeedItem>> = appAuth.authState
        .flatMapLatest { token ->
            repository.wall.map { cashed ->
                cashed.insertSeparators { previous, next ->
                    if (previous?.id == 0) {
                        UserAvatar(1, "")
                    } else {
                        null
                    }
                }
            }.map { pagingData ->
                pagingData.map { post ->
                    if (post is Post) {
                        post.copy(ownedByMe = post.authorId == token?.id)
                    } else {
                        post
                    }
                }
            }
        }.flowOn(Dispatchers.Default)


//    val data: Flow<PagingData<FeedItem>> = appAuth.authState
//        .flatMapLatest { token ->
//            repository.posts.map { cashed ->
//                cashed.insertSeparators { previous, next ->
//                    takeDateSeparator.createSeparator(
//                        if (previous is Post) previous else null,
//                        if (next is Post) next else null
//                    )?.let { return@insertSeparators it }
//
//                    if (previous?.id?.rem(5) == 0L) {
//                        Ad(Random.nextLong(), "figma.jpg")
//                    } else {
//                        null
//                    }
//                }
//            }
//                .map { pagingData ->
//                    pagingData.map { post ->
//                        if (post is Post) {
//                            post.copy(ownedByMe = post.authorId == token?.id)
//                        } else {
//                            post
//                        }
//                    }
//                }
//        }.flowOn(Dispatchers.Default)

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


    private val noAttachment = AttachmentModel()

    private val _attachment = MutableLiveData(noAttachment)
    val attachment: LiveData<AttachmentModel>
        get() = _attachment


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

    fun likeById(authorId: Int, post: Post) {
        if (appAuth.authState.value?.id == 0) {
            _requestSignIn.value = Unit
        } else {
            viewModelScope.launch {
                try {
                    if (!post.likedByMe) {
                        repository.likeByIdWall(authorId, post)
                    } else {
                        repository.disLikeByIdWall(authorId, post)
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

    fun updateAttachment(
        url: String?,
        uri: Uri?,
        file: File?,
        attachmentType: AttachmentType?
    ) {
        _attachment.value =
            AttachmentModel(
                url = null,
                uri = uri,
                file = file,
                attachmentType = attachmentType
            )
    }

    fun clearAttachment() {
        _attachment.value = noAttachment
    }

    fun setAuthorId(authorId: Int) {
        try {
            viewModelScope.launch {
                repository.setAuthorId(authorId)
            }
        } catch (e: Exception) {
            throw e
        }


    }

}