package ru.netology.nework.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.entity.WallEntity

@Dao
interface WallDao {
    @Query("SELECT COUNT(*) == 0 FROM WallEntity")
    suspend fun isEmpty(): Boolean

    @Query("UPDATE WallEntity SET isPlayingAudio = 0")
    suspend fun makeAllIsNotPlaying()

    @Query("UPDATE WallEntity SET isPlayingAudioPaused = 0")
    suspend fun makeAllIsNotPaused()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<WallEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: WallEntity)

    @Query("DELETE FROM WallEntity")
    suspend fun clear()

    @Query("SELECT * FROM WallEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, WallEntity>

    @Query("SELECT COUNT(*) FROM WallEntity")
    suspend fun count(): Int

    @Query("DELETE FROM WallEntity WHERE id = :id")
    suspend fun removePostById(id: Int)

}
