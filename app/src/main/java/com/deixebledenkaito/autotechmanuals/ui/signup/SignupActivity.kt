package com.deixebledenkaito.autotechmanuals.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.deixebledenkaito.autotechmanuals.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

// Activity principal per gestionar la pantalla de registre
@AndroidEntryPoint
class SignupActivity : ComponentActivity() {
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignupScreen(signUpViewModel)
        }
    }
}

// Funció composable que crea la UI del registre
@Composable
fun SignupScreen(viewModel: SignUpViewModel) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") } // Estat per l'input d'email
    var password by remember { mutableStateOf("") } // Estat per l'input de contrasenya
    val isLoading by viewModel.isLoading.collectAsState(initial = false) // Estat per mostrar el loading

    // Efecte que s'executa quan hi ha canvis a l'estat de càrrega
    LaunchedEffect(viewModel) {
        viewModel.isLoading.collectLatest { }
    }

    // Layout principal de la pantalla
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Camp d'entrada per l'email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Camp d'entrada per la contrasenya
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Botó per fer el registre
            Button(
                onClick = {
                    viewModel.register(email, password) {
                        context.startActivity(Intent(context, HomeActivity::class.java))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // Deshabilita el botó si s'està carregant
            ) {
                Text("Sign Up")
            }
            // Mostra un indicador de càrrega si s'està carregant
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

