package com.example.travelogue.table_journal


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "journal",
    foreignKeys = [
        ForeignKey(
            entity = Country::class, // Reference the Country table
            parentColumns = ["countryId"], // Primary key in Country table
            childColumns = ["countryId"], // Foreign key in Journal table
            onDelete = ForeignKey.CASCADE // Cascade delete to remove journals when a country is deleted
        )
    ]
)
data class Journal(
    @PrimaryKey(autoGenerate = true)
    val journalId: Long = 0, // Primary key for journal entries

    @ColumnInfo(name = "countryId")
    val countryId: Long, // Foreign key referencing Country table

    @ColumnInfo(name = "title")
    val title: String, // Title of the journal entry

    @ColumnInfo(name = "content")
    val content: String, // Content of the journal entry

    @ColumnInfo(name = "rating")
    val rating: Float, // Rating for the trip

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "photoUri")
    val photoUri: String? = null
)
