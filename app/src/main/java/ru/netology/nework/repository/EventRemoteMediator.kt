package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.EventDao
import ru.netology.nework.dao.EventRemoteKeyDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.EventRemoteKeyEntity
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator @Inject constructor(
    private val apiService: ApiService,
    private val dao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>,
    ): MediatorResult {

        try {
            val result = when (loadType) {
                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    apiService.getEventsBefore(id, state.config.pageSize)
                }

                LoadType.PREPEND -> {
                    val id = eventRemoteKeyDao.max() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    apiService.getEventsAfter(id, state.config.pageSize)
                }

                LoadType.REFRESH -> apiService.getEventsLatest(state.config.initialLoadSize)
            }
            if (!result.isSuccessful) {
                throw HttpException(result)
            }
            val data = result.body().orEmpty()

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {

                        eventRemoteKeyDao.clear()
                        eventRemoteKeyDao.insert(
                            listOf(
                                EventRemoteKeyEntity(
                                    type = EventRemoteKeyEntity.KeyType.AFTER,
                                    key = data.first().id,
                                ),
                                EventRemoteKeyEntity(
                                    type = EventRemoteKeyEntity.KeyType.BEFORE,
                                    key = data.last().id,
                                ),
                            )
                        )
                        dao.clear()

                    }

                    LoadType.PREPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                type = EventRemoteKeyEntity.KeyType.AFTER,
                                key = data.first().id,
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.KeyType.BEFORE,
                                data.last().id
                            )
                        )
                    }

                }

                dao.insert(data.map { EventEntity.fromDto(it) })
            }
            return MediatorResult.Success(data.isEmpty())

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }

    }


}