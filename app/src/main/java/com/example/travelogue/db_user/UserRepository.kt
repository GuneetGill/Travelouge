package com.example.travelogue.db_user

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

//A Repository manages queries and allows you to use multiple backends.
// In the most common example, the Repository implements the logic for
// deciding whether to fetch data from a network or use results cached in a local database.
class UserRepository(private val userDao: UserDao) {

    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    fun insertUser(user: User) {
        CoroutineScope(IO).launch {
            userDao.insertUser(user)
        }
    }

}