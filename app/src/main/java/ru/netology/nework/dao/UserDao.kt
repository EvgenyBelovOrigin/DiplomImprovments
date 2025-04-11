package ru.netology.nework.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.entity.UserEntity

@Dao
interface UserDao {

    @Query("DELETE FROM UserEntity")
    suspend fun clear()


    @Query("SELECT * FROM UserEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)
}
