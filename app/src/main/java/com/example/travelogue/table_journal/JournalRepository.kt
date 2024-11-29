package com.example.travelogue.table_journal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JournalRepository(private val journalDao: JournalDao) {

    suspend fun addJournal(journal: Journal) {
        CoroutineScope(IO).launch {
            journalDao.insertJournal(journal)
        }
    }

    suspend fun getJournalsByCountry(countryId: Long): List<Journal> {
        return withContext(Dispatchers.IO) {
            val journals = journalDao.getJournalsByCountry(countryId)
            journals
        }
    }

    suspend fun deleteJournal(journal: Journal) {
        CoroutineScope(IO).launch {
            journalDao.deleteJournal(journal)
        }
    }
}