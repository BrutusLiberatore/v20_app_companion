package com.v20charactermanager

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.v20charactermanager.data.di.AppContainer
import com.v20charactermanager.ui.navigation.V20NavGraph
import com.v20charactermanager.ui.theme.V20Theme
import com.v20charactermanager.util.LocaleHelper
import java.io.File

class MainActivity : AppCompatActivity() {
    private var appContainer: AppContainer? = null
    private var crashLog: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            enableEdgeToEdge()
        } catch (_: Exception) { }

        try {
            LocaleHelper.applySavedLocale(this)
        } catch (_: Exception) { }

        try {
            appContainer = AppContainer(applicationContext)
        } catch (e: Exception) {
            crashLog = "AppContainer init failed:\n${e.stackTraceToString()}"
        }

        val crashFile = File(filesDir, "crash_log.txt")
        if (crashFile.exists() && crashLog == null) {
            crashLog = crashFile.readText()
            crashFile.delete()
        }

        setContent {
            V20Theme {
                if (crashLog != null) {
                    CrashScreen(crashLog!!) {
                        crashLog = null
                    }
                } else {
                    val navController = rememberNavController()
                    appContainer?.let { container ->
                        V20NavGraph(
                            navController = navController,
                            appContainer = container
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CrashScreen(log: String, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "V20 - Crash Report",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE57373)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Copiare questo testo e inviarlo allo sviluppatore",
            fontSize = 12.sp,
            color = Color(0xFFAAAAAA)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF1A1A1A)
        ) {
            Text(
                text = log,
                modifier = Modifier.padding(12.dp),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFCCCCCC),
                lineHeight = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Chiudi e riprova")
        }
    }
}
