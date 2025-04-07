package ru.netology.nework.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Query("UPDATE PostEntity SET isPlayingAudio = 0")
    suspend fun makeAllIsNotPlaying()

    @Query("UPDATE PostEntity SET isPlayingAudioPaused = 0")
    suspend fun makeAllIsNotPaused()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Query("DELETE FROM PostEntity")
    suspend fun clear()

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, PostEntity>

    @Query("SELECT COUNT(*) FROM PostEntity")
    suspend fun count(): Int

}
