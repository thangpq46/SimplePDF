package com.qt46.simplepdf.screens.main.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.qt46.simplepdf.R
import com.qt46.simplepdf.data.PDFFile

@Composable
fun StaredFiles(
    modifier: Modifier = Modifier,
    filtedPDFs: List<PDFFile>,
    onClickItems: (PDFFile) -> Unit,
    onStarClicked:(PDFFile)->Unit
) {
    if (filtedPDFs.isNotEmpty()){
        Column(modifier = modifier) {

            LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                items(items = filtedPDFs) {
                    PDFPreview(it, onClickItems = onClickItems, onStarClicked =  onStarClicked)
                }
            }
        }
    }else{
        Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

            Icon(painter = painterResource(id = R.drawable.ic_star), contentDescription = null,modifier=modifier.requiredSize(200.dp))
            Text(text = stringResource(id = R.string.no_star_file))
        }
    }


}

