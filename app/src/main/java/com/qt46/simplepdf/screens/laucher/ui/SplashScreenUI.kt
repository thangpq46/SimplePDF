package com.qt46.simplepdf.screens.laucher.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.provider.Settings
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.qt46.simplepdf.R
import com.qt46.simplepdf.constants.FiraSansFamily
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Preview(showBackground = true)
fun SplashScreen(shouldShowRationale: () -> Unit = {}, onHasPermission: () -> Unit = {}) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    val manageFilePermission = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

    )
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = hasPermission, block = {
        if (hasPermission) {
            delay(1200)
            onHasPermission()
        }
    })
    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f, animationSpec = tween(durationMillis = 800, easing = {
                OvershootInterpolator(4f).getInterpolation(it)
            })
        )
//        delay(3000L)
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_splash),
            contentDescription = "Logo",
            modifier = Modifier
                .scale(scale.value)
                .requiredSize(250.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(id = R.string.app_name),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontFamily = FiraSansFamily
            ),

            )

        Spacer(modifier = Modifier.height(130.dp))

    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(.8f),
            color = MaterialTheme.colorScheme.surfaceVariant,
            trackColor = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(30.dp))
    }


    if (SDK_INT >= Build.VERSION_CODES.R) {
        if (Environment.isExternalStorageManager()) {
            hasPermission = true
        } else { //request for the permission
            if (!hasPermission) {
                AlertDialog(icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_browse),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }, onDismissRequest = { }, title = {
                    Text(
                        color = MaterialTheme.colorScheme.primary,
                        text = stringResource(id = R.string.permission_require),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                }, text = {
                    Text(text = stringResource(id = R.string.alert_permission_denied))
                }, confirmButton = {
                    TextButton(onClick = {
                        if (Environment.isExternalStorageManager()) {
                            hasPermission = true
                        } else {
                            context.startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                                addCategory(Intent.CATEGORY_DEFAULT)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                            })
                        }

                    }) {
                        Text(stringResource(id = R.string.dialog_confirm))
                    }
                })
            }


        }
    } else {
        if (manageFilePermission.allPermissionsGranted) {
            hasPermission = true
        } else {


            if (manageFilePermission.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                AlertDialog(icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_browse),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }, onDismissRequest = { }, title = {
                    Text(
                        color = MaterialTheme.colorScheme.primary,
                        text = stringResource(id = R.string.permission_require),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                }, text = {
                    Text(text = stringResource(id = R.string.alert_permission_denied))
                }, confirmButton = {
                    TextButton(onClick = {
                        shouldShowRationale()
                    }) {
                        Text(stringResource(id = R.string.dialog_confirm))
                    }
                })
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required

                AlertDialog(icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_browse),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }, onDismissRequest = { }, title = {
                    Text(
                        color = MaterialTheme.colorScheme.primary,
                        text = stringResource(id = R.string.permission_require),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                }, text = {
                    Text(text = stringResource(id = R.string.ask_permission))
                }, confirmButton = {
                    TextButton(onClick = {
                        manageFilePermission.launchMultiplePermissionRequest()
                    }) {
                        Text(stringResource(id = R.string.dialog_grand))
                    }
                })

            }
        }
    }


}
