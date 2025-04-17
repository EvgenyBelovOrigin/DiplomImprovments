package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.EventDao
import ru.netology.nework.dao.EventRemoteKeyDao
import ru.netology.nework.dao.JobDao
import ru.netology.nework.dao.PostDao
import ru.netology.nework.dao.PostRemoteKeyDao
import ru.netology.nework.dao.UserDao
import ru.netology.nework.dao.WallDao
import ru.netology.nework.dao.WallRemoteKeyDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.Media
import ru.netology.nework.dto.MediaUpload
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.JobEntity
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.entity.WallEntity
import ru.netology.nework.entity.toDto
import ru.netology.nework.entity.toEntity
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.RunTimeError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val appAuth: AppAuth,
    private val dao: PostDao,
    private val wallDao: WallDao,
    private val usersDao: UserDao,
    private val jobDao: JobDao,
    private val eventDao: EventDao,
    postRemoteKeyDao: PostRemoteKeyDao,
    eventRemoteKeyDao: EventRemoteKeyDao,
    private val wallRemoteKeyDao: WallRemoteKeyDao,
    private val appDb: AppDb,

    ) : Repository {

    @OptIn(ExperimentalPagingApi::class)
    override val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { dao.getPagingSource() },
        remoteMediator = PostRemoteMediator(
            apiService = apiService,
            dao = dao,
            postRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb
        )
    ).flow
        .map { it.map(PostEntity::toDto) }

    override suspend fun getPosts() {
        try {
            val response = apiService.getPosts()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

        } catch (e: IOException) {
            throw NetworkError
        }

    }

    override suspend fun signIn(login: String, password: String) {
        try {
            val response = apiService.signIn(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            appAuth.setAuth(body)


        } catch (e: RuntimeException) {
            throw RunTimeError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun signUp(login: String, password: String, name: String) {
        try {
            val response = apiService.signUp(login, password, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            appAuth.setAuth(body)


        } catch (e: RuntimeException) {
            throw RunTimeError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun signUpWithAvatar(
        login: String,
        password: String,
        name: String,
        upload: MediaUpload
    ) {
        try {
            val response = apiService.signUpWithAvatar(
                login.toRequestBody("text/plain".toMediaType()),
                password.toRequestBody("text/plain".toMediaType()),
                name.toRequestBody("text/plain".toMediaType()),
                MultipartBody.Part.createFormData(
                    "file",
                    upload.file.name,
                    upload.file.asRequestBody()
                ),
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            appAuth.setAuth(body)

        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun disLikeById(post: Post) {
        try {
            val response = apiService.disLikeById(post.id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            if (response.body() != null) {
                dao.insert(
                    PostEntity.fromDto(
                        response.body()!!
                            .copy(
                                isPlayingAudioPaused = post.isPlayingAudioPaused,
                                isPlayingAudio = post.isPlayingAudio
                            )
                    )
                )
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(post: Post) {
        try {
            val response = apiService.likeById(post.id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())

            }
            if (response.body() != null) {
                dao.insert(
                    PostEntity.fromDto(
                        response.body()!!.copy(
                            isPlayingAudio = post.isPlayingAudio,
                            isPlayingAudioPaused = post.isPlayingAudioPaused
                        )
                    )
                )
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(
        post: Post,
        upload: MediaUpload,
        attachmentType: AttachmentType
    ) {
        try {
            val media = upload(upload)
            val postWithAttachment =
                post.copy(attachment = Attachment(media.url, attachmentType))
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()

            )

            val response = apiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removePostById(id: Int) {

        try {
            dao.removePostById(id)
            val response = apiService.removePostById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    // EVENTS

    @OptIn(ExperimentalPagingApi::class)
    override val events = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { eventDao.getPagingSource() },
        remoteMediator = EventRemoteMediator(
            apiService = apiService,
            dao = eventDao,
            eventRemoteKeyDao = eventRemoteKeyDao,
            appDb = appDb
        )
    ).flow
        .map { it.map(EventEntity::toDto) }


    override suspend fun disLikeEventById(event: Event) {
        try {
            val response = apiService.disLikeEventById(event.id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            if (response.body() != null) {
                eventDao.insert(
                    EventEntity.fromDto(
                        response.body()!!
                            .copy(
                                isPlayingAudioPaused = event.isPlayingAudioPaused,
                                isPlayingAudio = event.isPlayingAudio
                            )
                    )
                )
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeEventById(event: Event) {
        try {
            val response = apiService.likeEventById(event.id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())

            }
            if (response.body() != null) {
                eventDao.insert(
                    EventEntity.fromDto(
                        response.body()!!.copy(
                            isPlayingAudio = event.isPlayingAudio,
                            isPlayingAudioPaused = event.isPlayingAudioPaused
                        )
                    )
                )
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    override suspend fun saveEvent(event: Event) {
        try {
            val response = apiService.saveEvent(event)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveEventWithAttachment(
        event: Event,
        upload: MediaUpload,
        attachmentType: AttachmentType
    ) {
        try {
            val media = upload(upload)
            val eventWithAttachment =
                event.copy(attachment = Attachment(media.url, attachmentType))
            saveEvent(eventWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun removeEventById(id: Int) {

        try {
            eventDao.removeEventById(id)
            val response = apiService.removeEventById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    //USERS

    override val users = usersDao.getAll()
        .map(List<UserEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override val checkedUsers = usersDao.getCheckedUsers()
        .map(List<UserEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getAllUsers() {
        try {
            val response = apiService.getAllUsers()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            usersDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    //WALL

    var authorId: Int = 0
    override lateinit var wall: Flow<PagingData<Post>>


    @OptIn(ExperimentalPagingApi::class)
    override suspend fun setAuthorId(id: Int) {
        this.authorId = id
        wall = Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { wallDao.getPagingSource() },
            remoteMediator = WallRemoteMediator(
                apiService = apiService,
                dao = wallDao,
                wallRemoteKeyDao = wallRemoteKeyDao,
                appDb = appDb,
                authorId = authorId
            )
        ).flow.map { it.map(WallEntity::toDto) }
    }


    override suspend fun disLikeByIdWall(authorId: Int, post: Post) {

        try {
            val response = apiService.disLikeByIdWall(authorId, post.id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            if (response.body() != null) {
                wallDao.insert(
                    WallEntity.fromDto(
                        response.body()!!
                            .copy(
                                isPlayingAudioPaused = post.isPlayingAudioPaused,
                                isPlayingAudio = post.isPlayingAudio
                            )
                    )
                )
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeByIdWall(authorId: Int, post: Post) {
        try {
            val response = apiService.likeByIdWall(authorId, post.id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())

            }
            if (response.body() != null) {
                wallDao.insert(
                    WallEntity.fromDto(
                        response.body()!!.copy(
                            isPlayingAudio = post.isPlayingAudio,
                            isPlayingAudioPaused = post.isPlayingAudioPaused
                        )
                    )
                )
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    //JOBS

    override val jobs = jobDao.getAll()
        .map(List<JobEntity>::toDto)
        .flowOn(Dispatchers.Default)


    override suspend fun getAllJobs(userId: Int) {
        try {
            jobDao.clear()
            val response = apiService.getUserJobs(userId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun addJob(job: Job) {
        try {
            val response = apiService.addJob(job)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}




