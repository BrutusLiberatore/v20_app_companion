package com.v20charactermanager

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import com.v20charactermanager.data.di.AppContainer
import com.v20charactermanager.ui.navigation.V20NavGraph
import com.v20charactermanager.ui.theme.V20Theme
import com.v20charactermanager.util.LocaleHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        LocaleHelper.applySavedLocale(this)

        val appContainer = AppContainer(applicationContext)

        setContent {
            V20Theme {
                val navController = rememberNavController()
                V20NavGraph(
                    navController = navController,
                    appContainer = appContainer
                )
            }
        }
    }
}
