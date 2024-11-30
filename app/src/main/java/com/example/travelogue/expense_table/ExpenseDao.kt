package com.example.travelogue.expense_table

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expense_table WHERE (:countryId IS NULL OR country_id = :countryId) AND (:sessionId IS NULL OR session_id = :sessionId)")
    suspend fun getExpenses(countryId: Long?, sessionId: String?): List<Expense>

    @Query("UPDATE expense_table SET journal_id = :journalId WHERE session_id = :sessionId")
    suspend fun updateJournalIdForSession(journalId: Long, sessionId: String)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expense_table WHERE session_id = :sessionId")
    suspend fun getExpensesBySessionId(sessionId: String?): List<Expense>

    @Query("SELECT * FROM expense_table WHERE country_id = :countryId")
    suspend fun getExpensesByCountryId(countryId: Long?): List<Expense>

}


