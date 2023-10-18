package com.qt46.simplepdf.screens.pdfviewer

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfPage
import com.itextpdf.kernel.pdf.canvas.parser.EventType
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy
import com.itextpdf.text.pdf.PdfReader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PDFViewModel(private val application: Application) : AndroidViewModel(application) {
    private val pdfUri = MutableStateFlow(Uri.parse(""))

    private val _pdfPageCount = MutableStateFlow(0)
    val pdfPageCount = _pdfPageCount.asStateFlow()
    var document = PDDocument()
    fun initData(uri: String) {
        viewModelScope.launch {
            pdfUri.update {
                Uri.parse(uri)
            }
            val reader = PdfReader(application.contentResolver.openInputStream(Uri.parse(uri)))
            _pdfPageCount.update {
                reader.numberOfPages
            }
            document = PDDocument.load(application.contentResolver.openInputStream(pdfUri.value))
        }

    }

    fun serText(text: String) {
        for (i in 1 until document.numberOfPages) {
            val reader = PDFTextStripper()
            reader.startPage = i
            reader.endPage = i
            val pageText = reader.getText(document)
            println(pageText)
        }
    }

    fun findWords(findword: String = "skill") {
        viewModelScope.launch {
            var count = 0
            val d = PdfDocument(
                com.itextpdf.kernel.pdf.PdfReader(
                    application.contentResolver.openInputStream(pdfUri.value)
                )
            )
            for (page in 1 .. d.numberOfPages) {
                if (d.getPage(page).contains(findword)) {
                    count++
                }
            }
            Log.d("ASDSA",count.toString())
        }

    }

    fun PdfPage.contains(find: String, caseSensitive: Boolean = true): Boolean {
        val content = PdfTextExtractor.getTextFromPage(this)
        if (content.isNullOrEmpty()) {
            return false
        }
        return content.indexOf(find, 0, caseSensitive) > -1
    }
}