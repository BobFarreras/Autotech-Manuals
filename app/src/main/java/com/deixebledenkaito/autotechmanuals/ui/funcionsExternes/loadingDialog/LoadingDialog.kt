package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.loadingDialog


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun LoadingDialog(isLoading: Boolean, message: String = "Carregant...") {
    if (isLoading) {
        Dialog(
            onDismissRequest = { /* No es pot tancar manualment */ },
            content = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row (
                        modifier = Modifier.padding(32.dp),
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.width(28.dp))
                        Text(text = message, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        )
    }
}