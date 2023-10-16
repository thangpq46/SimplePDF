package com.qt46.simplepdf.screens.main

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor
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
import com.qt46.simplepdf.R
import com.qt46.simplepdf.constants.DEFAULT_FILENAME
import com.qt46.simplepdf.constants.EX_IMG_FOLDER
import com.qt46.simplepdf.constants.EX_TXT_FOLDER
import com.qt46.simplepdf.constants.REORDER_FOLDER
import com.qt46.simplepdf.constants.SCALE_PREVIEW_REORDER
import com.qt46.simplepdf.constants.SPLIT_FOLDER
import com.qt46.simplepdf.data.ImageFile
import com.qt46.simplepdf.data.Notification
import com.qt46.simplepdf.data.PDFFile
import com.qt46.simplepdf.data.PDFFileEntity
import com.qt46.simplepdf.data.PDFRepository
import com.qt46.simplepdf.data.Page
import com.qt46.simplepdf.data.PdfDataBase
import com.qt46.simplepdf.data.ScreenState
import com.qt46.simplepdf.data.SearchBarStatus
import com.qt46.simplepdf.data.SimplePositionalTextEventListener
import com.qt46.simplepdf.data.SimpleTextWithRectangle
import com.qt46.simplepdf.data.toPDFFileEntity
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import com.tom_roush.pdfbox.pdmodel.PDDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.random.Random


