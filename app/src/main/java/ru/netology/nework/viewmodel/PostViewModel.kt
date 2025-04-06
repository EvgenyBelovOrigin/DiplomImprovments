package ru.netology.nework.viewmodel

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
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.repository.Repository
import ru.netology.nework.utils.MediaLifecycleObserver
import ru.netology.nework.utils.SingleLiveEvent
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: Repository,
    private val appAuth: AppAuth,
    private val dao: PostDao
) : ViewModel() {

    private val _refreshAdapter = SingleLiveEvent<Unit>()
    val refreshAdapter: SingleLiveEvent<Unit>
        get() = _refreshAdapter

    val data: Flow<PagingData<Post>> = appAuth.authState
        .flatMapLatest { token ->
            repository.posts.map { pagingData ->
                pagingData.map { post ->
                    post.copy(ownedByMe = post.authorId == token?.id)
                }
            }
        }.flowOn(Dispatchers.Default)

    fun playAudio(post: Post) {
        if (!post.isPlayingAudio) {
            try {
                viewModelScope.launch {
                    dao.makeAllIsNotPlaying()
                    dao.insert(PostEntity.fromDto(post.copy(isPlayingAudio = true)))
                    if (post.isPlayingAudioPaused) {
                        dao.makeAllIsNotPaused()
                        MediaLifecycleObserver.mediaContinuePlay()
                        _refreshAdapter.value = Unit
                    } else {
                        MediaLifecycleObserver.mediaStop()
                        post.attachment?.let { MediaLifecycleObserver.mediaPlay(it.url) }
                        _refreshAdapter.value = Unit
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
                    _refreshAdapter.value = Unit
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

}
