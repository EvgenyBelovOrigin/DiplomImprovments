package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.WallDao
import ru.netology.nework.dao.WallRemoteKeyDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.entity.WallEntity
import ru.netology.nework.entity.WallRemoteKeyEntity
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class WallRemoteMediator @Inject constructor(
    private val apiService: ApiService,
    private val dao: WallDao,
    private val wallRemoteKeyDao: WallRemoteKeyDao,
    private val appDb: AppDb,
    private val authorId: Int
) : RemoteMediator<Int, WallEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, WallEntity>,
    ): MediatorResult {

        try {
            val result = when (loadType) {
                LoadType.APPEND -> {
                    val id = wallRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    apiService.getBeforeWall(authorId, id, state.config.pageSize)
                }

                LoadType.PREPEND -> {
                    val id = wallRemoteKeyDao.max() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    apiService.getAfterWall(authorId, id, state.config.pageSize)
                }

                LoadType.REFRESH -> apiService.getLatestWall(authorId, state.config.initialLoadSize)
            }
            if (!result.isSuccessful) {
                throw HttpException(result)
            }
            val data = result.body().orEmpty()

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {

                        wallRemoteKeyDao.clear()
                        wallRemoteKeyDao.insert(
                            listOf(
                                WallRemoteKeyEntity(
                                    type = WallRemoteKeyEntity.KeyType.AFTER,
                                    key = data.first().id,
                                ),
                                WallRemoteKeyEntity(
                                    type = WallRemoteKeyEntity.KeyType.BEFORE,
                                    key = data.last().id,
                                ),
                            )
                        )
                        dao.clear()

                    }

                    LoadType.PREPEND -> {
                        wallRemoteKeyDao.insert(
                            WallRemoteKeyEntity(
                                type = WallRemoteKeyEntity.KeyType.AFTER,
                                key = data.first().id,
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        wallRemoteKeyDao.insert(
                            WallRemoteKeyEntity(
                                WallRemoteKeyEntity.KeyType.BEFORE,
                                data.last().id
                            )
                        )
                    }

                }

                dao.insert(data.map { WallEntity.fromDto(it) })
            }
            return MediatorResult.Success(data.isEmpty())

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }

    }


}