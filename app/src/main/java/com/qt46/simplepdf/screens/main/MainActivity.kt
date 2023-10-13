package com.qt46.simplepdf.screens.main

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.qt46.simplepdf.R
import com.qt46.simplepdf.constants.TOOL_BROWSE_PDF
import com.qt46.simplepdf.constants.TOOL_EDIT_META
import com.qt46.simplepdf.constants.TOOL_EXTRACT_IMAGE
import com.qt46.simplepdf.constants.TOOL_EXTRACT_TEXT
import com.qt46.simplepdf.constants.TOOL_IMAGE_TO_PDF
import com.qt46.simplepdf.constants.TOOL_MERGE_PDF
import com.qt46.simplepdf.constants.TOOL_OPTIMIZE
import com.qt46.simplepdf.constants.TOOL_REORDER
import com.qt46.simplepdf.constants.TOOL_SPLIT_PDF
import com.qt46.simplepdf.constants.items
import com.qt46.simplepdf.constants.tools
import com.qt46.simplepdf.data.PDFFile
import com.qt46.simplepdf.data.Screen
import com.qt46.simplepdf.data.ScreenState
import com.qt46.simplepdf.data.SearchBarStatus
import com.qt46.simplepdf.screens.main.ui.AllPDFFiles
import com.qt46.simplepdf.screens.main.ui.BottomDialog
import com.qt46.simplepdf.screens.main.ui.EditMetaDataUI
import com.qt46.simplepdf.screens.main.ui.ExtractImageUI
import com.qt46.simplepdf.screens.main.ui.ExtractTextScreen
import com.qt46.simplepdf.screens.main.ui.ImageToPDFScreen
import com.qt46.simplepdf.screens.main.ui.LoadingScreen
import com.qt46.simplepdf.screens.main.ui.MergeScreen
import com.qt46.simplepdf.screens.main.ui.OptimizePDFUI
import com.qt46.simplepdf.screens.main.ui.ReOrderPage
import com.qt46.simplepdf.screens.main.ui.SelectFileMergeScreen
import com.qt46.simplepdf.screens.main.ui.SplitScreen
import com.qt46.simplepdf.screens.main.ui.StaredFiles
import com.qt46.simplepdf.screens.pdfviewer.PDFViewer
import com.qt46.simplepdf.ui.theme.SimplePDFTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private var toolSelected = TOOL_MERGE_PDF

    private fun NavOptionsBuilder.popUpToTop(navController: NavController) {
        popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
            inclusive = true
        }
    }

    private lateinit var navController: NavHostController

    private val selectImagesActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data

                //If multiple image selected
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0
//                    val list = mutableListOf<Uri>()
                    when (toolSelected) {
                        TOOL_IMAGE_TO_PDF -> {
                            navController.navigate(Screen.ImageToPDF.route)
                            for (i in 0 until count) {
                                data.clipData?.getItemAt(i)?.uri?.let {
                                    viewModel.addImageToPDF(it)
                                }
                            }
                        }
                    }

