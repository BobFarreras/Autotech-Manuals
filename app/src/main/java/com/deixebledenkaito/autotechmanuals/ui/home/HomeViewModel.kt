package com.deixebledenkaito.autotechmanuals.ui.home



import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.service.AuthService
import com.deixebledenkaito.autotechmanuals.data.service.ManualService
import com.deixebledenkaito.autotechmanuals.data.service.UserService
import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.User
import com.deixebledenkaito.autotechmanuals.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch

import javax.inject.Inject

// ui/viewmodel/HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val manualService: ManualService,
    private val userService: UserService,
    private val authService: AuthService
) : ViewModel() {

    private val _manuals = MutableStateFlow<List<Manuals>>(emptyList())
    val manuals: StateFlow<List<Manuals>> get() = _manuals

    private val _topManuals = MutableStateFlow<List<Manuals>>(emptyList())
    val topManuals: StateFlow<List<Manuals>> get() = _topManuals

    private val _lastManual = MutableStateFlow<Manuals?>(null)
    val lastManual: StateFlow<Manuals?> get() = _lastManual

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    // Càrrega de totes les dades
    fun loadAllData() {
        viewModelScope.launch {
            loadManuals()
            loadTopManuals()
            loadLastManual()
            loadUser()
        }
    }

    // Càrrega dels manuals
    private fun loadManuals() {
        viewModelScope.launch {
            when (val result = manualService.totsElsManuals()) {
                is Result.Success -> _manuals.value = result.data
                is Result.Error -> Log.e("HomeViewModel", result.message)
            }
        }
    }

    // Càrrega dels manuals destacats
    private fun loadTopManuals() {
        viewModelScope.launch {
            when (val result = manualService.getTopManuals()) {
                is Result.Success -> {
                    val topManualIds = result.data
                    val topManuals = topManualIds.mapNotNull { id ->
                        when (val manualResult = manualService.getManualByName(id)) {
                            is Result.Success -> manualResult.data
                            is Result.Error -> {
                                Log.e("HomeViewModel", "Error carregant manual: ${manualResult.message}")
                                null
                            }
                        }
                    }
                    _topManuals.value = topManuals
                    Log.d("HomeViewModel", "Top manuals carregats")
                }
                is Result.Error -> Log.e("HomeViewModel", result.message)
            }
        }
    }


    // Càrrega de l'últim manual utilitzat
    private fun loadLastManual() {
        viewModelScope.launch {
            when (val result = manualService.getLastUsedManual()) {
                is Result.Success -> {
                    val lastManualName = result.data
                    if (lastManualName != null) {
                        when (val manualResult = manualService.getManualByName(lastManualName)) {
                            is Result.Success -> _lastManual.value = manualResult.data
                            is Result.Error -> Log.e("HomeViewModel", manualResult.message)
                        }
                    }
                }
                is Result.Error -> Log.e("HomeViewModel", result.message)
            }
        }
    }

    // Càrrega de l'usuari
    private fun loadUser() {
        viewModelScope.launch {
            when (val result = userService.getUser()) {
                is Result.Success -> _user.value = result.data
                is Result.Error -> Log.e("HomeViewModel", result.message)
            }
        }
    }

    // Tancament de sessió
    fun logout(navigationToLogin: () -> Unit) {
        viewModelScope.launch {
            authService.logout()
            navigationToLogin()
        }
    }

    // Increment de l'ús del manual
    fun incrementManualUsage(manualId: String) {
        viewModelScope.launch {
            when (val result = manualService.incrementManualUsage(manualId)) {
                is Result.Success -> Log.e("HomeViewModel", "Em sumat")
                is Result.Error -> Log.e("HomeViewModel", result.message)
            }
        }
    }

    // Actualització de l'últim manual utilitzat
    fun updateLastUsedManual(manualName: String) {
        viewModelScope.launch {
            when (val result = manualService.updateLastUsedManual(manualName)) {
                is Result.Success -> loadLastManual()
                is Result.Error -> Log.e("HomeViewModel", result.message)
            }
        }
    }
}