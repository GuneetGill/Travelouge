package com.example.travelogue.db_user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM user_table WHERE userId = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?

    @Query("UPDATE user_table SET user_first_name_col = :firstName, user_last_name_col = :lastName, user_name = :username WHERE userId = :userId")
    suspend fun updateUserDetails(userId: Long, firstName: String, lastName: String, username: String)

}