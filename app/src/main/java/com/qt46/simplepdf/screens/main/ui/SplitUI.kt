package com.qt46.simplepdf.screens.main.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.qt46.simplepdf.screens.main.ui.DialogWithTextField

@Composable

fun SplitScreen(pages:List<String> = listOf(),statePages:List<Boolean> = listOf(),onActionClicked:(String)->Unit={},onClickPage:(Int)->Unit={},onBackPressed:()->Unit){
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

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "back")
            }
            
        }, actions = {
            IconButton(onClick = { openAlertDialog.value=true}) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "merge icon",
                )
            }

        }, title = {
            Text(
                stringResource(id = R.string.title_split), color = MaterialTheme.colorScheme.onSurface, style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
        }, backgroundColor = MaterialTheme.colorScheme.background)
        Spacer(modifier = Modifier.height(5.dp))
        LazyVerticalGrid(columns = GridCells.Adaptive(120.dp),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)

        ){
            itemsIndexed(items = pages){ index,item ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_image),
                    contentDescription = stringResource(R.string.all_pdf),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(120.dp)
                        .height(160.dp)
                        .padding(2.dp)
                        .clickable {
                            onClickPage(index)
                        }
                        .shadow(16.dp, RoundedCornerShape(10.dp))
                )

                Checkbox(checked = statePages[index], onCheckedChange = {
                    onClickPage(index)
                })

            }
        }
    }

}