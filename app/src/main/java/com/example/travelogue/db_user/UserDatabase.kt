package com.example.travelogue.db_user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.travelogue.table_country.Country
import com.example.travelogue.table_country.CountryDao
import com.example.travelogue.folder_table.Folder
import com.example.travelogue.folder_table.FolderDao
import com.example.travelogue.table_journal.Journal
import com.example.travelogue.table_journal.JournalDao
import com.example.travelogue.doc_table.Document
import com.example.travelogue.doc_table.DocumentDao

@Database(entities = [User::class, Folder::class, Country::class, Journal::class, Document::class], version = 3)
abstract class UserDatabase :
    RoomDatabase() {
    abstract val userDao: UserDao
    abstract val folderDao : FolderDao
    abstract val journalDao: JournalDao
    abstract val countryDao: CountryDao
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
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
