package com.deixebledenkaito.autotechmanuals.ui.home


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add

import androidx.compose.ui.graphics.vector.ImageVector



// Definim les rutes de la toolbar inferior
sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    data object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    data object Profile : BottomNavItem("profile", Icons.Default.Person, "Perfil")
    data object Search : BottomNavItem("search", Icons.Default.Search, "Cercar")
    data object Create : BottomNavItem("create", Icons.Default.Add, "Crear")
}


