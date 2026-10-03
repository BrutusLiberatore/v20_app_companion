package com.v20charactermanager.ui.tutorial

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object TutorialPrefs {
    private const val FILE_NAME = "v20_tutorial"
    private const val KEY_DONE = "tutorial_done"
    private const val KEY_LANGUAGE_DONE = "language_chosen"

    fun isDone(context: Context): Boolean =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DONE, false)

    fun setDone(context: Context) {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DONE, true)
            .apply()
    }

    fun isLanguageChosen(context: Context): Boolean =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_LANGUAGE_DONE, false)

    fun setLanguageChosen(context: Context) {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_LANGUAGE_DONE, true)
            .apply()
    }
}

object TutorialState {
    var visible by mutableStateOf(false)
}
