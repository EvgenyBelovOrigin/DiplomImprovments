package ru.netology.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nework.dao.EventDao
import ru.netology.nework.dao.EventRemoteKeyDao
import ru.netology.nework.dao.PostDao
import ru.netology.nework.dao.PostRemoteKeyDao
import ru.netology.nework.dao.UserDao
import ru.netology.nework.dao.WallDao
import ru.netology.nework.dao.WallRemoteKeyDao
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.EventRemoteKeyEntity
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.entity.PostRemoteKeyEntity
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.entity.WallEntity
import ru.netology.nework.entity.WallRemoteKeyEntity


@Database(
    entities = [PostEntity::class,
        PostRemoteKeyEntity::class,
        UserEntity::class,
        EventEntity::class,
        EventRemoteKeyEntity::class,
        WallRemoteKeyEntity::class,
        WallEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Convertors::class)

abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun userDao(): UserDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun eventDao(): EventDao
    abstract fun wallRemoteKeyDao(): WallRemoteKeyDao
    abstract fun wallDao(): WallDao
}