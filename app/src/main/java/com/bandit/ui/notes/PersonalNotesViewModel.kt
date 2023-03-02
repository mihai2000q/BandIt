package com.bandit.ui.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.data.model.Note
import com.bandit.data.repository.NoteRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class PersonalNotesViewModel : ViewModel() {
    private val _repository = NoteRepository(DILocator.getDatabase())
    private val _notes = MutableLiveData(_repository.list)
    val notes: LiveData<List<Note>> = _notes
    val selectedNote = MutableLiveData<Note>()
    suspend fun addNote(note: Note) = coroutineScope {
        launch { _repository.add(note) }.join()
        _notes.value = _repository.list
    }
    suspend fun removeNote(note: Note) = coroutineScope {
        launch { _repository.remove(note) }.join()
        _notes.value = _repository.list
    }
    suspend fun editNote(note: Note) = viewModelScope.launch {
        launch { _repository.edit(note) }.join()
        _notes.value = _repository.list
    }
    companion object {
        const val TAG = Constants.PersonalNotes.VIEW_MODEL_TAG
    }
}
