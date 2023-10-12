package com.qt46.simplepdf.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PDFDao {

    @Query("SELECT * from pdffileentity ORDER BY id ASC")
     fun readALlData(): Flow<List<PDFFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStaredFile(file:PDFFileEntity):Long

    @Delete
    suspend fun deleteStarFile(file: PDFFileEntity)
}