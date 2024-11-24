package com.example.travelogue.db_user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.travelogue.doc_table.Document
import com.example.travelogue.doc_table.DocumentDao
import com.example.travelogue.folder_table.Folder
import com.example.travelogue.folder_table.FolderDao

@Database(entities = [User::class, Folder::class, Document::class], version = 3)
abstract class UserDatabase :
    RoomDatabase() {
    abstract val userDao: UserDao
    abstract val folderDao : FolderDao
    abstract val documentDao : DocumentDao


    companion object {
        //The Volatile keyword guarantees visibility of changes to the INSTANCE variable across threads
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java, "user_table"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}