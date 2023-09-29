package com.qt46.simplepdf.screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

fun ReOrderPage(images: MutableList<String> ,onActionClicked:()->Unit,onMove:(Int,Int)->Unit ){
    Column {
        TopAppBar(navigationIcon = {
            Icon(Icons.Default.ArrowBack, contentDescription = "back")
        }, actions = {
            TextButton(onClick = {  }) {
                androidx.compose.material.Text(text = stringResource(id = R.string.action_add))
            }

            Spacer(modifier = Modifier.width(9.dp))
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "merge icon",
                modifier = Modifier.clickable {
                    onActionClicked()
//                    openAlertDialog.value=true

                })
            Spacer(modifier = Modifier.width(9.dp))
        }, title = {
            Text(
                "Merge", color = MaterialTheme.colorScheme.onSurface, style = TextStyle(
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
            .reorderable(state)
            .background(Color.Green)
    ) {
        items(images, { it }) { item ->
            ReorderableItem(state, key = item, defaultDraggingModifier = Modifier) { isDragging ->
                Surface(
                    modifier = Modifier
                        .aspectRatio(1f)
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