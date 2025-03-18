package com.deixebledenkaito.autotechmanuals.ui.aportacions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser

// Objecte global per emmagatzemar les dades de les aportacions
object AportacioData {
    var aportacions by mutableStateOf<List<AportacioUser>>(emptyList())
}