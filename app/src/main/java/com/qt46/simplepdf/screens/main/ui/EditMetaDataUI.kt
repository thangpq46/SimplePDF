package com.qt46.simplepdf.screens.main.ui


import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qt46.simplepdf.R
import com.qt46.simplepdf.screens.main.MainViewModel
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun EditMetaDataUI(viewModel: MainViewModel= androidx.lifecycle.viewmodel.compose.viewModel()){

    val title by viewModel.metaTitle.collectAsState()
    val author by viewModel.metaAuthor.collectAsState()
    val creator by viewModel.metaCreator.collectAsState()
    val producer by viewModel.metaProducer.collectAsState()
    val subject by viewModel.metaSubject.collectAsState()
    val keywords by viewModel.metaKeywords.collectAsState()
//    val createdDate by viewModel.metaCreationDate.collectAsState()
    val calendar = android.icu.util.Calendar.getInstance()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis,
        initialDisplayMode = DisplayMode.Picker
    )
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    var selectedDate by remember {
        mutableLongStateOf(calendar.timeInMillis) // or use mutableStateOf(calendar.timeInMillis)
    }
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.ROOT)
    Scaffold(modifier = Modifier
        .background(MaterialTheme.colorScheme.background), topBar = {
        TopAppBar(
            title = {
                Text(
                    "Edit METADATA",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            },
            navigationIcon = {
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            },
            actions = {
                TextButton(onClick = { /* doSomething() */ }) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        )
    }) {

        Surface(modifier = Modifier
            .padding(it)
            .padding(horizontal = 9.dp)) {
            Column {
                TextFieldWithLimitLength(
                    title,
                    viewModel::updateTitle,
                    label = stringResource(id = R.string.meta_title),
                    charLimit=100,
                    leadingIconId = R.drawable.ic_edit,
                )
                TextFieldWithLimitLength(
                    author,
                    viewModel::updateAuthor,
                    label = stringResource(id = R.string.meta_author),
                    charLimit=200,
                    leadingIconId = R.drawable.ic_author,
                )
                TextFieldWithLimitLength(
                    creator,
                    viewModel::updateCreator,
                    label = stringResource(id = R.string.meta_creator),
                    charLimit=200,
                    leadingIconId = R.drawable.ic_creator,
                )
                TextFieldWithLimitLength(
                    producer,
                    viewModel::updateProducer,
                    label = stringResource(id = R.string.meta_producer),
                    charLimit=200,
                    leadingIconId = R.drawable.ic_proceducer,
                )
                TextFieldWithLimitLength(
                    subject,
                    viewModel::updateSubject,
                    label = stringResource(id = R.string.meta_subject),
                    charLimit=150,
                    leadingIconId = R.drawable.ic_subject,
                )
                TextFieldWithLimitLength(
                    keywords,
                    viewModel::updateKeywords,
                    label = stringResource(id = R.string.meta_keyword),
                    leadingIconId = R.drawable.ic_keywords,
                    charLimit=200,
                )
//                TextField(modifier = Modifier
//                    .fillMaxWidth(),value = formatter.format(createdDate), onValueChange = {}, enabled = false,leadingIcon = {
//
//                    Icon(Icons.Default.DateRange, null)
//                }, label = {
//                           Text(text = stringResource(id = R.string.meta_date_mod))
//                },
//                    trailingIcon = {
//                        IconButton(onClick = { showDatePicker=true }) {
//                            Icon(Icons.Default.DateRange, null)
//                        }
//
//                    })
//                Spacer(modifier = Modifier.height(18.dp))
//                TextField(modifier = Modifier
//                    .fillMaxWidth(),value = formatter.format(createdDate), onValueChange = {}, enabled = false,leadingIcon = {
//
//                    Icon(Icons.Default.DateRange, null)
//                }, label = {
//                    Text(text = stringResource(id = R.string.meta_date_cre))
//                },
//                    trailingIcon = {
//                        IconButton(onClick = { showDatePicker=true }) {
//                            Icon(Icons.Default.DateRange, null)
//                        }
//
//                    })
                if (showDatePicker){
                    DatePickerDialog(
                        onDismissRequest = {
                            showDatePicker = false
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showDatePicker = false
                                selectedDate = datePickerState.selectedDateMillis!!
                            }) {
                                Text(text = "Confirm")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDatePicker = false
                            }) {
                                Text(text = "Cancel")
                            }
                        }
                    ) {
                        DatePicker(
                            state = datePickerState
                        )
                    }
                }



            }
        }

    }
}
