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
import androidx.compose.foundation.lazy.itemsIndexed
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
fun AllPDFFiles(
    modifier: Modifier = Modifier,
    filtedPDFs: List<PDFFile>,
    onClickItems: (PDFFile) -> Unit,
    onStarClicked:(PDFFile)->Unit
) {
    Column(modifier = modifier) {

        LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            itemsIndexed(items = filtedPDFs) {index,item->
                PDFPreview(item, onClickItems = onClickItems, onStarClicked =  onStarClicked)
            }
        }
    }

}


@Composable
fun PDFPreview(
    file: PDFFile,
    onClickItems: (PDFFile) -> Unit,
    clickable: Boolean = true,
    onStarClicked:(PDFFile)->Unit
) {
    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        enabled = clickable,
        onClick = { onClickItems(file) },
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier
                    .requiredSize(70.dp),
                painter = painterResource(id = R.drawable.ic_document),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "pdf image"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = file.filename,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = file.size + "kb",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = file.dateModified,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

            }
            IconButton(onClick = { onStarClicked(file) }) {
                Icon(
                    modifier = Modifier
                        .requiredSize(32.dp)
                        .padding(start = 10.dp),
                    painter = if (file.isStared) painterResource(id = R.drawable.ic_unstar)  else painterResource(id = R.drawable.ic_star),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "pdf image"
                )
            }

        }

    }
}
