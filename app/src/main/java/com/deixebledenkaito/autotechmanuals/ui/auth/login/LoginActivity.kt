package com.deixebledenkaito.autotechmanuals.ui.auth.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.deixebledenkaito.autotechmanuals.R
import com.deixebledenkaito.autotechmanuals.ui.home.HomeActivity
import com.deixebledenkaito.autotechmanuals.ui.auth.signup.SignupActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(loginViewModel = loginViewModel)
        }
    }
}

@Composable
fun LoginScreen(loginViewModel: LoginViewModel) {
    val context = LocalContext.current
    val loginState by loginViewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observar l'estat de l'inici de sessió
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                // Navegar a la pantalla principal
                context.startActivity(Intent(context, HomeActivity::class.java))
            }
            is LoginState.Error -> {
                // Mostrar missatge d'error amb Snackbar
                val errorMessage = (loginState as LoginState.Error).message
                snackbarHostState.showSnackbar(errorMessage)
            }
            else -> {
                // No cal fer res en altres estats
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imatge del logo
            AsyncImage(
                model = R.drawable.logo_v1, // Referència a la imatge
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Camp d'usuari
            TextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("Usuari") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Camp de contrasenya
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contrasenya") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botó d'inici de sessió
            Button(
                onClick = { loginViewModel.login(user, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sessió")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Enllaç per registrar-se
            TextButton(
                onClick = {
                    context.startActivity(Intent(context, SignupActivity::class.java))
                }
            ) {
                Text("Registra't")
            }
        }
    }
}

