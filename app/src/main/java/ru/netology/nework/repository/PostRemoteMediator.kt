package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.nework.api.ApiService
import ru.netology.nework.db.AppDb
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nework.entity.PostRemoteKeyEntity
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val apiService: ApiService,
    private val dao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>,
    ): MediatorResult {

        try {
            val result = when (loadType) {
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBefore(id, state.config.pageSize)
                }

                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.REFRESH -> {
                    val id = postRemoteKeyDao.max()
                    if (id != null) {
                        apiService.getNewer(id)
                    } else {
                        apiService.getLatest(state.config.pageSize)
                    }
                }
            }
            if (!result.isSuccessful) {
                throw HttpException(result)
            }
            val data = result.body().orEmpty()

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {

                        if (dao.isEmpty()) {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.BEFORE,
                                    data.last().id
                                )
                            )

                        }
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                data.first().id
                            )
                        )

                    }

                    LoadType.PREPEND -> Unit

                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.BEFORE,
                                data.last().id
                            )
                        )
                    }

                }

                dao.insert(data.map { PostEntity.fromDto(it) })
            }
            return MediatorResult.Success(data.isEmpty())

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }

    }
}