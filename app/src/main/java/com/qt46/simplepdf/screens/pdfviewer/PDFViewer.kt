package com.qt46.simplepdf.screens.pdfviewer

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.AndroidView
import com.example.compose.SimplePDFTheme
import com.github.barteksc.pdfviewer.PDFView
import com.qt46.simplepdf.R
import kotlinx.coroutines.flow.StateFlow

class PDFViewer : ComponentActivity() {
    private val viewModel by viewModels<PDFViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra("uri_doc")?.let {
            viewModel.initData(it)
        }
        setContent {
            SimplePDFTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column {
                        PDFView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(.8f)
                                .background(Color.Red), state = viewModel.pdfUri
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PDFView(
    modifier: Modifier,
    state: StateFlow<Uri>
) {
    var pdf: PDFView? = null
    val pdfState = state.collectAsState()
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PDFView(context, null).apply {
                maxZoom = 4f
                minZoom = 0.2f
            }
        },
        update = { pdfView ->
            pdf = pdfView
            pdfView.fromUri(pdfState.value)
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .load()

        }
    )
    val openDialog = remember { mutableStateOf(false) }
    Button(onClick = {
        openDialog.value = true
    }) {
        Text(text = "Page")
    }
    AlertDialogSample(state = openDialog,pdf?.pageCount, onConfirm ={
        openDialog.value = false
        pdf?.jumpTo(it.toInt())
    }, onDismiss = {
        openDialog.value = false
    })
    Button(onClick = {

    }) {
        Text(text = "Search")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogSample(state: MutableState<Boolean>,pageCount:Int?, onConfirm: (String) -> Unit = {},onDismiss:()->Unit) {
    var text by remember {
        mutableStateOf("")
    }
    if (state.value){
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                text=""
                onDismiss()
            },
            title = {
                Text(text = stringResource(id = R.string.go_to_page))
            },
            text = {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), placeholder = {
                        Text(text = "Pick from 0 to $pageCount")
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        state.value = false
                        onConfirm(text)
                        text=""
                    }) {
                    Text(text = stringResource(id = R.string.ok).uppercase())
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        text=""
                        onDismiss()
                    }) {
                    Text(stringResource(id = R.string.cancel).uppercase())
                }
            }
        )
    }


}