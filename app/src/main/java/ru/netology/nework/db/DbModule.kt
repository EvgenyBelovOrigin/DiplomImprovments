package ru.netology.nework.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.dao.EventDao
import ru.netology.nework.dao.EventRemoteKeyDao
import ru.netology.nework.dao.JobDao
import ru.netology.nework.dao.PostDao
import ru.netology.nework.dao.PostRemoteKeyDao
import ru.netology.nework.dao.UserDao
import ru.netology.nework.dao.WallDao
import ru.netology.nework.dao.WallRemoteKeyDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context,
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePostDao(
        appDb: AppDb,
    ): PostDao = appDb.postDao()

    @Provides
    fun providePostRemoteKeyDao(
        appDb: AppDb,
    ): PostRemoteKeyDao = appDb.postRemoteKeyDao()

    @Provides
    fun provideUserDao(
        appDb: AppDb,
    ): UserDao = appDb.userDao()

    @Provides
    fun provideJobDao(
        appDb: AppDb,
    ): JobDao = appDb.jobDao()

    @Provides
    fun provideEventRemoteKeyDao(
        appDb: AppDb,
    ): EventRemoteKeyDao = appDb.eventRemoteKeyDao()

    @Provides
    fun provideEventDao(
        appDb: AppDb,
    ): EventDao = appDb.eventDao()

    @Provides
    fun provideWallRemoteKeyDao(
        appDb: AppDb,
    ): WallRemoteKeyDao = appDb.wallRemoteKeyDao()

    @Provides
    fun provideWallDao(
        appDb: AppDb,
    ): WallDao = appDb.wallDao()
}