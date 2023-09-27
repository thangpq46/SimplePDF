package com.qt46.simplepdf.screens.pdfviewer

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfImportedPage
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.InputStream
import java.io.OutputStream


class PDFViewModel(private val application: Application) : AndroidViewModel(application) {
    val pdfUri = MutableStateFlow(Uri.parse(""))
    fun initData(uri: String){
        this.pdfUri.update {
            Uri.parse(uri)
        }
    }
    private val _currentPage =  MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage
    fun setCurrentPage(page: Int){
        _currentPage.update {
            page
        }
    }

    val openDialog =MutableStateFlow(false)
    val message = MutableStateFlow("")
    val editMessage = MutableStateFlow("")





}