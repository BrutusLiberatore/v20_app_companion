package com.v20charactermanager.util

import android.content.Context
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

object CrashHandler {

    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    private var crashCallback: ((String) -> Unit)? = null

    fun init(context: Context, onCrash: (String) -> Unit) {
        crashCallback = onCrash
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val log = buildCrashLog(thread, throwable)
            saveCrashToFile(context, log)
            crashCallback?.invoke(log)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    fun getCrashLogs(context: Context): List<Pair<String, String>> {
        val dir = File(context.filesDir, "crash_logs")
        if (!dir.exists()) return emptyList()
        return dir.listFiles()
            ?.sortedByDescending { it.lastModified() }
            ?.take(20)
            ?.map { file ->
                val date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    .format(Date(file.lastModified()))
                date to file.readText()
            }
            ?: emptyList()
    }

    fun clearCrashLogs(context: Context) {
        File(context.filesDir, "crash_logs").deleteRecursively()
    }

    private fun buildCrashLog(thread: Thread, throwable: Throwable): String {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        val stackTrace = sw.toString()

        return buildString {
            appendLine("=== V20 CRASH LOG ===")
            appendLine("Data: ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())}")
            appendLine("Dispositivo: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("App: 1.0")
            appendLine("Thread: ${thread.name}")
            appendLine()
            appendLine("=== ERRORE ===")
            appendLine("${throwable.javaClass.name}: ${throwable.message}")
            appendLine()
            appendLine("=== STACK TRACE ===")
            appendLine(stackTrace)
        }
    }

    private fun saveCrashToFile(context: Context, log: String) {
        try {
            val dir = File(context.filesDir, "crash_logs")
            if (!dir.exists()) dir.mkdirs()
            val fileName = "crash_${System.currentTimeMillis()}.txt"
            File(dir, fileName).writeText(log)
        } catch (_: Exception) {}
    }
}
