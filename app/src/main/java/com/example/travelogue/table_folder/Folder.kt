package com.example.travelogue.table_folder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import com.example.travelogue.db_user.User

@Entity(
    tableName = "folder_table",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_owner_id_col"],
            onDelete = ForeignKey.CASCADE // Deletes folders when the user is deleted
        )
    ]
)

data class Folder(
    @PrimaryKey(autoGenerate = true)
    var folder_id: Long = 0L,

    @ColumnInfo(name = "user_owner_id_col")
    var userOwnerId: Long = 0L,

    @ColumnInfo(name = "folder_name_col")
    var folderName: String = "",

    @ColumnInfo
    val createdAt: Long = System.currentTimeMillis()
)