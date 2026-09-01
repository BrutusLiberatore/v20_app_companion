package com.v20charactermanager.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {
    private const val PREFS_NAME = "v20_locale"
    private const val KEY_LANGUAGE = "language"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLanguage(context: Context, language: String) {
        getPrefs(context).edit().putString(KEY_LANGUAGE, language).apply()
        applyLocale(language)
    }

    fun applySavedLocale(context: Context) {
        val language = getLanguage(context)
        applyLocale(language)
    }

    private fun applyLocale(language: String) {
        val locale = when (language) {
            "it" -> Locale("it")
            else -> Locale("en")
        }
        val localeList = LocaleListCompat.create(locale)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun isItalian(context: Context): Boolean {
        return getLanguage(context) == "it"
    }
}
