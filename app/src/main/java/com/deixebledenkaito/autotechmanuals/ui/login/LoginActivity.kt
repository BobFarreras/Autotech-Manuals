package com.deixebledenkaito.autotechmanuals.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.deixebledenkaito.autotechmanuals.R
import com.deixebledenkaito.autotechmanuals.ui.home.HomeActivity
import com.deixebledenkaito.autotechmanuals.ui.signup.SignupActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
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
    val isLoading by loginViewModel.isLoading.collectAsState()

    var showPhoneDialog by remember { mutableStateOf(false) }
    // Observa l'estat de càrrega
    LaunchedEffect(Unit) {
        loginViewModel.isLoading.collect { isLoading ->
            // Aquí pots gestionar l'estat de càrrega si cal
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imatge del logo centrada a la part superior
        Image(
            painter = rememberImagePainter(R.drawable.logo_v1),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Camp d'usuari
        var user by remember { mutableStateOf("matutano8@gmail.com") }
        TextField(
            value = user,
            onValueChange = { user = it },
            label = { Text("Usuari") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Camp de contrasenya
        var password by remember { mutableStateOf("123456") }
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contrasenya") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            // Enllaç per registrar-se alineat a la dreta
            TextButton(
                onClick = {
                    context.startActivity(Intent(context, SignupActivity::class.java))
                }
            ) {
                Text("Registra't")
            }
        }

        // Botó d'inici de sessió
        Button(
            onClick = {
                loginViewModel.login(user, password) {
                    context.startActivity(Intent(context, HomeActivity::class.java))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sessió")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (showPhoneDialog) {
            PhoneLoginDialog(
                loginViewModel = loginViewModel,
                onDismiss = { showPhoneDialog = false }
            )
        }

        // Botó d'inici de sessió amb telèfon amb icona de mòbil
        Button(
            onClick = { showPhoneDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_movil),
                contentDescription = "Phone Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Iniciar sessió amb telèfon")
        }

        // Indicador de càrrega
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
    }
}




@Composable
fun PhoneLoginDialog(
    loginViewModel: LoginViewModel,
    onDismiss: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val callback = remember {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                loginViewModel.verifyCode(credential.smsCode ?: "") {
                    onDismiss()
                    Toast.makeText(context, "Verificació completada", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                onDismiss()
                Toast.makeText(context, "Error de verificació: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                loginViewModel.setVerificationId(verificationId)
                isCodeSent = true
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Iniciar sessió amb telèfon") },
        text = {
            Column {
                if (!isCodeSent) {
                    TextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Número de telèfon") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    TextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Codi de verificació") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!isCodeSent) {
                        loginViewModel.startPhoneNumberVerification(phoneNumber, callback)
                    } else {
                        loginViewModel.verifyCode(code) {
                            onDismiss()
                            Toast.makeText(context, "Verificació completada", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            ) {
                Text(if (isCodeSent) "Verifica" else "Envia codi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel·la")
            }
        }
    )
}