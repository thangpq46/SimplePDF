package com.qt46.simplepdf.screens.main.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign


@Composable
fun TextFieldWithLimitLength(text:String,onTextChange:(String)->Unit,label:String, charLimit:Int=50, leadingIconId:Int){
    val errorMessage = "Text input too long"
    var isError by rememberSaveable { mutableStateOf(false) }

    fun validate(text: String) {
        isError = text.length > charLimit
    }

    TextField(
        value = text,
        onValueChange = {
            onTextChange(it)
            validate(text)
        },
        singleLine = true,
        label = { Text(if (isError) "$label*" else label) },
        supportingText = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Limit: ${text.length}/$charLimit",
                textAlign = TextAlign.End,
            )
        },
        isError = isError,
        keyboardActions = KeyboardActions { validate(text) },
        modifier = Modifier
            .semantics {
                // Provide localized description of the error
                if (isError) error(errorMessage)
            }
            .fillMaxWidth(),
        leadingIcon = {

            Icon(painter = painterResource(id = leadingIconId), null)
        },
        trailingIcon = {
            IconButton(onClick = { onTextChange("") }) {
                Icon(Icons.Default.Clear, null)
            }

        }
    )
}