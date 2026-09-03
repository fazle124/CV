package com.example.cv.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cv.data.CvRepository
import com.example.cv.model.CvDocument
import com.example.cv.model.CvHeader
import com.example.cv.model.CvSection
import com.example.cv.model.DefaultCvData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CvTab {
    CV_VIEW,
    ADMIN_PANEL,
    DOCUMENTS
}

data class CvUiState(
    val header: CvHeader = DefaultCvData.defaultHeader,
    val sections: List<CvSection> = DefaultCvData.defaultSections,
    val documents: List<CvDocument> = DefaultCvData.defaultDocuments,
    val activeTab: CvTab = CvTab.CV_VIEW,
    val isAdminLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val statusNotification: String? = null
)

class CvViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CvRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(CvUiState())
    val uiState: StateFlow<CvUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val header = repository.getHeader()
            val sections = repository.getSections()
            val documents = repository.getDocuments()

            _uiState.update {
                it.copy(
                    header = header,
                    sections = sections,
                    documents = documents,
                    isLoading = false
                )
            }

            // Attempt background sync with remote server
            val (remoteHeader, remoteSections) = repository.syncRemoteData()
            if (remoteHeader != null || remoteSections != null) {
                _uiState.update { current ->
                    current.copy(
                        header = remoteHeader ?: current.header,
                        sections = remoteSections ?: current.sections
                    )
                }
            }
        }
    }

    fun setTab(tab: CvTab) {
        _uiState.update { it.copy(activeTab = tab, loginError = null) }
    }

    fun loginAdmin(phone: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loginError = null) }
            val result = repository.verifyAdminLogin(phone, pass)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isAdminLoggedIn = true,
                        isLoading = false,
                        loginError = null,
                        statusNotification = "Logged in successfully as Admin"
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAdminLoggedIn = false,
                        isLoading = false,
                        loginError = err.message ?: "Login failed. Check phone or password."
                    )
                }
            }
        }
    }

    fun logoutAdmin() {
        _uiState.update {
            it.copy(
                isAdminLoggedIn = false,
                statusNotification = "Logged out from Admin"
            )
        }
    }

    fun updateHeader(header: CvHeader) {
        viewModelScope.launch {
            repository.saveHeader(header)
            _uiState.update { it.copy(header = header, statusNotification = "Header updated locally") }
            // Background sync
            repository.syncSaveToRemote(header, _uiState.value.sections)
        }
    }

    fun updateSection(index: Int, title: String, body: String) {
        val current = _uiState.value.sections.toMutableList()
        if (index in current.indices) {
            val old = current[index]
            current[index] = old.copy(title = title, body = body)
            _uiState.update { it.copy(sections = current) }
        }
    }

    fun moveSection(index: Int, direction: Int) {
        val current = _uiState.value.sections.toMutableList()
        val targetIndex = index + direction
        if (index in current.indices && targetIndex in current.indices) {
            val item = current.removeAt(index)
            current.add(targetIndex, item)
            _uiState.update { it.copy(sections = current) }
        }
    }

    fun addSection() {
        val current = _uiState.value.sections.toMutableList()
        val newSection = CvSection(
            id = "sec_${System.currentTimeMillis()}",
            title = "NEW SECTION",
            body = "Write your content here."
        )
        current.add(newSection)
        _uiState.update { it.copy(sections = current, statusNotification = "New section added") }
    }

    fun deleteSection(index: Int) {
        val current = _uiState.value.sections.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _uiState.update { it.copy(sections = current, statusNotification = "Section removed") }
            viewModelScope.launch {
                repository.saveSections(current)
                repository.syncSaveToRemote(_uiState.value.header, current)
            }
        }
    }

    fun saveAllChanges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            repository.saveHeader(state.header)
            repository.saveSections(state.sections)
            val remoteSynced = repository.syncSaveToRemote(state.header, state.sections)

            val msg = if (remoteSynced) {
                "Saved locally & synced to Remote Database!"
            } else {
                "Saved locally! (Offline / Server unreachable)"
            }
            _uiState.update { it.copy(isLoading = false, statusNotification = msg) }
        }
    }

    fun resetCV() {
        viewModelScope.launch {
            repository.resetToDefault()
            _uiState.update {
                it.copy(
                    header = DefaultCvData.defaultHeader,
                    sections = DefaultCvData.defaultSections,
                    documents = DefaultCvData.defaultDocuments,
                    statusNotification = "CV reset to default profile"
                )
            }
        }
    }

    fun addDocument(name: String) {
        val current = _uiState.value.documents.toMutableList()
        val newDoc = CvDocument(
            id = "doc_${System.currentTimeMillis()}",
            name = name.ifBlank { "Custom Document" },
            dateAdded = "Verified",
            imageUri = null
        )
        current.add(newDoc)
        _uiState.update { it.copy(documents = current, statusNotification = "Document added") }
        viewModelScope.launch {
            repository.saveDocuments(current)
        }
    }

    fun clearNotification() {
        _uiState.update { it.copy(statusNotification = null) }
    }
}
