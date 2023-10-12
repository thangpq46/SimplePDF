package com.qt46.simplepdf.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class StaredFile(@PrimaryKey val uid: Int,
                      @ColumnInfo val fileName: String)
