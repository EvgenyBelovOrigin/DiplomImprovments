package ru.netology.nework.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.UserEntity

@Dao
interface EventDao {

    @Query("DELETE FROM EventEntity")
    suspend fun clear()


    @Query("SELECT * FROM UserEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: EventEntity)
}
