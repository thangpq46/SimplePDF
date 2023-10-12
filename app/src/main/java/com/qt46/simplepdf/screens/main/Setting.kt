package com.qt46.simplepdf.screens.main


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.qt46.simplepdf.R
import com.qt46.simplepdf.constants.DEFAULT_MESSAGE_VALUE
import com.qt46.simplepdf.constants.INTENT_TITLE
import com.qt46.simplepdf.constants.MAIL_SUBJECT
import com.qt46.simplepdf.constants.MAIL_TO
import com.qt46.simplepdf.constants.OWNER_FACEBOOK
import com.qt46.simplepdf.constants.OWNER_GMAIL
import com.qt46.simplepdf.constants.OWNER_INSTAGRAM
import com.qt46.simplepdf.constants.OWNER_TIKTOK
import com.qt46.simplepdf.ui.theme.SimplePDFTheme


class SettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimplePDFTheme {
                // A surface container using the 'background' color from the theme

            }
        }
    }
}


@Composable
fun Setting(

){
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) {
        Column {
            TopAppBar(backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                    ) {
                        IconButton(
                            onClick = { },
                            enabled = true,
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.background
                            )

                        }
                    }
                }

                Row(
                    Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProvideTextStyle(value = MaterialTheme.typography.h6) {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.high,
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                text = stringResource(id = R.string.settings),
                                color = androidx.compose.material3.MaterialTheme.colorScheme.background
                            )
                        }
                    }
                }
            }
//                        Image(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .fillMaxHeight(.15f),
//                            painter = painterResource(id = R.drawable.ic_download),
//                            contentDescription = stringResource(
//                                id = R.string.ads
//                            )
//                        )
            SectionSetting(stringResource(id = R.string.sns_channel)) {
                SectionItem(
                    title = stringResource(id = R.string.instagram),
                    icon = R.drawable.ic_insta
                ) {
                    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(OWNER_INSTAGRAM)
                    })
                }
                SectionItem(
                    title = stringResource(id = R.string.tiktok),
                    icon = R.drawable.ic_tiktok
                ) {
                    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(OWNER_TIKTOK)
                    })
                }
                SectionItem(
                    title = stringResource(id = R.string.facebook),
                    icon = R.drawable.ic_fb
                ) {
                    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(OWNER_FACEBOOK)
                    })
                }
            }
            SectionSetting(stringResource(id = R.string.support)) {
                SectionItem(title = stringResource(id = R.string.contact)) {
                    context.sendMail()
                }
                SectionItem(title = stringResource(id = R.string.rating)) {

                }
            }
            SectionSetting(stringResource(id = R.string.other)) {
                SectionItem(title = stringResource(id = R.string.app_info)) {

                }
            }
        }
    }
}


@Composable
fun SectionSetting(
    title: String = "Section 1",
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
    ) {
        TextButton(onClick = { /*TODO*/ }, enabled = false) {
            Text(
                text = title,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }
        content()
    }
}

@Composable
fun SectionItem(title: String = "Instagram", icon: Int? = null, onClick: () -> Unit) {
    TextButton(
        modifier = Modifier
            .fillMaxWidth(), onClick = { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(
                Modifier.wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                icon?.let {
                    Icon(
                        painterResource(id = it),
                        contentDescription = null,
                        Modifier
                            .width(20.dp)
                            .height(20.dp),
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = title,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
            }
            Icon(
                 Icons.Default.ArrowForward,
                contentDescription = null,
                Modifier
                    .width(20.dp)
                    .height(20.dp),
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
            )
        }
    }

}

fun Context.sendMail() {
    try {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(MAIL_TO)
            putExtra(Intent.EXTRA_EMAIL, arrayOf(OWNER_GMAIL))
            putExtra(Intent.EXTRA_SUBJECT, MAIL_SUBJECT)
            putExtra(Intent.EXTRA_TEXT, DEFAULT_MESSAGE_VALUE)
        }
        startActivity(Intent.createChooser(emailIntent, INTENT_TITLE))
    } catch (e: ActivityNotFoundException) {
        // TODO: Handle case where no email app is available
    } catch (t: Throwable) {
        // TODO: Handle potential other type of exceptions
    }
}