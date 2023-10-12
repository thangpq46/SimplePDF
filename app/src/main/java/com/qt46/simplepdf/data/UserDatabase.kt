package com.qt46.simplepdf.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [
        PDFFileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PdfDataBase : RoomDatabase() {
    abstract fun pdfDao(): PDFDao
    companion object{
        @Volatile
        private var INSTANCE : PdfDataBase? = null
        fun getDatabase(context: Context):PdfDataBase{

            val tempInstance = INSTANCE
            if (tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,PdfDataBase::class.java,"pdf_db").fallbackToDestructiveMigration().build()
                INSTANCE=instance
                return instance
            }
        }
    }
}