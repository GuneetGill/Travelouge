package com.example.travelogue.table_folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.travelogue.db_user.UserRepository
import com.example.travelogue.db_user.UserViewModel
import java.lang.IllegalArgumentException

class FolderViewModel(private val repository: FolderRepository): ViewModel() {
    val allFoldersLiveData: LiveData<List<Folder>> = repository.allFolder.asLiveData()

    fun insertFolder(folder: Folder) {
        repository.insertFolder(folder)
    }

    fun deleteFolder(folder: Folder) {
        repository.deleteFolder(folder)
    }
}

class FolderViewModelFactory (private val repository: FolderRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(FolderViewModel::class.java))
            return FolderViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}