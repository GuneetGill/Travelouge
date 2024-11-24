package com.example.travelogue.doc_table

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import java.lang.IllegalArgumentException

class DocumentViewModel(private val repository: DocumentRepository): ViewModel() {
    val allDocumentsLiveData: LiveData<List<Document>> = repository.allDocument.asLiveData()

    fun insertDocument(document: Document) {
        repository.insertDocument(document)
    }

    fun deleteDocument(document: Document) {
        repository.deleteDocument(document)
    }
}

class DocumentViewModelFactory (private val repository: DocumentRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(DocumentViewModel::class.java))
            return DocumentViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}