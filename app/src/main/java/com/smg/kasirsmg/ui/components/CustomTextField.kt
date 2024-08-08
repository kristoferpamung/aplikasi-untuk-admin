package com.smg.kasirsmg.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField (
    modifier: Modifier = Modifier,
    label : String,
    value : String,
    placeholder: String,
    keyboardType: KeyboardType,
    onValueChanged : (String) -> Unit
) {
    Column (
        modifier = modifier
    ) {
        Text(text = label)
        Spacer(modifier = Modifier.width(2.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChanged,
            singleLine = true,
            placeholder = { Text(text = placeholder) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = keyboardType
            ),
            modifier = modifier.fillMaxWidth()
        )
    }
}