class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    private val _screenState = MutableStateFlow(ScreenState.INDIE)
    val screenState = _screenState.asStateFlow()
    private val _notification = MutableStateFlow(Notification("", ""))
    val notification = _notification.asStateFlow()
    private val _searchBarWidgetStatus = MutableStateFlow(SearchBarStatus.CLOSED)
    val searchBarWidgetStatus = _searchBarWidgetStatus.asStateFlow()
    private val applicationDir: File = ContextCompat.getExternalFilesDirs(application, null)[0]
    private val _filesToMerge = mutableStateListOf<PDFFile>()
    val filesToMerge: List<PDFFile> = _filesToMerge
    private val _splitUri = MutableStateFlow(Uri.parse(""))
    private val repository: PDFRepository =
        PDFRepository(PdfDataBase.getDatabase(application).pdfDao())

    private val _pdfFiles = MutableStateFlow<MutableList<PDFFile>>(mutableListOf())

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _splitPages = mutableStateListOf<String>()
    val splitPages: List<String> = _splitPages

    private val _splitPagesSelectState = mutableStateListOf<Boolean>()
    val splitPagesSelectState: List<Boolean> = _splitPagesSelectState
    private var extractTextUri = Uri.parse("")
    private val _extractTextPages = mutableStateListOf<String>()
    val extractTextPages: List<String> = _extractTextPages
    private val _extractTextPagesState = mutableStateListOf<Boolean>()
    val extractTextPagesState: List<Boolean> = _extractTextPagesState

    fun changePageState(index: Int) {
        _splitPagesSelectState[index] = !_splitPagesSelectState[index]
    }

    private val _listImageToPDF = mutableStateListOf<ImageFile>()
    val listImageToPDF: List<ImageFile> = _listImageToPDF


    fun openSearchBar() {
        changeSearchBarWidgetStatus(true)
    }

    fun closeSearchBar() {
        changeSearchBarWidgetStatus(false)
    }


    fun changeIndexFilesMerge(from: Int, to: Int) {
        viewModelScope.launch {
            _filesToMerge.add(to, _filesToMerge.removeAt(from))
        }
    }

    fun changeIndexFilesImage(from: Int, to: Int) {
        viewModelScope.launch {
            _listImageToPDF.add(to, _listImageToPDF.removeAt(from))
        }
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
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.update {
                ScreenState.LOADING
            }
            val outfile = File(saveLocation, "${fileName}.pdf")
            val lis = mutableListOf<InputStream>()
            _filesToMerge.forEach {
                application.contentResolver.openInputStream(Uri.parse(it.uri))
                    ?.let { it1 -> lis.add(it1) }
            }
            mergePdfFiles(lis, outfile.outputStream())
            _notification.update {
                Notification(
                    application.getString(R.string.save_success), String.format(
                        application.getString(
                            R.string.save_location
                        ), "${fileName}.pdf"
                    )
                )
            }
            _screenState.update {
                ScreenState.NOTIFICATION
            }
        }

    }

    private fun mergePdfFiles(
        inputPdfList: List<InputStream>, outputStream: OutputStream
    ) {

        //Create document and pdfReader objects.
        val document = Document()
        val readers: MutableList<PdfReader> = ArrayList()
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
            while (currentPdfReaderPage <= pdfReader.numberOfPages) {
                document.newPage()
                pdfImportedPage = writer.getImportedPage(
                    pdfReader, currentPdfReaderPage
                )
                pageContentByte.addTemplate(pdfImportedPage, 0f, 0f)
                currentPdfReaderPage++
            }
            currentPdfReaderPage = 1
        }

        outputStream.flush()
        document.close()
        outputStream.close()
    }


    fun splitPdf(filename: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.update {
                ScreenState.LOADING
            }
            try {
                val inFile: InputStream? =
                    application.contentResolver.openInputStream(_splitUri.value)
                val reader = PdfReader(inFile)
//                val n = reader.numberOfPages
                val outFile = File(saveLocation, "$filename.pdf")
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
            } finally {
                _notification.update {
                    Notification(
                        application.getString(R.string.save_success), String.format(
                            application.getString(
                                R.string.save_location
                            ), "${filename}.pdf"
                        )
                    )
                }
                _screenState.update {
                    ScreenState.NOTIFICATION
                }
            }

        }

    }

    fun optimize(pdfUri: Uri) {
        viewModelScope.launch {

            _screenState.update {
                ScreenState.LOADING
            }

            val reader = PdfReader(application.contentResolver.openInputStream(pdfUri))
            val out = File(saveLocation, "optimize.pdf")
            val stamper = PdfStamper(reader, out.outputStream())
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
            } finally {
                _notification.update {
                    Notification(
                        application.getString(R.string.save_success), String.format(
                            application.getString(
                                R.string.save_location
                            ), "ptimize.pdf"
                        )
                    )
                }
                _screenState.update {
                    ScreenState.NOTIFICATION
                }

            }

        }
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    fun imageToPdf(filename: String = "image") {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.update {
                ScreenState.LOADING
            }
            val document = Document()
            val out = File(saveLocation, "$filename.pdf")

            PdfWriter.getInstance(
                document, out.outputStream()
            ) //  Change pdf's name.


            document.open()
            for (item in _listImageToPDF) {
                val image =
                    Image.getInstance(application.contentResolver.openInputStream(Uri.parse(item.uri))
                        ?.let { getBytes(it) }) // Change image's name and extension.


                val scaler: Float =
                    (document.pageSize.width - document.leftMargin() - document.rightMargin() - 0) / image.width * 100 // 0 means you have no indentation. If you have any, change it.

                image.scalePercent(scaler)
                image.alignment = Image.ALIGN_CENTER or Image.ALIGN_TOP

                document.add(image)
            }

            document.close()
            _notification.update {
                Notification(
                    application.getString(R.string.save_success), String.format(
                        application.getString(
                            R.string.save_location
                        ), "${filename}.pdf"
                    )
                )
            }
            _screenState.update {
                ScreenState.NOTIFICATION
            }
        }

    }


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
        viewModelScope, SharingStarted.WhileSubscribed(5000), _pdfFiles.value
    )
    private val _staredFiles = MutableStateFlow<List<PDFFileEntity>>(listOf())

    @OptIn(ExperimentalCoroutinesApi::class)
    val staredFiles = combine(_staredFiles, _pdfFiles, searchText) { stars, files, text ->
        Triple(stars, files, text)
    }.flatMapLatest { (stars, files, text) ->
        flow {
            emit(files.filter { file ->
                stars.find {
                    it.filename == file.filename
                } != null
            }.filter {
                it.filename.contains(text, true)
            })


        }


    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())


    fun loadAllPDF() {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.update {
                ScreenState.LOADING
            }
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
                        MediaStore.Files.getContentUri("external"), cursor.getString(idCol)
                    )
                    val dateModified = cursor.getLong(modifiedCol)
                    _pdfFiles.update {
                        var fileSize = cursor.getInt(sizeCol)
                        if (fileSize>0){
                            fileSize/=1024
                        }
                        it.add(
                            PDFFile(
                                cursor.getInt(idCol),
                                cursor.getString(nameCol),
                                fileUri.toString(),
                                fileSize .toString(),
                                convertLongToDate(dateModified)
                            )
                        )
                        it
                    }

