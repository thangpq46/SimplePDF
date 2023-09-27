package com.qt46.simplepdf.screens.main

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfImportedPage
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.qt46.simplepdf.data.ImageFile
import com.qt46.simplepdf.data.PDFFile
import com.qt46.simplepdf.data.SearchBarStatus
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ItemPosition
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.random.Random


class MainViewModel(private val application: Application) : AndroidViewModel(application) {


    private val _searchBarWidgetStatus = MutableStateFlow(SearchBarStatus.CLOSED)
    val searchBarWidgetStatus = _searchBarWidgetStatus.asStateFlow()

    fun openSearchBar() {
        changeSearchBarWidgetStatus(true)
    }

    fun closeSearchBar() {
        changeSearchBarWidgetStatus(false)
    }

    private val _filesToMerge = mutableStateListOf<PDFFile>()
    val filesToMerge: List<PDFFile> = _filesToMerge
    private val _splitUri = MutableStateFlow(Uri.parse(""))
    fun addFilesToMerge(file: PDFFile) {
//        _filesToMerge.update {
//            it.add(file)
//            it
//        }
    }

    fun changeIndex(from: ItemPosition, to: ItemPosition) {

        viewModelScope.launch {
            _filesToMerge.add(to.index, _filesToMerge.removeAt(from.index))
        }

//        = data.value.toMutableList().apply {
//
//        }
    }

    private fun changeSearchBarWidgetStatus(newState: Boolean) {
        if (newState) {
            _searchBarWidgetStatus.update {
                SearchBarStatus.OPEN
            }
        } else {
            _searchBarWidgetStatus.update {
                SearchBarStatus.CLOSED
            }
        }
    }

    fun merge(fileName: String = "Merge" + Random.nextInt(20000)) {
        val primaryExternalStorage = ContextCompat.getExternalFilesDirs(application, null)[0]
        val outfile = File(primaryExternalStorage, "${fileName}.pdf")
        val lis = mutableListOf<InputStream>()
        _filesToMerge.forEach {
            application.contentResolver.openInputStream(Uri.parse(it.uri))
                ?.let { it1 -> lis.add(it1) }
        }
        mergePdfFiles(lis, outfile.outputStream())
    }

    @Throws(Exception::class)
    fun mergePdfFiles(
        inputPdfList: List<InputStream>,
        outputStream: OutputStream
    ) {

        //Create document and pdfReader objects.
        val document = Document()
        val readers: MutableList<PdfReader> = ArrayList<PdfReader>()
        var totalPages = 0

        //Create pdf Iterator object using inputPdfList.
        val pdfIterator = inputPdfList.iterator()

        // Create reader list for the input pdf files.
        while (pdfIterator.hasNext()) {
            val pdf = pdfIterator.next()
            val pdfReader = PdfReader(pdf)
            readers.add(pdfReader)
            totalPages += pdfReader.numberOfPages
        }

        // Create writer for the outputStream
        val writer: PdfWriter = PdfWriter.getInstance(document, outputStream)

        //Open document.
        document.open()

        //Contain the pdf data.
        val pageContentByte: PdfContentByte = writer.directContent
        var pdfImportedPage: PdfImportedPage
        var currentPdfReaderPage = 1
        val iteratorPDFReader: Iterator<PdfReader> = readers.iterator()

        // Iterate and process the reader list.
        while (iteratorPDFReader.hasNext()) {
            val pdfReader: PdfReader = iteratorPDFReader.next()
            //Create page and add content.
            while (currentPdfReaderPage <= pdfReader.getNumberOfPages()) {
                document.newPage()
                pdfImportedPage = writer.getImportedPage(
                    pdfReader, currentPdfReaderPage
                )
                pageContentByte.addTemplate(pdfImportedPage, 0f, 0f)
                currentPdfReaderPage++
            }
            currentPdfReaderPage = 1
        }

        //Close document and outputStream.
        outputStream.flush()
        document.close()
        outputStream.close()
        println("Pdf files merged successfully.")
    }


