package com.example.travelogue.expense_table

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> get() = _expenses

    private val _expensesBySessionId = MutableLiveData<List<Expense>>()
    val expensesBySessionId: LiveData<List<Expense>> get() = _expensesBySessionId

    private val _expensesByCountryId = MutableLiveData<List<Expense>>()
    val expensesByCountryId: LiveData<List<Expense>> get() = _expensesByCountryId


    // Add an expense
    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.addExpense(expense)
        refreshExpenses(expense.journalId, expense.sessionId) // Refresh after adding
    }

    // Get expenses by countryId or sessionId
    fun getExpenses(countryId: Long?, sessionId: String?) {
        viewModelScope.launch {
            _expenses.value = repository.getExpenses(countryId, sessionId)
        }
    }

    // Get expenses by sessionId
    fun getExpensesBySessionId(sessionId: String) = viewModelScope.launch {
        _expensesBySessionId.value = repository.getExpensesBySessionId(sessionId)
    }

    // Get expenses by sessionId
    fun getExpensesByCountryId(countryId: Long) = viewModelScope.launch {
        _expensesByCountryId.value = repository.getExpensesByCountryId(countryId)
    }

    // Link expenses to a journal after journal is saved
    fun linkExpensesToJournal(journalId: Long, sessionId: String) = viewModelScope.launch {
        repository.linkExpensesToJournal(journalId, sessionId)
    }

    // Delete an expense
    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.deleteExpense(expense)
        refreshExpenses(expense.journalId, expense.sessionId) // Refresh after deleting
    }

    // Refresh expenses
    private fun refreshExpenses(journalId: Long?, sessionId: String?) {
        viewModelScope.launch {
            _expenses.value = repository.getExpenses(journalId, sessionId)
        }
    }
}



class ExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java))
            return ExpenseViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