//                    _pdfFiles.add(PDFFile(cursor.getString(nameCol),fileUri.toString(),cursor.getInt(sizeCol).toString(),convertLongToDate(dateModified)))
                    // ...
                } while (cursor.moveToNext())
            }
            cursor.close()
            val staredFiles = repository.getStaredFiles()
            val l = staredFiles.first()
            _pdfFiles.value = _pdfFiles.value.toMutableList().apply {
                for (index in this.indices) {
                    if (l.find {
                            this[index].filename == it.filename
                        } != null) {
                        this[index] = this[index].copy(isStared = true)
                    }
                }
            }
            _screenState.update {
                ScreenState.INDIE
            }
            staredFiles.collectLatest { starFiles ->
                _staredFiles.update {
                    starFiles
                }
            }

        }
    }

    fun filter(searchText: String) {
        _searchText.update {
            searchText
        }
    }

    private fun convertLongToDate(time: Long): String =
        DateTimeFormatter.ofPattern("dd MMMM yyyy").format(
            Instant.ofEpochMilli(time * 1000).atZone(ZoneId.systemDefault()).toLocalDate()
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


    fun addImageToPDF(item: Uri) {
        viewModelScope.launch {
            application.contentResolver.query(item, null, null, null, null)?.let { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                cursor.moveToFirst()
                _listImageToPDF.add(
                    ImageFile(
                        item.toString(),
                        cursor.getString(nameIndex),
                        cursor.getLong(sizeIndex).toString()
                    )
                )
                cursor.close()
            }
        }


    }

    fun SearchTextaaaa() {

        val primaryExternalStorage = ContextCompat.getExternalFilesDirs(application, null)[0]
        val inpt = File(primaryExternalStorage, "merge.pdf")
        val out = File(primaryExternalStorage, "merge.pdf")
        val pdfDoc = com.itextpdf.kernel.pdf.PdfDocument(
            com.itextpdf.kernel.pdf.PdfReader(
                inpt.inputStream()
            ), com.itextpdf.kernel.pdf.PdfWriter(
                out.outputStream()
            )
        )

        val listener = SimplePositionalTextEventListener()
        PdfCanvasProcessor(listener).processPageContent(pdfDoc.firstPage)
        val result: List<SimpleTextWithRectangle> = listener.getResultantTextWithPosition()

        var R = 0
        var G = 0
        var B = 0
        Log.d("TEXTS", result.toString())
        for (textWithRectangle in result) {
            R += 40
            R %= 256
            G += 20
            G %= 256
            B += 80
            B %= 256
            val canvas = PdfCanvas(pdfDoc.getPage(4))
            canvas.setStrokeColor(DeviceRgb(R, G, B))
            canvas.rectangle(textWithRectangle.rectangle)
            canvas.stroke()
        }


        pdfDoc.close()
    }


    private val _pdfPageReorder = mutableStateListOf<String>()
    val pdfPageReorder: MutableList<String> = _pdfPageReorder
    private val _indexReorderedPages = mutableListOf<Int>()
    private val _uriReOrder = MutableStateFlow(Uri.parse(""))
    private val saveLocation: File =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    fun reOrderIndexPages(to: Int, from: Int) {
        _indexReorderedPages.apply {
            add(to, removeAt(from))
        }
    }

    fun reOrderPages(filename: String = DEFAULT_FILENAME) {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.update {
                ScreenState.LOADING
            }
            val inFile: InputStream? =
                application.contentResolver.openInputStream(_uriReOrder.value)
            val reader = PdfReader(inFile)
//                val n = reader.numberOfPages
            val document = Document(reader.getPageSizeWithRotation(1))
            val writer =
                PdfCopy(document, application.contentResolver.openOutputStream(_uriReOrder.value))
            document.open()
            for (index in _indexReorderedPages) {
//            if (_splitPagesSelectState[index]) {
                val page = writer.getImportedPage(reader, index + 1) //Page Start from 1 so gotta +1
                writer.addPage(page)
//            }
            }
            document.close()
            writer.close()

            _notification.update {
                Notification(
                    application.getString(R.string.save_success), application.getString(
                        R.string.save_success_detail
                    )
                )
            }
            _screenState.update {
                ScreenState.NOTIFICATION
            }

        }

    }


    fun initPreviewSplit(uri: Uri) {

        viewModelScope.launch(Dispatchers.IO) {
            _screenState.update {
                ScreenState.LOADING
            }
            _splitUri.update {
                uri
            }
            val reader = PdfReader(application.contentResolver.openInputStream(uri))
            val pdfiumCore = PdfiumCore(application.applicationContext)

            val pdfDocument: PdfDocument =
                pdfiumCore.newDocument(application.contentResolver.openFileDescriptor(uri, "r"))
            val saveFolder = File(applicationDir, SPLIT_FOLDER)
            if (!saveFolder.exists()) {
                saveFolder.mkdirs()
            }
            for (page in 0 until reader.numberOfPages) {
                _splitPagesSelectState.add(false)
                _splitPages.add(
                    savePDFPageToData(
                        pdfiumCore, saveFolder, pdfDocument, page
                    )
                )
            }
            pdfiumCore.closeDocument(pdfDocument)
            _screenState.update {
                ScreenState.INDIE
            }
        }

    }

    fun initReorderPage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.update {
                ScreenState.LOADING
            }
            _uriReOrder.update {
                uri
            }
            val reader = PdfReader(application.contentResolver.openInputStream(uri))
            val pdfiumCore = PdfiumCore(application.applicationContext)
            val pdfDocument: PdfDocument =
                pdfiumCore.newDocument(application.contentResolver.openFileDescriptor(uri, "r"))
            val saveFolder = File(applicationDir, REORDER_FOLDER)
            if (!saveFolder.exists()) {
                saveFolder.mkdirs()
            }
            for (page in 0 until reader.numberOfPages) {
                _indexReorderedPages.add(page)
                _pdfPageReorder.add(
                    savePDFPageToData(
                        pdfiumCore, saveFolder, pdfDocument, page
                    )
                )
            }
            pdfiumCore.closeDocument(pdfDocument)
            _screenState.update {
                ScreenState.INDIE
            }
        }

    }

    private fun savePDFPageToData(
        pdfiumCore: PdfiumCore,
        primaryExternalStorage: File,
        pdfDocument: PdfDocument,
        page: Int,
        scale: Int = SCALE_PREVIEW_REORDER,
        quality: Int = 80
    ): String {
        val out = File(primaryExternalStorage, "$page.jpeg")
        pdfiumCore.openPage(pdfDocument, page)
        val width = pdfiumCore.getPageWidthPoint(pdfDocument, page) / scale
        val height = pdfiumCore.getPageHeightPoint(pdfDocument, page) / scale

        // ARGB_8888 - best quality, high memory usage, higher possibility of OutOfMemoryError
        // RGB_565 - little worse quality, twice less memory usage
        val bitmap = Bitmap.createBitmap(
            width, height, Bitmap.Config.RGB_565
        )
        pdfiumCore.renderPageBitmap(
            pdfDocument, bitmap, page, 0, 0, width, height
        )
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out.outputStream())
        return Uri.fromFile(out).toString()
    }

    fun removeImage(index: Int) {
        _listImageToPDF.removeAt(index)
    }

    private val _metaUri = MutableStateFlow(Uri.parse(""))
    private val _metaTitle = MutableStateFlow("")
    val metaTitle = _metaTitle.asStateFlow()
    private val _metaAuthor = MutableStateFlow("")
    val metaAuthor = _metaAuthor.asStateFlow()
    private val _metaCreator = MutableStateFlow("")
    val metaCreator = _metaCreator.asStateFlow()
    private val _metaProducer = MutableStateFlow("")
    val metaProducer = _metaProducer.asStateFlow()
    private val _metaKeywords = MutableStateFlow("")
    val metaKeywords = _metaKeywords.asStateFlow()
    private val _metaSubject = MutableStateFlow("")
    val metaSubject = _metaSubject.asStateFlow()
    private val _metaCreationDate = MutableStateFlow(Calendar.getInstance().timeInMillis)
    val metaCreationDate = _metaCreationDate.asStateFlow()

    private var _extractUri = MutableStateFlow(Uri.parse(""))
    private val _extractImagePages = mutableStateListOf<Page>()
    val extractImagePages: List<Page> = _extractImagePages


    fun initMetaData(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.update {
                ScreenState.LOADING
            }
            _metaUri.update {
                uri
            }
            val document: PDDocument =
                PDDocument.load(application.contentResolver.openInputStream(uri))
            val info = document.documentInformation
            _metaTitle.update {
                if (info.title == null) {
                    ""
                } else {
                    info.title
                }

            }
            _metaAuthor.update {
                if (info.author == null) {
                    ""
                } else {
                    info.author
                }

            }
            _metaCreator.update {
                if (info.creator == null) {
                    ""
                } else {
                    info.creator
                }

            }
            _metaProducer.update {
                if (info.producer == null) {
                    ""
                } else {
                    info.producer
                }

            }
            _metaKeywords.update {
                if (info.keywords == null) {
                    ""
                } else {
                    info.keywords
                }

            }
            _metaSubject.update {
                if (info.subject == null) {
                    ""
                } else {
                    info.subject
                }

            }
            _metaCreationDate.update {
                if (info.creationDate == null) {
                    Calendar.getInstance().timeInMillis
                } else {
                    info.creationDate.timeInMillis
                }

            }
            _screenState.update {
                ScreenState.INDIE
            }
        }
    }

    fun updateMetaDate(date: Long) {
        _metaCreationDate.update {
            date
        }
    }

    fun updateTitle(text: String) {
        _metaTitle.update {
            text
        }
    }

    fun updateAuthor(text: String) {
        _metaAuthor.update {
            text
        }
    }

    fun updateCreator(text: String) {
        _metaAuthor.update {
            text
        }
    }

    fun updateProducer(text: String) {
        _metaProducer.update {
            text
        }
    }

    fun updateSubject(text: String) {
        _metaSubject.update {
            text
        }
    }

    fun updateKeywords(text: String) {
        _metaKeywords.update {
            text
        }
    }

    fun saveMetaData() {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.update {
                ScreenState.LOADING
            }
            val document: PDDocument =
                PDDocument.load(application.contentResolver.openInputStream(_metaUri.value))
            val info = document.documentInformation
            info.author = _metaAuthor.value
            info.creator = _metaCreator.value
            info.keywords = _metaKeywords.value
            info.subject = _metaSubject.value
            info.title = _metaTitle.value
            info.producer = _metaProducer.value
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = _metaCreationDate.value
            info.creationDate = calendar
            document.documentInformation = info

            document.save(application.contentResolver.openOutputStream(_metaUri.value))
            document.close()
            _notification.update {
                Notification(
                    application.getString(R.string.save_success),
                    application.getString(R.string.save_success_detail)
                )
            }
            _screenState.update {
                ScreenState.NOTIFICATION
            }
        }


    }


    fun initExtractImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _screenState.update {
                ScreenState.LOADING
            }
            _extractUri.update {
                uri
            }
            val reader = PdfReader(application.contentResolver.openInputStream(uri))
            val pdfiumCore = PdfiumCore(application.applicationContext)

            val pdfDocument: PdfDocument =
                pdfiumCore.newDocument(application.contentResolver.openFileDescriptor(uri, "r"))

            val saveFolder = File(applicationDir, EX_IMG_FOLDER)
            if (!saveFolder.exists()) {
                saveFolder.mkdirs()
            }
            for (page in 0 until reader.numberOfPages) {
                _extractImagePages.add(
                    Page(
                        savePDFPageToData(
                            pdfiumCore, saveFolder, pdfDocument, page
                        ), false
                    )

                )
            }
            pdfiumCore.closeDocument(pdfDocument)
            _screenState.update {
                ScreenState.INDIE
            }
        }
    }

    fun extractImgPageSelect(page: Int) {
        _extractImagePages[page] =
            _extractImagePages[page].copy(isSelected = !_extractImagePages[page].isSelected)
    }

    fun extractImage() {
        viewModelScope.launch {
            _screenState.update {
                ScreenState.LOADING
            }
            val pdfiumCore = PdfiumCore(application.applicationContext)
            val pdfDocument: PdfDocument = pdfiumCore.newDocument(
                application.contentResolver.openFileDescriptor(
                    _extractUri.value, "r"
                )
            )
            val imgSaveLocation = File(saveLocation, "Images")
            if (!imgSaveLocation.exists()) {
                imgSaveLocation.mkdirs()
            }
            for (page in _extractImagePages.indices) {
                if (_extractImagePages[page].isSelected) {
                    savePDFPageToData(
                        pdfiumCore, imgSaveLocation, pdfDocument, page, scale = 1, quality = 100
                    )
                }
            }


            pdfiumCore.closeDocument(pdfDocument)
            _notification.update {
                Notification(
                    application.getString(R.string.save_success),

                    String.format(
                        application.getString(
                            R.string.save_location
                        ), "Images"
                    )
                )
            }
            _screenState.update {
                ScreenState.NOTIFICATION
            }
        }
    }

    fun dismissNotification() {
        viewModelScope.launch {
            _screenState.update {
                ScreenState.INDIE
            }
            clearCache()
        }
    }

    fun initExtractText(uri: Uri) {
        viewModelScope.launch {
            extractTextUri = uri
            _screenState.update {
                ScreenState.LOADING
            }
            val reader = PdfReader(application.contentResolver.openInputStream(uri))
            val pdfiumCore = PdfiumCore(application.applicationContext)
            val pdfDocument: PdfDocument =
                pdfiumCore.newDocument(application.contentResolver.openFileDescriptor(uri, "r"))
            val saveFolder = File(applicationDir, EX_TXT_FOLDER)
            if (!saveFolder.exists()) {
                saveFolder.mkdirs()
            }
            for (page in 0 until reader.numberOfPages) {
                _extractTextPagesState.add(false)
                _extractTextPages.add(
                    savePDFPageToData(
                        pdfiumCore, saveFolder, pdfDocument, page
                    )
                )
            }
            pdfiumCore.closeDocument(pdfDocument)
            _screenState.update {
                ScreenState.INDIE
            }
        }

    }

    fun changePreviewExtractTextPage(index: Int) {
        _extractTextPagesState[index] = !_extractTextPagesState[index]


    }

    fun extractText(uri: Uri) {
        try {
            var parsedText = ""
            val reader = PdfReader(application.contentResolver.openInputStream(uri))
            val n: Int = reader.numberOfPages
            for (i in 0 until n) {
                parsedText = """
            $parsedText${PdfTextExtractor.getTextFromPage(reader, i + 1).trim { it <= ' ' }}
            
            """.trimIndent() //Extracting the content from the different pages
            }
            println(parsedText)
            reader.close()
        } catch (e: java.lang.Exception) {
            println(e)
        }
    }

    fun extractText(filename: String) {
        viewModelScope.launch {
            _screenState.update {
                ScreenState.LOADING
            }
            val out = File(saveLocation, "$filename.txt")
            withContext(Dispatchers.IO) {
                BufferedWriter(
                    OutputStreamWriter(
                        out.outputStream(), "utf-8"
                    )
                ).use { writer ->

                    try {
                        var parsedText = ""
                        val reader =
                            PdfReader(application.contentResolver.openInputStream(extractTextUri))
                        val n: Int = reader.numberOfPages
                        for (i in 0 until n) {
                            parsedText = """
            $parsedText${PdfTextExtractor.getTextFromPage(reader, i + 1).trim { it <= ' ' }}
            
            """.trimIndent() //Extracting the content from the different pages
                            writer.write(parsedText)
                        }
//                    println(parsedText)
                        reader.close()
                    } catch (e: Exception) {
                        println(e)
                    }


                }
            }

            _notification.update {
                Notification(
                    application.getString(R.string.save_success), String.format(
                        application.getString(
                            R.string.save_location
                        ), "${filename}.txt"
                    )
                )
            }
            _screenState.update {
                ScreenState.NOTIFICATION
            }
        }
    }

    fun clearCache() {
        _listImageToPDF.clear()
        _splitPages.clear()
        _filesToMerge.clear()
        _extractTextPages.clear()
        _listImageToPDF.clear()
        _pdfPageReorder.clear()
    }

    @Throws(IOException::class, DocumentException::class)
    fun compressPdf(uri: Uri) {
        viewModelScope.launch {
            _screenState.update {
                ScreenState.LOADING
            }
            val reader = PdfReader(application.contentResolver.openInputStream(uri))
            val stamper = PdfStamper(
                reader, application.contentResolver.openOutputStream(uri), PdfWriter.VERSION_1_5
            )
            stamper.writer.compressionLevel = 9
            val total = reader.numberOfPages + 1
            for (i in 1 until total) {
                reader.setPageContent(i, reader.getPageContent(i))
            }
            stamper.setFullCompression()
            stamper.close()
            reader.close()

            _notification.update {
                Notification(application.getString(R.string.save_success), "Optimize Done")
            }
            _screenState.update {
                ScreenState.NOTIFICATION
            }
        }

    }

    fun addStaredFile(file: PDFFile) {
        viewModelScope.launch(Dispatchers.IO) {
            val searchResult = _staredFiles.value.find {
                it.filename == file.filename
            }
            if (searchResult != null) {
                repository.removeStarFile(searchResult)
            } else {
                repository.addStaredFile(file.toPDFFileEntity())
            }
            _pdfFiles.value = _pdfFiles.value.toMutableList().apply {
                for (index in this.indices) {
                    if (this[index].filename == file.filename) {
                        this[index] = this[index].copy(isStared = !this[index].isStared)
                    }
                }

            }
        }

    }
}