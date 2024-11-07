package com.example.travelogue.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folder_table")
data class Folder(
    @PrimaryKey(autoGenerate = true)
    var folder_id: Long = 0L,

    @ColumnInfo(name = "doc_folder")
    var folderName: String = ""
    )
