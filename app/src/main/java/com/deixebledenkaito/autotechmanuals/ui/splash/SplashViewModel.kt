package com.deixebledenkaito.autotechmanuals.ui.splash

import androidx.lifecycle.ViewModel
import com.deixebledenkaito.autotechmanuals.data.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val authService: AuthService) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination

    init {
        checkDestination()
    }

    private fun isUserLogged(): Boolean {
        return authService.isUserLogged()
    }

    private fun checkDestination() {
        _destination.value = if (isUserLogged()) {
            SplashDestination.Home
        } else {
            SplashDestination.Login
        }
    }
}

// Definició de les destinacions de la pantalla Splash
sealed class SplashDestination {
    data object Login : SplashDestination()
    data object Home : SplashDestination()
}
