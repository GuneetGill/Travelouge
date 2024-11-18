package com.example.travelogue.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Folder::class], version = 1)
abstract class TravelogueDatabase :
    RoomDatabase() { //XD: Room automatically generates implementations of your abstract CommentDatabase class.
    abstract val folderDao: FolderDao

    companion object {
        //The Volatile keyword guarantees visibility of changes to the INSTANCE variable across threads
        @Volatile
        private var INSTANCE: TravelogueDatabase? = null

        fun getInstance(context: Context): TravelogueDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TravelogueDatabase::class.java, "folder_table"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}