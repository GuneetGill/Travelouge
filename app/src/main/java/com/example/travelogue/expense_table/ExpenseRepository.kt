package com.example.travelogue.expense_table

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // Add an expense
    suspend fun addExpense(expense: Expense) {
        withContext(Dispatchers.IO) {
            expenseDao.insertExpense(expense)
        }
    }

    suspend fun getExpensesBySessionId(sessionId: String?): List<Expense> {
        return withContext(Dispatchers.IO) {
        expenseDao.getExpensesBySessionId(sessionId)}
    }

    suspend fun getExpensesByCountryId(countryId: Long?): List<Expense> {
        return withContext(Dispatchers.IO) {
            expenseDao.getExpensesByCountryId(countryId)}
    }

    // Get expenses by countryId or sessionId
    suspend fun getExpenses(countryId: Long?, sessionId: String?): List<Expense> {
        return withContext(Dispatchers.IO) {
            expenseDao.getExpenses(countryId, sessionId)
        }
    }

    // Link expenses to a journal after journal is saved
    suspend fun linkExpensesToJournal(journalId: Long, sessionId: String) {
        withContext(Dispatchers.IO) {
            expenseDao.updateJournalIdForSession(journalId, sessionId)
        }
    }

    // Delete an expense
    suspend fun deleteExpense(expense: Expense) {
        withContext(Dispatchers.IO) {
            expenseDao.deleteExpense(expense)
        }
    }


}

