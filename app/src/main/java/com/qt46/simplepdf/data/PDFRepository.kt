package com.qt46.simplepdf.data

class PDFRepository(private val pdfDao:PDFDao) {

    suspend fun getStaredFiles() = pdfDao.readALlData()
    suspend fun addStaredFile(file:PDFFileEntity):Long =pdfDao.addStaredFile(file)
    suspend fun removeStarFile(file: PDFFileEntity) = pdfDao.deleteStarFile(file)

}