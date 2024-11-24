package com.example.travelogue.folder_table

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

    fun deleteFolder(folder: Folder) {
        CoroutineScope(IO).launch {
            folderDao.deleteFolder(folder)
        }
    }
}