package ru.netology.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nework.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.entity.PostRemoteKeyEntity


@Database(
    entities = [PostEntity::class, PostRemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}