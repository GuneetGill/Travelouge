package com.example.travelogue.db_user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey




@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0L,

    @ColumnInfo(name = "user_first_name_col")
    var userFirstName: String = "",

    @ColumnInfo(name = "user_last_name_col")
    var userLastName: String = "",

    @ColumnInfo(name = "user_name")
    var userName: String = "",

    @ColumnInfo(name = "user_email")
    var userEmail: String = "",

    @ColumnInfo(name = "user_password")
    var userPassword: String = ""
)

