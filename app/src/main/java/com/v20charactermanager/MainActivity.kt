package com.v20charactermanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.v20charactermanager.data.di.AppContainer
import com.v20charactermanager.ui.navigation.V20NavGraph
import com.v20charactermanager.ui.theme.V20Theme
import com.v20charactermanager.util.CrashHandler
import com.v20charactermanager.util.LocaleHelper
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {
    private var appContainer: AppContainer? = null
    private var crashLog: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try { enableEdgeToEdge() } catch (_: Exception) {}
        try { LocaleHelper.applySavedLocale(this) } catch (_: Exception) {}

        try {
            appContainer = AppContainer(applicationContext)
        } catch (e: Exception) {
            crashLog = buildCrashString("AppContainer init failed", e)
        }

        val crashFile = File(filesDir, "crash_logs").listFiles()?.firstOrNull()
        if (crashFile != null && crashLog == null) {
            try {
                crashLog = crashFile.readText()
                crashFile.delete()
            } catch (_: Exception) {}
        }

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val log = buildCrashString("Uncaught exception on thread: ${thread.name}", throwable)
                val dir = File(filesDir, "crash_logs")
                if (!dir.exists()) dir.mkdirs()
                File(dir, "crash_${System.currentTimeMillis()}.txt").writeText(log)
            } catch (_: Exception) {}
        }

        setContent {
            V20Theme {
                val currentCrash = crashLog
                if (currentCrash != null) {
                    CrashDialog(
                        log = currentCrash,
                        onDismiss = { crashLog = null },
                        onCopy = { text ->
                            val clip = ClipData.newPlainText("Crash Log", text)
                            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(this, "Copiato!", Toast.LENGTH_SHORT).show()
                        }
                    )
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

    private fun buildCrashString(label: String, throwable: Throwable): String {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        return "$label\n\n${throwable.javaClass.name}: ${throwable.message}\n\n$sw"
    }
}

@Composable
private fun CrashDialog(
    log: String,
    onDismiss: () -> Unit,
    onCopy: (String) -> Unit
) {
    val safeLog = remember(log) {
        log.take(50000)
            .replace("\u0000", "")
            .replace("\r\n", "\n")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Crash Report", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "Copia questo testo e incollalo allo sviluppatore:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = safeLog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onCopy(safeLog) }) {
                Text("COPIA LOG")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Chiudi")
            }
        }
    )
}
