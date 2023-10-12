package com.qt46.simplepdf.screens.main.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    Column(modifier = modifier) {

        LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            items(items = filtedPDFs) {
                PDFPreview(it, onClickItems = onClickItems, onStarClicked =  onStarClicked)
            }
        }
    }

}

