package com.deixebledenkaito.autotechmanuals.ui.Profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService
import com.deixebledenkaito.autotechmanuals.data.repository.UserRepository
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.domain.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseDataBaseService: FirebaseDataBaseService,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val _userAportacions = MutableStateFlow<List<AportacioUser>>(emptyList())
    val userAportacions: StateFlow<List<AportacioUser>> get() = _userAportacions

    fun loadUser() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getUser()
                _user.value = currentUser
                currentUser?.let { user ->
                    val aportacions = firebaseDataBaseService.getAportacionsByUser(user.id)
                    _userAportacions.value = aportacions
                    Log.d("Aportacions carregades" ,"${aportacions.size}")

                }
            } catch (e: Exception) {
                println("Error carregant l'usuari: ${e.message}")
            }
        }
    }

    fun eliminarAportacio(aportacio: AportacioUser) {
        viewModelScope.launch {
            val success = firebaseDataBaseService.eliminarAportacio(
                userId = auth.currentUser?.uid ?: "",
                aportacio = aportacio // Passem l'objecte complet
            )

            if (success) {
                // Actualitzar la llista d'aportacions a la UI
                _userAportacions.value = _userAportacions.value.filter { it.id != aportacio.id }
            }
        }
    }

    // Funció per actualitzar la imatge de perfil
    fun updateProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                // Pujar la nova imatge a Firebase Storage
                val imageUrl = firebaseDataBaseService.uploadAndDownloadImage(imageUri,auth.currentUser?.uid ?: "")

                // Actualitzar la imatge de perfil a Firestore
                val success = firebaseDataBaseService.updateUserProfileImage(
                    userId = auth.currentUser?.uid ?: "",
                    imageUrl = imageUrl
                )

                if (success) {
                    // Actualitzar l'estat de l'usuari a la UI
                    _user.value = _user.value?.copy(profileImageUrl = imageUrl)
                }
            } catch (e: Exception) {
                println("Error actualitzant la imatge de perfil: ${e.message}")
            }
        }
    }


}
