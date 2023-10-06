package com.qt46.simplepdf.screens.main.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.qt46.simplepdf.R
import com.qt46.simplepdf.data.ImageFile


@Composable
//@Preview(showBackground = true)
fun ImageToPDFScreen(files:List<ImageFile> = listOf(),onActionClicked:(String)->Unit,onSwap:(Int,Int)->Unit,onRemoveIconCLicked:(Int)->Unit,addFile:()->Unit,onBackPressed:()->Unit){

    val openAlertDialog = remember { mutableStateOf(false) }
    when {
        // ...
        openAlertDialog.value -> {
            DialogWithTextField(onDismiss = {
                openAlertDialog.value = false
            }, onConfirm = {
                openAlertDialog.value = false
                onActionClicked(it)
            }, title = stringResource(id = R.string.input_file_name), placeholder = stringResource(
                id = R.string.place_holder_filename
            ))
        }
    }

    Column {
        TopAppBar(navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "back")
            }

        }, actions = {
            TextButton(onClick = { addFile() }) {
                androidx.compose.material.Text(text = stringResource(id = R.string.action_add))
            }

            Spacer(modifier = Modifier.width(9.dp))
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "merge icon",
                modifier = Modifier.clickable {
                    openAlertDialog.value=true
//                    openAlertDialog.value=true

                })
            Spacer(modifier = Modifier.width(9.dp))
        }, title = {
            Text(
                "Split", color = MaterialTheme.colorScheme.onSurface, style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
        }, backgroundColor = MaterialTheme.colorScheme.background)
        DragDropColumn(items = files, onSwap =onSwap ) { index,item->
            ImagePreview(item = item){
                onRemoveIconCLicked(index)
            }
        }
    }

}

@Composable
fun ImagePreview(item:ImageFile ,onRemoveIconCLicked: () -> Unit){
    OutlinedButton(
        onClick={},
//        horizontalArrangement = Arrangement.Start,
//        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
//            .clip(RoundedCornerShape(10.dp))
    , shape = RoundedCornerShape(10.dp)
    ) {
        Icon(
            Icons.Default.Menu,
            contentDescription = "pdf image"
        )
        Spacer(modifier = Modifier.width(22.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.uri)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_image),
            contentDescription = stringResource(R.string.all_pdf),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .requiredSize(70.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.fillMaxWidth(0.9f)) {
            Text(
                text = item.fileName,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary,
                 maxLines = 1 ,
                 overflow= TextOverflow.Ellipsis
            )
            Text(
                text = item.fileSize + "kb",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )


        }
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = {
                onRemoveIconCLicked()
        }) {

            Icon(
                Icons.Default.Close,
                modifier = Modifier
                    .requiredSize(32.dp)
                    .clip(CircleShape),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "pdf image"
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
    }

}