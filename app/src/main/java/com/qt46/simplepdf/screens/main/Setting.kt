package com.qt46.simplepdf.screens.main


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.qt46.simplepdf.R
import com.qt46.simplepdf.constants.DEFAULT_MESSAGE_VALUE
import com.qt46.simplepdf.constants.INTENT_TITLE
import com.qt46.simplepdf.constants.MAIL_SUBJECT
import com.qt46.simplepdf.constants.MAIL_TO
import com.qt46.simplepdf.constants.OWNER_FACEBOOK
import com.qt46.simplepdf.constants.OWNER_GMAIL
import com.qt46.simplepdf.constants.OWNER_INSTAGRAM
import com.qt46.simplepdf.constants.OWNER_TIKTOK


@Composable
fun Setting(

) {
    val context = LocalContext.current
    val openAlertDialog = remember { mutableStateOf(false) }
    if (openAlertDialog.value) {
        AlertDialog(onDismissRequest = { openAlertDialog.value = false }, buttons = {
            androidx.compose.material3.TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { openAlertDialog.value = false }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        }, text = {
            Text(text = "asdghasjkhashdkkkjjjjjjjjjjjjjjjjjhsakdjasssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssjdkjasldhsjlassssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssshastlsajhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhsjdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd")
        })
    }
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
                            onClick = { (context as Activity).finish() },
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
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    AdView(context).apply {
                        setAdSize(AdSize.BANNER)
                        adUnitId = "ca-app-pub-3940256099942544/6300978111"
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
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
                    openAlertDialog.value = true
                }
            }
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    AdView(context).apply {
                        setAdSize(AdSize.LARGE_BANNER)
                        adUnitId = "ca-app-pub-3940256099942544/6300978111"
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
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