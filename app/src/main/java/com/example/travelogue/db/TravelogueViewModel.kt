package com.example.travelogue.db

import androidx.lifecycle.*
import java.lang.IllegalArgumentException


class TravelogueViewModel(private val repository: FolderRepository) : ViewModel() {
    val allTravelsLiveData: LiveData<List<Folder>> = repository.allComments.asLiveData()

    fun insertFolder(folder: Folder) {
        repository.insertFolder(folder)
    }

}

class TravelogueViewModelFactory (private val repository: FolderRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(TravelogueViewModel::class.java))
            return TravelogueViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
