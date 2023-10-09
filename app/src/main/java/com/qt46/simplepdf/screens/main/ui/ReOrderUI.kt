package com.qt46.simplepdf.screens.main.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.qt46.simplepdf.R
import org.burnoutcrew.reorderable.NoDragCancelledAnimation
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.reorderable

@Composable

fun ReOrderPage(images: MutableList<String> ,onActionClicked:(String)->Unit,onMove:(Int,Int)->Unit ,onBackPressed:()->Unit){
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
            IconButton(onClick = { onBackPressed()
                images.clear()}) {
                Icon(Icons.Default.ArrowBack, contentDescription = "back")
            }

        }, actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "icon guide")
            }

            Spacer(modifier = Modifier.width(9.dp))
            androidx.compose.material.IconButton(onClick = { openAlertDialog.value=true }) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "merge icon",
                    )
            }
            Spacer(modifier = Modifier.width(9.dp))
        }, title = {
            Text(
                stringResource(id = R.string.reorder_title), color = MaterialTheme.colorScheme.onSurface, style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
        }, backgroundColor = MaterialTheme.colorScheme.background)
        VerticalReorderGrid(images,onMove)
    }

}

@Composable
fun VerticalReorderGrid(images: MutableList<String>,onMove:(Int,Int)->Unit ) {

    val state = rememberReorderableLazyGridState(dragCancelledAnimation = NoDragCancelledAnimation(),
        onMove = { from, to ->
            images.apply {
                add(to.index, removeAt(from.index))
            }
            onMove(to.index,from.index)
        })
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        state = state.gridState,
        modifier = Modifier
            .reorderable(state),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement =Arrangement.spacedBy(4.dp)
    ) {
        items(images, { it }) { item ->
            ReorderableItem(state, key = item, defaultDraggingModifier = Modifier) { isDragging ->
                Surface(
                    modifier = Modifier
                        .width(120.dp)
                        .height(160.dp)
                        .background(MaterialTheme.colorScheme.surface),
                    tonalElevation = if (isDragging) 16.dp else 5.dp,
                    shadowElevation = if (isDragging) 16.dp else 5.dp
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_image),
                        contentDescription = stringResource(R.string.all_pdf),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .fillMaxSize(.8f)
                            .detectReorderAfterLongPress(state)
                    )
//                    Text(text = item, modifier = Modifier.detectReorderAfterLongPress(state))
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showBackground = true)
fun text(){
    var list by remember { mutableStateOf(listOf("A", "B", "C")) }
    LazyColumn {
        item {
            Button(onClick = { list = list.shuffled() }) {
                Text("Shuffle")
            }
        }
        items(list, key = { it }) {
            Text("Item $it", Modifier.animateItemPlacement())
        }
    }
}