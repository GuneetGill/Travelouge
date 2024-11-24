package com.example.travelogue.doc_table
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DocumentRepository(private val documentDao: DocumentDao) {
    val allDocument: Flow<List<Document>> = documentDao.getAllDocuments()

    fun insertDocument(document: Document) {
        CoroutineScope(IO).launch {
            documentDao.insertDocument(document)
        }
    }

     fun deleteDocument(document: Document) {
        CoroutineScope(IO).launch {
            documentDao.deleteDocument(document)
        }
    }
}
