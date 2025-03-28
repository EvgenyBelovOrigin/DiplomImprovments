package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.PostDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.MediaUpload
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.PostEntity
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.error.ApiError
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
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,

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
        TODO("Not yet implemented")
    }


}


