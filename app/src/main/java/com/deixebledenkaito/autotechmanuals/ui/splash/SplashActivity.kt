package com.deixebledenkaito.autotechmanuals.ui.splash


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier




import androidx.compose.ui.unit.dp

import com.deixebledenkaito.autotechmanuals.ui.home.HomeActivity

import com.deixebledenkaito.autotechmanuals.ui.auth.login.LoginActivity

import dagger.hilt.android.AndroidEntryPoint
import org.opencv.android.OpenCVLoader

import androidx.hilt.navigation.compose.hiltViewModel as hiltViewModel1

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Inicialitza OpenCV
            if (!OpenCVLoader.initDebug()) {
                Log.e("OpenCV", "Error en inicialitzar OpenCV")
            } else {
                Log.d("OpenCV", "OpenCV inicialitzat correctament")
            }
            SplashScreen(
                onNavigateToHome = { navigateToHome() },
                onNavigateToLogin = { navigateToLogin() },
                extras = intent.extras
            )
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

@Composable

fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    extras: Bundle?
) {
    val splashViewModel: SplashViewModel = hiltViewModel1()
    val destination by splashViewModel.destination.collectAsState(initial = SplashDestination.Login)

    // Navegación automática
    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.Home -> onNavigateToHome()
            SplashDestination.Login -> onNavigateToLogin()
            null -> onNavigateToLogin()
        }
    }

    // Llegir dades dels extras
    val example1 = extras?.getString("example1") ?: "N/A"
    val example2 = extras?.getString("example2") ?: "N/A"

    Log.i("notific", "El valor de example 1: $example1")
    Log.i("notific", "El valor de example 2: $example2")

    // Disseny de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Carregant...", style = MaterialTheme.typography.titleLarge)
        }
    }
}




