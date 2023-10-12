package com.qt46.simplepdf.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PDFFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo val filename: String
)