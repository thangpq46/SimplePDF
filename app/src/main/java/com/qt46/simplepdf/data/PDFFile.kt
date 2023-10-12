package com.qt46.simplepdf.data


data class PDFFile( val id: Int,  val filename:String, val uri:String, val size:String, val dateModified:String,var isStared:Boolean = false)

fun PDFFile.toPDFFileEntity():PDFFileEntity = PDFFileEntity(id = this.id,filename = this.filename)
