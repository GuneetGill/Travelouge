package com.example.travelogue.db_user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.travelogue.table_folder.Folder
import com.example.travelogue.table_folder.FolderDao

@Database(entities = [User::class, Folder::class], version = 2)
abstract class UserDatabase :
    RoomDatabase() {
    abstract val userDao: UserDao
    abstract val folderDao : FolderDao


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