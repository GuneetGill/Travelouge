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

//    @Query("DELETE FROM folder_table")
//    suspend fun deleteAll()
//
//    @Query("DELETE FROM folder_table WHERE folder_id = :key") //":" indicates that it is a Bind variable
//    suspend fun deleteFolder(key: Long)
}