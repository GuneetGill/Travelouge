package com.example.travelogue.table_country

import com.example.travelogue.table_journal.Journal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CountryRepository(private val countryDao: CountryDao) {
    //val allCountries: Flow<List<Country>> = countryDao.getAllCountries()

    fun insertCountry(country: Country) {
        CoroutineScope(IO).launch {
            countryDao.insertCountry(country)
        }
    }

    suspend fun getCountriesByUserId(userId: Long): List<Country> {
        return withContext(Dispatchers.IO) {
            val countries = countryDao.getAllCountries(userId)
            countries
        }
    }

    fun deleteCountry(country: Country) {
        CoroutineScope(IO).launch {
            countryDao.deleteCountry(country)
        }
    }
}