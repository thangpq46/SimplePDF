package com.qt46.simplepdf.screens.main.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qt46.simplepdf.R


@Composable
@Preview(showBackground = true)
fun DialogWithTextField(placeholder:String = stringResource(id = R.string.all_pdf),title:String= stringResource(
    id = R.string.input_page
),onConfirm:(String)->Unit={},onDismiss:()->Unit={}){
    var text by remember {
        mutableStateOf("")
    }
    AlertDialog(
        icon = {
            Icon(painter = painterResource(id = R.drawable.ic_edit), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
        },
        onDismissRequest = onDismiss,
        title = {
            Text( color = MaterialTheme.colorScheme.primary,text = title.uppercase(), fontWeight = FontWeight.SemiBold, fontSize = MaterialTheme.typography.titleMedium.fontSize)
        },
        text = {
            TextField(value = text, onValueChange = {
                text=it
            }, modifier = Modifier
                .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                .fillMaxWidth(), textStyle = MaterialTheme.typography.bodyMedium, placeholder = {
                Text(text = placeholder)
            }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (text.isNotEmpty()){
                        onConfirm(text)
                    }
                     }
            ) {
                Text(stringResource(id = R.string.dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(id = R.string.dialog_dismiss))
            }
        }
    )
}