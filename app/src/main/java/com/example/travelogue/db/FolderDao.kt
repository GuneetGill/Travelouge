package com.example.travelogue.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface FolderDao {

    @Insert
    suspend fun insertTravel(folder: Folder)

    @Query("SELECT * FROM folder_table")
    fun getAllFolders(): Flow<List<Folder>>

//    @Query("DELETE FROM folder_table")
//    suspend fun deleteAll()
//
//    @Query("DELETE FROM folder_table WHERE folder_id = :key") //":" indicates that it is a Bind variable
//    suspend fun deleteFolder(key: Long)
}