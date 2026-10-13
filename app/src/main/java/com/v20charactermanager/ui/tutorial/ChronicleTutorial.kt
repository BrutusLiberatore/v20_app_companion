package com.v20charactermanager.ui.tutorial

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.v20charactermanager.R

private const val CHRONICLE_TUTORIAL_FILE = "v20_chronicle_tutorial"
private const val CHRONICLE_TUTORIAL_KEY = "chronicle_tutorial_done"

object ChronicleTutorialPrefs {
    fun isDone(context: Context): Boolean =
        context.getSharedPreferences(CHRONICLE_TUTORIAL_FILE, Context.MODE_PRIVATE)
            .getBoolean(CHRONICLE_TUTORIAL_KEY, false)

    fun setDone(context: Context) {
        context.getSharedPreferences(CHRONICLE_TUTORIAL_FILE, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(CHRONICLE_TUTORIAL_KEY, true)
            .apply()
    }
}

private val chronicleTutorialSteps = listOf(
    SpotlightStep(
        null,
        R.string.ct_welcome_title,
        R.string.ct_welcome_body
    ),
    SpotlightStep(
        "chron_live",
        R.string.ct_live_title,
        R.string.ct_live_body
    ),
    SpotlightStep(
        "chron_people",
        R.string.ct_people_title,
        R.string.ct_people_body
    ),
    SpotlightStep(
        "chron_plots",
        R.string.ct_plots_title,
        R.string.ct_plots_body
    ),
    SpotlightStep(
        "chron_media",
        R.string.ct_media_title,
        R.string.ct_media_body
    ),
    SpotlightStep(
        "chron_audio",
        R.string.ct_audio_title,
        R.string.ct_audio_body
    ),
    SpotlightStep(
        "chron_more",
        R.string.ct_more_title,
        R.string.ct_more_body
    ),
    SpotlightStep(
        "chron_search",
        R.string.ct_search_title,
        R.string.ct_search_body
    ),
    SpotlightStep(
        null,
        R.string.ct_final_title,
        R.string.ct_final_body
    )
)

object ChronicleTutorialState {
    var visible by mutableStateOf(false)
    var stepIndex by mutableStateOf(0)

    fun open() {
        stepIndex = 0
        visible = true
    }
}

@Composable
fun ChronicleTutorialOverlay() {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val finish = {
        ChronicleTutorialPrefs.setDone(ctx)
        ChronicleTutorialState.visible = false
    }
    SpotlightOverlay(
        steps = chronicleTutorialSteps,
        stepIndex = ChronicleTutorialState.stepIndex,
        onSkip = finish,
        onNext = {
            if (ChronicleTutorialState.stepIndex >= chronicleTutorialSteps.lastIndex) {
                finish()
            } else {
                ChronicleTutorialState.stepIndex++
            }
        },
        lastButtonRes = R.string.table_tutorial_done_btn
    )
}
