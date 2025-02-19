package com.deixebledenkaito.autotechmanuals.ui.home


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService
import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.User

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseDataBaseService: FirebaseDataBaseService
) : ViewModel() {

    private val _manuals = MutableStateFlow<List<Manuals>>(emptyList())
    val manuals: StateFlow<List<Manuals>> get() = _manuals

    private val _topManuals = MutableStateFlow<List<Manuals>>(emptyList())
    val topManuals: StateFlow<List<Manuals>> get() = _topManuals

    private val _lastManual = MutableStateFlow<Manuals?>(null)
    val lastManual: StateFlow<Manuals?> get() = _lastManual

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    fun loadManuals() {
        viewModelScope.launch {
            _manuals.value = firebaseDataBaseService.totsElsManuals()
        }
    }

    fun loadTopManuals() {
        viewModelScope.launch {
            _topManuals.value = firebaseDataBaseService.topManuals()
        }
    }

    fun loadLastManual() {
        viewModelScope.launch {
            _lastManual.value = firebaseDataBaseService.ultimManual()
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            _user.value = firebaseDataBaseService.getUser() ?: User (
                id = "unknown",
                name = "Usuari",
                profileImageUrl = ""
            )
        }
    }

    fun addManual(nom: String, descripcio: String, imageUri: Uri) {
        viewModelScope.launch {
            val imageUrl = firebaseDataBaseService.uploadAndDownloadImage(imageUri)
            if (imageUrl.isNotEmpty()) {
                val success = firebaseDataBaseService.afegirManual(nom, descripcio, imageUrl)
                if (success) {
                    loadManuals() // Recarregar la llista de manuals
                }
            }
        }
    }

    fun logout() {
        firebaseDataBaseService.logout()
    }
}