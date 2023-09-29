package com.qt46.simplepdf.screens.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text

import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qt46.simplepdf.R
import com.qt46.simplepdf.constants.TOOL_MERGE_PDF
import com.qt46.simplepdf.data.PDFFile
import com.qt46.simplepdf.data.SearchBarStatus
import kotlinx.coroutines.flow.StateFlow
import org.burnoutcrew.reorderable.ItemPosition
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.w3c.dom.Text

@Composable
fun MergeScreen(files:
                List<PDFFile> ,onMove:(ItemPosition, ItemPosition,Int)->Unit,onMerge:(String)->Unit,onRemoveClicked:(PDFFile)->Unit,addFile:()->Unit){
//    val files by data.collectAsState()

    val state = rememberReorderableLazyListState(onMove = { from, to ->

        onMove(from,to, TOOL_MERGE_PDF)

    })
    val openAlertDialog = remember { mutableStateOf(false) }
    when {
        // ...
        openAlertDialog.value -> {
            AlertDialogWithTextField(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                    onMerge(it)
                },
                dialogTitle = "Alert dialog example"
            )
        }
    }
    Column {
        TopAppBar(navigationIcon = {
            Icon(Icons.Default.ArrowBack, contentDescription = "back")
        }, actions = {
            TextButton(onClick = { addFile() }) {
                Text(text = stringResource(id = R.string.action_add))
            }

            Spacer(modifier = Modifier.width(9.dp))
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "merge icon",
                modifier = Modifier.clickable {
                    openAlertDialog.value=true

                })
            Spacer(modifier = Modifier.width(9.dp))
        }, title = {
            androidx.compose.material3.Text(
                "Merge", color = MaterialTheme.colorScheme.onSurface, style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
        }, backgroundColor = MaterialTheme.colorScheme.background)
        LazyColumn(
            state = state.listState,
            modifier = Modifier
                .reorderable(state)
                .detectReorderAfterLongPress(state)
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            itemsIndexed(items = files) { index,item ->
                ReorderableItem(state, key = item) { isDragging ->
                    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                    Column(
                        modifier = Modifier
                            .shadow(elevation.value)
                    ) {
//                        Text(text = item.filename)
                        FileMerge(modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)).detectReorderAfterLongPress(state),file = item, onRemoveClicked =  onRemoveClicked)
                        if(index<files.lastIndex){
                            Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.End){
                                Divider(color = Color.Black, modifier = Modifier
                                    .fillMaxWidth(.75f)
                                    .alpha(.3f))
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FileMerge(modifier: Modifier=Modifier,file:PDFFile,onRemoveClicked: (PDFFile) -> Unit){

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Icon(
                 Icons.Default.Menu,
                contentDescription = "pdf image"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                modifier = Modifier
                    .requiredSize(70.dp),
                painter = painterResource(id = R.drawable.ic_browse),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "pdf image"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.fillMaxWidth(0.9f)) {
                androidx.compose.material3.Text(
                    text = file.filename,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    androidx.compose.material3.Text(
                        text = file.size + "kb",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    androidx.compose.material3.Text(
                        text = file.dateModified,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

            }

            Icon(
                Icons.Default.Close,
                                modifier = Modifier
                                    .requiredSize(32.dp)
                                    .clickable {
                                        onRemoveClicked(file)
                                    }
                                    .clip(CircleShape),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "pdf image"
            )
            Spacer(modifier = Modifier.width(4.dp))
        }


}


@Composable
    fun SelectFileMergeScreen(files:StateFlow<List<PDFFile>>, SearchBarStatus: StateFlow<SearchBarStatus>,searchTextState:StateFlow<String>, filesToMerge:List<PDFFile>, onClick:(PDFFile)->Unit,onSearchIconClick:()->Unit,onTextChange:(String)->Unit,onClose:()->Unit){
    val pdfFiles by files.collectAsState()
    val searchState by SearchBarStatus.collectAsState()
    val textSearch by searchTextState.collectAsState()
    val context = LocalContext.current
    Column {
        MainAppBar(
            context.getString(R.string.app_name)
            ,
            searchState,
            onSearchIconClick = onSearchIconClick,
            textSearch,
            onTextChange,
            onClose
        )

        LazyColumn(
            modifier = Modifier
        ) {
            itemsIndexed(items = pdfFiles) { index,item ->
                FileSelect(file = item, isSelected = filesToMerge.contains(item),onClick)
                if(index<pdfFiles.lastIndex){
                    Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.End){
                        Divider(color = Color.Black, modifier = Modifier
                            .fillMaxWidth(.7f)
                            .alpha(.3f))
                    }
                }
            }
        }
    }

}

@Composable
fun FileSelect(file:PDFFile,isSelected:Boolean,onClick: (PDFFile) -> Unit){
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                onClick(file)
            }
    ) {
        Checkbox(checked = isSelected, onCheckedChange = {}, enabled = false)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            modifier = Modifier
                .requiredSize(70.dp),
            painter = painterResource(id = R.drawable.ic_browse),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "pdf image"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.fillMaxWidth(0.9f)) {
            androidx.compose.material3.Text(
                text = file.filename,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.material3.Text(
                    text = file.size + "kb",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
                androidx.compose.material3.Text(
                    text = file.dateModified,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

        }

    }
}


@Preview(showBackground = true)
@Composable
fun AlertDialogWithTextField(
    onDismissRequest: () -> Unit={},
    onConfirmation: (String) -> Unit={},
    dialogTitle: String="File Name",
) {
    var filename by remember {
        mutableStateOf("Merge 12231231")
    }
    AlertDialog(
        icon = {
            Icon(painterResource(id = R.drawable.ic_edit), contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            TextField(value = filename, onValueChange ={filename=it} )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(filename)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}