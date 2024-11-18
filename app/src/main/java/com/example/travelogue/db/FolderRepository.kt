package com.example.travelogue.db

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

//A Repository manages queries and allows you to use multiple backends.
// In the most common example, the Repository implements the logic for
// deciding whether to fetch data from a network or use results cached in a local database.
class FolderRepository(private val folderDao: FolderDao) {

    val allComments: Flow<List<Folder>> = folderDao.getAllFolders()

    fun insertFolder(folder: Folder){
        CoroutineScope(IO).launch{
            folderDao.insertTravel(folder)
        }
    }

//    fun delete(id: Long){
//        CoroutineScope(IO).launch {
//            folderDao.deleteTravel(id)
//        }
//    }
//
//    fun deleteAll(){
//        CoroutineScope(IO).launch {
//            folderDao.deleteAll()
//        }
//    }
}