//                    viewModel.merge(list)
                }
                //If single image selected
                else if (data?.data != null) {

                    val imageUri: Uri? = data.data
                    imageUri?.let {
                        when (toolSelected) {
                            TOOL_SPLIT_PDF -> {
                                navController.navigate(Screen.SplitFile.route)
                                viewModel.initPreviewSplit(it)
                            }

                            TOOL_REORDER -> {
                                navController.navigate(Screen.ReOrderPage.route)
                                viewModel.initReorderPage(it)
                            }

                            TOOL_EXTRACT_TEXT -> {
                                navController.navigate(Screen.ExtractText.route)
                                viewModel.initExtractText(it)
                            }

                            TOOL_EDIT_META -> {
                                navController.navigate(Screen.EditMetaData.route)
                                viewModel.initMetaData(it)
                            }

                            TOOL_OPTIMIZE -> {
                                navController.navigate(Screen.Optimize.route)
                                viewModel.compressPdf(it)
                            }

                            TOOL_IMAGE_TO_PDF -> {
                                navController.navigate(Screen.ImageToPDF.route)
                                viewModel.addImageToPDF(it)
                            }

                            TOOL_BROWSE_PDF -> {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        PDFViewer::class.java
                                    ).apply {
                                        putExtra("uri_doc", it.toString())
                                    })
                            }

                            TOOL_EXTRACT_IMAGE -> {
                                navController.navigate(Screen.ExtractImage.route)
                                viewModel.initExtractImage(it)

                            }
                        }
                    }
                }
            }

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        MobileAds.initialize(this
//        ) { }
        viewModel.loadAllPDF()
        setContent {
            navController = rememberNavController()

            SimplePDFTheme {
                val screenState by viewModel.screenState.collectAsState()
                val notification by viewModel.notification.collectAsState()
                Scaffold(modifier = Modifier
                    .background(MaterialTheme.colorScheme.background),
                    bottomBar = {
                        BottomNavigation(backgroundColor = MaterialTheme.colorScheme.surface) {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            items.forEach { screen ->
                                BottomNavigationItem(
                                    icon = {
                                        Icon(
                                            painterResource(id = screen.iconId),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    label = {
                                        Text(
                                            stringResource(screen.resourceId),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            this.popUpToTop(navController)
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
//                                            popUpTo(navController.graph.findStartDestination().id) {
//                                                saveState = true
//                                            }
                                            // Avoid multiple copies of the same destination when
                                            // reselecting the same item
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->

                    NavHost(
                        navController,
                        startDestination = Screen.Tools.route,
                        Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Tools.route) {
                            MainScreenUI { tool ->
                                when (tool) {
                                    TOOL_MERGE_PDF -> {
                                        navController.navigate(Screen.Merge.route)
                                    }

                                    TOOL_SPLIT_PDF -> {
                                        toolSelected = TOOL_SPLIT_PDF
                                        selectImagesActivityResult.launch(Intent(ACTION_GET_CONTENT).apply {
                                            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                                            type = "application/pdf"
                                        })
                                    }

                                    TOOL_REORDER -> {
                                        toolSelected = TOOL_REORDER
                                        selectImagesActivityResult.launch(
                                            Intent(
                                                ACTION_OPEN_DOCUMENT
                                            ).apply {
                                                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                                                type = "application/pdf"
                                            })

                                    }

                                    TOOL_IMAGE_TO_PDF -> {
                                        toolSelected = TOOL_IMAGE_TO_PDF
                                        selectImagesActivityResult.launch(Intent(ACTION_GET_CONTENT).apply {
                                            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                                            type = "image/*"
                                        })

                                    }

                                    TOOL_EXTRACT_IMAGE -> {
                                        toolSelected = TOOL_EXTRACT_IMAGE
                                        selectImagesActivityResult.launch(Intent(ACTION_GET_CONTENT).apply {
                                            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                                            type = "application/pdf"
                                        })
                                    }

                                    TOOL_EXTRACT_TEXT -> {
                                        toolSelected = TOOL_EXTRACT_TEXT
                                        selectImagesActivityResult.launch(Intent(ACTION_GET_CONTENT).apply {
                                            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                                            type = "application/pdf"
                                        })
                                    }

                                    TOOL_BROWSE_PDF -> {
                                        toolSelected = TOOL_BROWSE_PDF
                                        selectImagesActivityResult.launch(Intent(ACTION_GET_CONTENT).apply {
                                            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                                            type = "application/pdf"
                                        })
//                                        openFile()
                                    }

                                    TOOL_OPTIMIZE -> {
                                        toolSelected = TOOL_OPTIMIZE
                                        selectImagesActivityResult.launch(
                                            Intent(
                                                ACTION_OPEN_DOCUMENT
                                            ).apply {
                                                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                                                type = "application/pdf"
                                            })

                                    }

                                    TOOL_EDIT_META -> {
                                        toolSelected = TOOL_EDIT_META
                                        selectImagesActivityResult.launch(
                                            Intent(
                                                ACTION_OPEN_DOCUMENT
                                            ).apply {
                                                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                                                flags =
                                                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                                type = "application/pdf"
                                            })

                                    }
                                }
                            }
//                            AndroidView(
//                                modifier = Modifier.fillMaxWidth(),
//                                factory = { context ->
//                                    AdView(context).apply {
//                                        setAdSize(AdSize.BANNER)
//                                        adUnitId = context.getString(R.string.app_name)
//                                        loadAd(AdRequest.Builder().build())
//                                    }
//                                }
//                            )
                        }
                        composable(Screen.AllPDF.route) {
                            val textSearch by viewModel.searchText.collectAsState()
                            val pdfFiles by viewModel.pdfFilters.collectAsState()
                            val searchState by viewModel.searchBarWidgetStatus.collectAsState()

                            Column {
                                MainAppBar(
                                    stringResource(id = R.string.all_pdf),
                                    searchState,
                                    onSearchIconClick = viewModel::openSearchBar,
                                    textSearch,
                                    viewModel::filter,
                                    viewModel::closeSearchBar
                                ) {
                                    finish()
                                }
                                Spacer(modifier = Modifier.height(9.dp))

                                AllPDFFiles(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    pdfFiles,
                                    this@MainActivity::onClick,
                                    viewModel::addStaredFile
                                )
                            }

                        }
                        composable(Screen.More.route) {
                            Setting()
                        }
                        composable(Screen.Merge.route) {

                            MergeScreen(
                                viewModel.filesToMerge,
                                viewModel::merge,
                                viewModel::removeFileMerge,
                                viewModel::changeIndexFilesMerge, {
                                    navController.popBackStack()
                                }
                            ) {
                                navController.navigate(Screen.SelectFile.route)
                            }
                        }
                        composable(Screen.SelectFile.route) {
                            SelectFileMergeScreen(
                                files = viewModel.pdfFilters,
                                viewModel.searchBarWidgetStatus,
                                viewModel.searchText,
                                filesToMerge = viewModel.filesToMerge,
                                onClick = viewModel::selectFile,
                                viewModel::openSearchBar,
                                viewModel::filter,
                                viewModel::closeSearchBar
                            ) {
                                navController.popBackStack()
                            }

                        }
                        composable(Screen.SplitFile.route) {
                            SplitScreen(
                                viewModel.splitPages,
                                viewModel.splitPagesSelectState,
                                viewModel::splitPdf,
                                viewModel::changePageState
                            ) {
                                navController.popBackStack()
                            }
                        }
                        composable(Screen.ExtractImage.route) {
                            ExtractImageUI(
                                viewModel.extractImagePages,
                                viewModel::extractImgPageSelect,
                                viewModel::extractImage
                            ) {
                                navController.popBackStack()
                            }
                        }
                        composable(Screen.ImageToPDF.route) {
                            ImageToPDFScreen(
                                viewModel.listImageToPDF,
                                viewModel::imageToPdf,
                                viewModel::changeIndexFilesImage,
                                addFile = {
                                    selectImagesActivityResult.launch(Intent(ACTION_GET_CONTENT).apply {
                                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                                        type = "image/*"
                                    })
                                },
                                onRemoveIconCLicked = viewModel::removeImage
                            ) {
                                navController.popBackStack()
                            }
                        }
                        composable(Screen.ReOrderPage.route) {
                            ReOrderPage(
                                images = viewModel.pdfPageReorder,
                                viewModel::reOrderPages,
                                viewModel::reOrderIndexPages
                            ) {
                                navController.popBackStack()
                            }
                        }
                        composable(Screen.EditMetaData.route) {
                            EditMetaDataUI(viewModel) {
                                navController.popBackStack()
                            }
                        }
                        composable(Screen.Optimize.route) {

                            OptimizePDFUI()
                        }
                        composable(Screen.ExtractText.route) {
                            ExtractTextScreen(
                                viewModel.extractTextPages,
                                viewModel.extractTextPagesState,
                                onActionClicked = viewModel::extractText,
                                onClickPage = viewModel::changePreviewExtractTextPage
                            ) {
                                navController.popBackStack()
                            }
                        }
                        composable(Screen.Stared.route) {
                            val starFiles by viewModel.staredFiles.collectAsState()
                            val textSearch by viewModel.searchText.collectAsState()
                            val searchState by viewModel.searchBarWidgetStatus.collectAsState()
                            Column {

                                MainAppBar(
                                    stringResource(id = R.string.starred_file),
                                    searchState,
                                    onSearchIconClick = viewModel::openSearchBar,
                                    textSearch,
                                    viewModel::filter,
                                    viewModel::closeSearchBar
                                ) {
                                    finish()
                                }
                                Spacer(modifier = Modifier.height(9.dp))
                                StaredFiles(
                                    filtedPDFs = starFiles,
                                    onClickItems = this@MainActivity::onClick,
                                    onStarClicked = viewModel::addStaredFile
                                )
                            }

                        }
                    }
                }
                when (screenState) {
                    ScreenState.LOADING -> {
                        LoadingScreen()
                    }

                    ScreenState.NOTIFICATION -> {
                        BottomDialog(
                            dialogTitle = notification.title,
                            dialogText = notification.detail,
                            onConfirmation = this::dismissNotification
                        )

                    }

                    else -> {}
                }

            }
        }
    }


    private fun dismissNotification() {
        viewModel.dismissNotification()
        viewModel.clearCache()
        navController.popBackStack()
    }

    private fun onClick(file: PDFFile) {
        startActivity(Intent(this@MainActivity, PDFViewer::class.java).apply {
            putExtra("uri_doc", file.uri)
        })
    }
}

@Composable
fun MainScreenUI(onClickItems: (Int) -> Unit = {}) {
    val context = LocalContext.current
    Column {
        TopAppBar(navigationIcon = {
            Spacer(modifier = Modifier.width(9.dp))
            IconButton(onClick = { (context as Activity).finish() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "back")
            }

        }, title = {
            Text(
                "Tools", color = MaterialTheme.colorScheme.onSurface, style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
        }, backgroundColor = MaterialTheme.colorScheme.background)
        Spacer(modifier = Modifier.height(9.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp)
                .fillMaxHeight(.5f)
        ) {
            itemsIndexed(tools) { index, tool ->
                // Replace this with your item composable

                Button(
                    onClick = { onClickItems(index) }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Icon(

                            painter = painterResource(id = tool.resourceID),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier
                                .requiredSize(55.dp)
                                .background(tool.color, CircleShape)
                                .padding(vertical = 10.dp)
                        )


                        Text(
                            text = stringResource(id = tool.toolNameID),
                            textAlign = TextAlign.Center,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }


            }
        }
        TextButton(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(id = R.string.cant_find_tool),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

}


@Composable
//@Preview
fun MainAppBar(

    title: String,
    searchState: SearchBarStatus = SearchBarStatus.CLOSED,
    onSearchIconClick: () -> Unit = {},
    textSearch: String,
    onTextChange: (String) -> Unit,
    onClose: () -> Unit,
    onBackPress: () -> Unit
) {
    if (searchState == SearchBarStatus.CLOSED) {
        DefaultAppBar(title, onSearchIconClick, onBackPress)
    } else {
        SearchAppBar(textSearch, onTextChange, onTextChange, onClose)
    }
}

@Composable
fun DefaultAppBar(title: String, onSearchIconClick: () -> Unit, onBackPress: () -> Unit) {
    TopAppBar(navigationIcon = {
        IconButton(onClick = { onBackPress() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "back")
        }

    }, actions = {
        Icon(
            Icons.Default.Search,
            contentDescription = "search icon",
            modifier = Modifier.clickable {
                onSearchIconClick()
            })
        Spacer(modifier = Modifier.width(9.dp))
    }, title = {
        Text(
            title, color = MaterialTheme.colorScheme.onSurface, style = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        )
    }, backgroundColor = MaterialTheme.colorScheme.background)
}

@Composable
fun SearchAppBar(
    textSearch: String,
    onTextChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = AppBarDefaults.TopAppBarElevation, color = MaterialTheme.colorScheme.background
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = textSearch,
            onValueChange = { onTextChange(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable {
                        if (textSearch.isBlank()) {
                            onClose()
                        } else {
                            onTextChange("")
                        }

                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Icon"
                )
            },
            placeholder = {
                Text(
                    text = "Search...",
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(textSearch)
                }

            ), colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent
            )

        )
    }


}