    fun splitPdf() {
        viewModelScope.launch(Dispatchers.IO) {
            val primaryExternalStorage = ContextCompat.getExternalFilesDirs(application, null)[0]
            try {
                val inFile: InputStream? =
                    application.contentResolver.openInputStream(_splitUri.value)
                println("Reading $inFile")
                val reader = PdfReader(inFile)
                val n = reader.numberOfPages
                val outFile = File(primaryExternalStorage, "splited.pdf")
                val document = Document(reader.getPageSizeWithRotation(1))
                val writer = PdfCopy(document, FileOutputStream(outFile))
                document.open()
                for (j in 0 until _splitPagesSelectState.size) {
                    if (_splitPagesSelectState[j]) {
                        val page =
                            writer.getImportedPage(reader, j + 1) //Page Start from 1 so gotta +1
                        writer.addPage(page)
                    }
                }
                document.close()
                writer.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }

    fun optimize(pdfUri: Uri) {


        val reader = PdfReader(application.contentResolver.openInputStream(pdfUri))
        val primaryExternalStorage = ContextCompat.getExternalFilesDirs(application, null)[0]
        val out = File(primaryExternalStorage, "optimize.pdf")
        val stamper = PdfStamper(reader, out.outputStream())
        //PdfWriter writer = stamper.wr
        //PdfWriter writer = stamper.wr
        reader.removeFields()
        reader.removeUnusedObjects()

        val total = reader.numberOfPages + 1
        for (i in 1 until total) {
            reader.setPageContent(i + 1, reader.getPageContent(i + 1))
        }
        try {
            stamper.setFullCompression()
            stamper.close()
        } catch (e: DocumentException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    fun imageToPdf() {
        viewModelScope.launch(Dispatchers.IO) {
            val document = Document()
            val primaryExternalStorage = ContextCompat.getExternalFilesDirs(application, null)[0]
            val out = File(primaryExternalStorage, "image.pdf")

            PdfWriter.getInstance(
                document,
                out.outputStream()
            ) //  Change pdf's name.


            document.open()
            for( item in _listImageToPDF){
                val image = Image.getInstance(application.contentResolver.openInputStream(Uri.parse(item.uri))
                    ?.let { getBytes(it) }) // Change image's name and extension.


                val scaler: Float = (document.pageSize.width - document.leftMargin()
                        - document.rightMargin() - 0) / image.width * 100 // 0 means you have no indentation. If you have any, change it.

                image.scalePercent(scaler)
                image.alignment = Image.ALIGN_CENTER or Image.ALIGN_TOP

                document.add(image)
            }

            document.close()
        }

    }

    fun extractText() {
        try {
            var parsedText = ""
            val reader = PdfReader("yourPdfPath")
            val n: Int = reader.numberOfPages
            for (i in 0 until n) {
                parsedText =
                    """
            $parsedText${PdfTextExtractor.getTextFromPage(reader, i + 1).trim { it <= ' ' }}
            
            """.trimIndent() //Extracting the content from the different pages
            }
            println(parsedText)
            reader.close()
        } catch (e: java.lang.Exception) {
            println(e)
        }
    }

    private val _pdfFiles = MutableStateFlow<MutableList<PDFFile>>(mutableListOf())
    val pdfFiles: StateFlow<MutableList<PDFFile>> = _pdfFiles

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _pdfFilesFilter = mutableStateListOf<PDFFile>()

    @OptIn(FlowPreview::class)
    val pdfFilters = searchText.debounce(1000L).combine(_pdfFiles) { text, files ->
        if (text.isBlank()) {
            files
        } else {
            files.filter {
                it.filename.contains(text, true)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _pdfFiles.value
    )

    fun loadAllPDF() {
        viewModelScope.launch(Dispatchers.IO) {
            _pdfFiles.update {
                mutableListOf()
            }
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.SIZE
            )


            val whereClause = MediaStore.Files.FileColumns.MIME_TYPE + "=?" + ""
            val orderBy = MediaStore.Files.FileColumns.SIZE + " DESC"
            val cursor = application.contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                whereClause,
                arrayOf("application/pdf"),
                orderBy
            )
            val idCol = cursor!!.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
//            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
//            val addedCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
            val modifiedCol =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
//            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            if (cursor.moveToFirst()) {
                do {
                    val fileUri = Uri.withAppendedPath(
                        MediaStore.Files.getContentUri("external"),
                        cursor.getString(idCol)
                    )
                    val dateModified = cursor.getLong(modifiedCol)
                    _pdfFiles.update {
                        it.add(
                            PDFFile(
                                cursor.getString(nameCol),
                                fileUri.toString(),
                                cursor.getInt(sizeCol).toString(),
                                convertLongToDate(dateModified)
                            )
                        )
                        it
                    }
                    _filesToMerge.add(
                        PDFFile(
                            cursor.getString(nameCol),
                            fileUri.toString(),
                            cursor.getInt(sizeCol).toString(),
                            convertLongToDate(dateModified)
                        )
                    )

//                    _pdfFiles.add(PDFFile(cursor.getString(nameCol),fileUri.toString(),cursor.getInt(sizeCol).toString(),convertLongToDate(dateModified)))
                    // ...
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }

    fun filter(searchText: String) {
        _searchText.update {
            searchText
        }
    }

    private fun convertLongToDate(time: Long): String =
        DateTimeFormatter.ofPattern("dd MMMM yyyy").format(
            Instant.ofEpochMilli(time * 1000)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        )

    fun selectFile(item: PDFFile) {
        if (_filesToMerge.contains(item)) {
            removeFileMerge(item)
        } else {
            _filesToMerge.add(item)
        }

    }

    fun removeFileMerge(item: PDFFile) {
        _filesToMerge.remove(item)
    }

    private val _splitPages = mutableStateListOf<ImageBitmap>()
    val splitPages: List<ImageBitmap> = _splitPages

    private val _splitPagesSelectState = mutableStateListOf<Boolean>()
    val splitPagesSelectState: List<Boolean> = _splitPagesSelectState
    fun initPreviewSplit(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _splitUri.update {
                uri
            }
            val primaryExternalStorage = ContextCompat.getExternalFilesDirs(application, null)[0]
            val out = File(primaryExternalStorage, "merge.pdf")
            val reader = PdfReader(application.contentResolver.openInputStream(uri))
            for (i in 0 until reader.numberOfPages) {
                getBitmap(out, i)?.let {
                    _splitPages.add(it.asImageBitmap())
                    _splitPagesSelectState.add(false)
                }
            }
        }

    }

    private fun getBitmap(file: File?, page: Int): Bitmap? {
        val pdfiumCore = PdfiumCore(application.applicationContext)
        try {
            val pdfDocument: PdfDocument = pdfiumCore.newDocument(openFile(file))
            pdfiumCore.openPage(pdfDocument, page)
            val width = pdfiumCore.getPageWidthPoint(pdfDocument, page)
            val height = pdfiumCore.getPageHeightPoint(pdfDocument, page)


            // ARGB_8888 - best quality, high memory usage, higher possibility of OutOfMemoryError
            // RGB_565 - little worse quality, twice less memory usage
            val bitmap = Bitmap.createBitmap(
                width, height,
                Bitmap.Config.RGB_565
            )
            pdfiumCore.renderPageBitmap(
                pdfDocument, bitmap, page, 0, 0,
                width, height
            )
            //if you need to render annotations and form fields, you can use
            //the same method above adding 'true' as last param
            pdfiumCore.closeDocument(pdfDocument) // important!
            return bitmap
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return null
    }

    private fun openFile(file: File?): ParcelFileDescriptor? {
        val descriptor: ParcelFileDescriptor = try {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }
        return descriptor
    }

    fun changePageState(index: Int) {
        _splitPagesSelectState[index] = !_splitPagesSelectState[index]
    }

    fun searchText(text:String){
        val inFile: InputStream? =
            application.contentResolver.openInputStream(_splitUri.value)
        println("Reading $inFile")
        val reader = PdfReader(inFile)

    }

    private val _listImageToPDF = mutableStateListOf<ImageFile>()
    val listImageToPDF:List<ImageFile> =_listImageToPDF

    fun addImageToPDF(item:Uri){
        application.contentResolver.query(item,null,null,null,null)?.let {cursor->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            _listImageToPDF.add(ImageFile(item.toString(),cursor.getString(nameIndex),cursor.getLong(sizeIndex).toString()))
            cursor.close()
        }

    }
}