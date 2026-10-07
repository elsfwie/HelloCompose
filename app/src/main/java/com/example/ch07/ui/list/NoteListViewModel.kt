package com.example.ch07.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch07.data.NoteEntity
import com.example.ch07.data.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NoteListUiState(
    val isLoading: Boolean = false,
    val notes: List<NoteEntity> = emptyList(),
    val query: String = "",
    val recentlyDeleted: NoteEntity? = null,
    val availableTags: List<String> = emptyList(),
    val selectedTag: String? = null,
    val errorMessage: String? = null
) {
    val isFiltering: Boolean
        get() = query.isNotBlank() || selectedTag != null
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class NoteListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    // Teks yang sedang diketik (langsung tampil di TextField)
    private val queryText = MutableStateFlow("")
    private val loadError = MutableStateFlow<String?>(null)
    private val recentlyDeleted = MutableStateFlow<NoteEntity?>(null)

    private val selectedTag = MutableStateFlow<String?>(null)

    // Query ke database ditunda 300 ms setelah pengguna berhenti mengetik
    // (debounce), dan hanya query TERBARU yang diamati (flatMapLatest).
    // (debounce), dan hanya query TERBARU yang diamati (flatMapLatest).
    // Query kosong tidak ditunda: daftar penuh tampil segera.
    private val searchResults: Flow<List<NoteEntity>> = queryText
        .debounce { query -> if (query.isBlank()) 0L else 300L }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllNotes()
            else repository.searchNotes(query)
        }
        .catch { e ->
            loadError.value = e.message ?: "Gagal memuat catatan"
            emit(emptyList())
        }

    // UiState DITURUNKAN dari beberapa sumber (pola Chapter 6).
    private val tagState = combine(
        repository.getAllNotes(),
        selectedTag
    ) { allNotes, selected ->
        val available = allNotes
            .flatMap { it.tags }
            .distinct()
            .sorted()

        val active = selected?.takeIf { it in available }

        // Lepas filter jika tag sudah tidak digunakan.
        if (selected != null && active == null) {
            selectedTag.value = null
        }

        available to active
    }
    val uiState: StateFlow<NoteListUiState> = combine(
        searchResults,
        queryText,
        loadError,
        recentlyDeleted,
        tagState
    ) { results, query, error, deleted, tagInfo->
        val (available, active) = tagInfo

        val filteredNotes = if (active == null) {
            results
        } else {
            results.filter { active in it.tags }
        }

        NoteListUiState(
            notes = filteredNotes,
            query = query,
            errorMessage = error,
            recentlyDeleted = deleted,
            availableTags = available,
            selectedTag = active
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoteListUiState(isLoading = true)
    )

    fun onQueryChange(newQuery: String) {
        queryText.value = newQuery
    }
    fun onTagSelected(tag: String?) {
        selectedTag.value = if (selectedTag.value == tag) {
            null
        } else {
            tag
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
            recentlyDeleted.value = note
        }
    }

    fun undoDelete() {
        val note = recentlyDeleted.value ?: return

        recentlyDeleted.value = null

        viewModelScope.launch {
            repository.insertNote(note)
        }
    }

    fun onUndoDismissed() {
        recentlyDeleted.value = null
    }
    // TODO [T4.6] Tambahkan `onTagSelected(tag: String?)`: ketuk tag yang sedang aktif
    //   melepas filter; null = "Semua". Tag yang sudah tidak dipakai catatan mana
    //   pun harus otomatis dilepas.

    // Menyematkan tidak mengubah updatedAt (bukan perubahan isi catatan)
    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }
}
