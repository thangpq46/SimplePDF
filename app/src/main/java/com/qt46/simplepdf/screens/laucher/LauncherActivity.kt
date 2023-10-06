package com.qt46.simplepdf.screens.laucher

import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.qt46.simplepdf.screens.laucher.ui.SplashScreen
import com.qt46.simplepdf.screens.main.MainActivity
import com.qt46.simplepdf.ui.theme.SimplePDFTheme

class LauncherActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SimplePDFTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    SplashScreen(shouldShowRationale = {
                        startActivity(Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", packageName, null)
                            addCategory(CATEGORY_DEFAULT)
                            addFlags(FLAG_ACTIVITY_NEW_TASK)
                            addFlags(FLAG_ACTIVITY_NO_HISTORY)
                            addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        })
                    }) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

}
