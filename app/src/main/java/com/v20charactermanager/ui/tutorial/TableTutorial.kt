package com.v20charactermanager.ui.tutorial

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.v20charactermanager.R

private const val TABLE_TUTORIAL_FILE = "v20_table_tutorial"
private const val TABLE_TUTORIAL_KEY = "table_tutorial_done"

object TableTutorialPrefs {
    fun isDone(context: Context): Boolean =
        context.getSharedPreferences(TABLE_TUTORIAL_FILE, Context.MODE_PRIVATE)
            .getBoolean(TABLE_TUTORIAL_KEY, false)

    fun setDone(context: Context) {
        context.getSharedPreferences(TABLE_TUTORIAL_FILE, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(TABLE_TUTORIAL_KEY, true)
            .apply()
    }
}

private val tableTutorialSteps = listOf(
    SpotlightStep(
        "tt_ip",
        R.string.tt_master_welcome_title,
        R.string.tt_master_welcome_body
    ),
    SpotlightStep(
        "tt_header",
        R.string.tt_master_players_title,
        R.string.tt_master_players_body
    ),
    SpotlightStep(
        "tt_present",
        R.string.tt_master_present_title,
        R.string.tt_master_present_body
    ),
    SpotlightStep(
        "tt_menu",
        R.string.tt_master_combat_title,
        R.string.tt_master_combat_body
    ),
    SpotlightStep(
        "tt_menu",
        R.string.tt_master_dice_title,
        R.string.tt_master_dice_body
    ),
    SpotlightStep(
        "tt_feed",
        R.string.tt_master_feed_title,
        R.string.tt_master_feed_body
    ),
    SpotlightStep(
        null,
        R.string.tt_master_final_title,
        R.string.tt_master_final_body
    )
)

object TableTutorialState {
    var visible by mutableStateOf(false)
    var stepIndex by mutableStateOf(0)

    fun open() {
        stepIndex = 0
        visible = true
    }
}

@Composable
fun TableTutorialOverlay() {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val finish = {
        TableTutorialPrefs.setDone(ctx)
        TableTutorialState.visible = false
    }
    SpotlightOverlay(
        steps = tableTutorialSteps,
        stepIndex = TableTutorialState.stepIndex,
        onSkip = finish,
        onNext = {
            if (TableTutorialState.stepIndex >= tableTutorialSteps.lastIndex) {
                finish()
            } else {
                TableTutorialState.stepIndex++
            }
        },
        lastButtonRes = R.string.table_tutorial_done_btn
    )
}
