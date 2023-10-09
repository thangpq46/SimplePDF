package com.qt46.simplepdf.screens.main.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.qt46.simplepdf.R

@Composable
@Preview(showBackground = true)
fun BottomDialog(
    onConfirmation: () -> Unit={},
    dialogTitle: String="Save Successfully",
    dialogText: String="File saved to: Download/abcxysasdas.pdf",
    icon: ImageVector = Icons.Default.Check,
) {
    AlertDialog(
        modifier = Modifier.customDialogModifier(CustomDialogPosition.BOTTOM),
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle ,style= MaterialTheme.typography.titleMedium)
        },
        text = {
            Text(text = dialogText, style = TextStyle(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            ))
        },
        onDismissRequest = {
            onConfirmation()
        },
        confirmButton = {
            TextButton( modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(id = R.string.dialog_confirm))
            }
        }
    )
}

enum class CustomDialogPosition {
    BOTTOM, TOP
}

fun Modifier.customDialogModifier(pos: CustomDialogPosition) = layout { measurable, constraints ->

    val placeable = measurable.measure(constraints);
    layout(constraints.maxWidth, constraints.maxHeight){
        when(pos) {
            CustomDialogPosition.BOTTOM -> {
                placeable.place(0, constraints.maxHeight - placeable.height, 10f)
            }
            CustomDialogPosition.TOP -> {
                placeable.place(0,0,10f)
            }
        }
    }
}