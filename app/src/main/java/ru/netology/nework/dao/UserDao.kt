package ru.netology.nework.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM UserEntity ORDER BY id")
    fun getAll(): Flow<List<UserEntity>>

    @Query("DELETE FROM UserEntity")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("UPDATE UserEntity SET isChecked = 0")
    suspend fun makeAllUsersUnchecked()

    @Query("SELECT * FROM UserEntity WHERE isChecked=1")
    fun getCheckedUsers(): Flow<List<UserEntity>>
}
