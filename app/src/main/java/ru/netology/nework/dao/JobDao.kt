package ru.netology.nework.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.entity.JobEntity

@Dao
interface JobDao {

    @Query("SELECT * FROM JobEntity ORDER BY id")
    fun getAll(): Flow<List<JobEntity>>


    @Query("DELETE FROM JobEntity")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jobs: List<JobEntity>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(user: UserEntity)
//
//    @Query("UPDATE UserEntity SET isChecked = 0")
//    suspend fun makeAllUsersUnchecked()
//
//    @Query("SELECT * FROM UserEntity WHERE isChecked=1")
//    fun getCheckedUsers(): Flow<List<UserEntity>>
//
//
//    @Query(
//        """
//        UPDATE UserEntity SET
//        isChecked = 1
//        WHERE id = :id
//        """
//    )
//    suspend fun checkById(id: Int)
}
