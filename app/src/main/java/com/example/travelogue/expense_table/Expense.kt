package com.example.travelogue.expense_table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.travelogue.table_journal.Journal


@Entity(
    tableName = "expense_table",
    foreignKeys = [
        ForeignKey(
            entity = Journal::class, // Reference journal table
            parentColumns = ["journalId"], // Primary key in journal table
            childColumns = ["journal_id"], // Foreign key in expense table
            onDelete = ForeignKey.CASCADE, // Cascade delete
            onUpdate = ForeignKey.CASCADE // Cascade update
        )
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    var expenseId: Long = 0L,

    @ColumnInfo(name = "journal_id")
    val journalId: Long? = null, // Set after journal is saved

    @ColumnInfo(name = "session_id")
    val sessionId: String? = null, // Temporary identifier

    @ColumnInfo(name = "country_id")
    val countryId: Long? = null,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "amount")
    val amount: Float,

    @ColumnInfo(name = "currency")
    val currency: String
)
{
    // Empty constructor required by Room
    constructor() : this(
        journalId = null,
        sessionId = null,
        countryId = null,
        category = "",
        amount = 0f,
        currency = ""
    )
}