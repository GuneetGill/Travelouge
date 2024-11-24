package com.example.travelogue.table_folder

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FolderRepository(private val folderDao: FolderDao) {
    val allFolder: Flow<List<Folder>> = folderDao.getAllFolders()

    fun insertFolder(folder: Folder) {
        CoroutineScope(IO).launch {
            folderDao.insertFolder(folder)
        }
    }
//
//    suspend fun getFoldersByUser(userId: Long): LiveData<List<Folder>> {
//        return folderDao.getFoldersByUser(userId)
//    }

    fun deleteFolder(folder: Folder) {
        CoroutineScope(IO).launch {
            folderDao.deleteFolder(folder)
        }
    }
}