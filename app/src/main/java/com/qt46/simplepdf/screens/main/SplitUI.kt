package com.qt46.simplepdf.screens.main


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itextpdf.text.Image
import com.qt46.simplepdf.R

@Composable
@Preview(showBackground = true)
fun SplitScreen(pages:List<ImageBitmap> = listOf(),statePages:List<Boolean> = listOf(),onActionClicked:()->Unit={},onClickPage:(Int)->Unit={}){
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(navigationIcon = {
            Icon(Icons.Default.ArrowBack, contentDescription = "back")
        }, actions = {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "merge icon",
                modifier = Modifier.clickable {
                    onActionClicked()
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
//        val pages by remember {
//            mutableStateOf(listOf(1,2,3,4,5,6,7,8,9,10,11,12,13))
//        }
        Spacer(modifier = Modifier.height(5.dp))
        LazyVerticalGrid(columns = GridCells.Adaptive(120.dp)){
            itemsIndexed(items = pages){ index,item ->
//                Image(bitmap = , contentDescription = )

                Image(item, contentDescription = null, modifier = Modifier
                    .width(120.dp)
                    .height(170.dp)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Color.Red
                    )
                    .clickable {
                        onClickPage(index)
                    }, contentScale = ContentScale.Crop)
                Checkbox(checked = statePages[index], onCheckedChange = {
                    onClickPage(index)
                })
//                Text(text = statePages[index].toString())

            }
        }
    }

}