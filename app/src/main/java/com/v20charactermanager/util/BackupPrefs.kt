package com.v20charactermanager.util

import android.content.Context
import android.content.SharedPreferences

/** Timestamps of the daily automatic backup (24h cadence). */
object BackupPrefs {

    private const val FILE_NAME = "v20_backup_prefs"
    private const val KEY_LAST_AUTO_BACKUP_MS = "last_auto_backup_ms"
    private const val AUTO_BACKUP_INTERVAL_MS = 24L * 60L * 60L * 1000L

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun lastAutoBackupMs(context: Context): Long =
        prefs(context).getLong(KEY_LAST_AUTO_BACKUP_MS, 0L)

    fun isAutoDue(context: Context): Boolean {
        val last = lastAutoBackupMs(context)
        return last == 0L || System.currentTimeMillis() - last >= AUTO_BACKUP_INTERVAL_MS
    }

    fun markAutoDone(context: Context) {
        prefs(context).edit().putLong(KEY_LAST_AUTO_BACKUP_MS, System.currentTimeMillis()).apply()
    }
}
