package com.example.travelogue.table_journal

class JournalRepository(private val journalDao: JournalDao) {
    suspend fun addJournal(journal: Journal) {
        journalDao.insertJournal(journal)
    }

    suspend fun getJournalsByCountry(countryId: Long): List<Journal> {
        return journalDao.getJournalsByCountry(countryId)
    }

    suspend fun deleteJournal(journal: Journal) {
        journalDao.deleteJournal(journal)
    }
}