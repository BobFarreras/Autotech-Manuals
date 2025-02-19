package com.deixebledenkaito.autotechmanuals.data.response

// Resposta de Firestore per als manuals més populars
data class TopManualsResponse(
    var ids: List<String> = emptyList()
)