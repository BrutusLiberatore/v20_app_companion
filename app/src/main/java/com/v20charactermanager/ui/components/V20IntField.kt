package com.v20charactermanager.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun V20IntField(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var fieldState by remember(value) {
        mutableStateOf(TextFieldValue(
            text = value.toString(),
            selection = TextRange(value.toString().length)
        ))
    }

    OutlinedTextField(
        value = fieldState,
        onValueChange = { newValue ->
            fieldState = newValue
            newValue.text.toIntOrNull()?.let { onValueChange(it) }
        },
        label = { Text(label) },
        modifier = modifier,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}
