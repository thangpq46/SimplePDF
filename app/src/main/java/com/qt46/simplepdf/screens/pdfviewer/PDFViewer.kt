package com.qt46.simplepdf.screens.pdfviewer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.barteksc.pdfviewer.PDFView
import com.qt46.simplepdf.R
import com.qt46.simplepdf.ui.theme.SimplePDFTheme
import kotlinx.coroutines.flow.StateFlow

class PDFViewer : ComponentActivity() {
    private val viewModel by viewModels<PDFViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra("uri_doc")?.let {
            viewModel.initData(it)
            setContent {
                SimplePDFTheme {
                    PDFView(
                        modifier = Modifier.fillMaxSize(),
                        uri = Uri.parse(it),
                        viewModel.pdfPageCount
                    )
                }
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PDFView(
    modifier: Modifier,
    uri: Uri,
    pagesizeState: StateFlow<Int>
) {
    var pdf: PDFView? = null
    var openDialog by remember { mutableStateOf(false) }
    val pdfPageCount by pagesizeState.collectAsState()
    var doNotUpdate by remember {
        mutableStateOf(false)
    }
    var nightMode by remember {
        mutableStateOf(false)
    }
    val activity = LocalContext.current as Activity
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = stringResource(id = R.string.title_pdfviewer))
            }, navigationIcon = {
                IconButton(onClick = {
                    activity.finish()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
            }, actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }

            }, modifier = Modifier.padding(horizontal = 9.dp))
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = {
                        openDialog = true
                        doNotUpdate = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_page),
                            contentDescription = "Localized description"
                        )

                    }
                    IconButton(onClick = {
                        doNotUpdate = false
                        nightMode = !nightMode

                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_nightmode),
                            contentDescription = "Localized description"
                        )

                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_rotate),
                            contentDescription = "Localized description"
                        )

                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {

                            val shareIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                // Example: content://com.google.android.apps.photos.contentprovider/...
                                putExtra(Intent.EXTRA_STREAM, uri)
                                type = "application/pdf"
                            }
                            activity.startActivity(Intent.createChooser(shareIntent, null))
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            "Localized description"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {

            Column {
                AndroidView(
                    modifier = modifier,
                    factory = { context ->
                        PDFView(context, null).apply {
                            maxZoom = 4f
                            minZoom = 0.2f
                        }
                    },
                    update = { pdfView ->
                        if (!doNotUpdate) {
                            pdf = pdfView
                            pdfView.fromUri(uri)
                                .enableSwipe(true) // allows to block changing pages using swipe
                                .swipeHorizontal(false)
                                .enableDoubletap(true)
                                .nightMode(nightMode)
                                .load()
                        }


                    }
                )
            }
            if (openDialog) {
                var text by remember {
                    mutableStateOf("")
                }
                AlertDialog(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = ""
                        )
                    },
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onDismissRequest.
                        openDialog = false
                    },
                    title = {
                        Text(text = "Title")
                    },
                    text = {
                        TextField(value = text,
                            onValueChange = {
                                text = it
                            },
                            modifier = Modifier
                                .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                                .fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            placeholder = {
                                Text(text = "Select from 1 to $pdfPageCount")
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openDialog = false
                                pdf?.jumpTo(text.toInt())
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openDialog = false
                            }
                        ) {
                            Text("Dismiss")
                        }
                    }
                )
            }
        }
    }

}
