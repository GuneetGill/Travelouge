package com.example.travelogue.db_user

import androidx.lifecycle.*
import java.lang.IllegalArgumentException


class UserViewModel(private val repository: UserRepository) : ViewModel() {
    val allTravelsLiveData: LiveData<List<User>> = repository.allComments.asLiveData()

    fun insertUser(user: User) {
        repository.insertUser(user)
    }


}

class UserViewModelFactory (private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(UserViewModel::class.java))
            return UserViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
