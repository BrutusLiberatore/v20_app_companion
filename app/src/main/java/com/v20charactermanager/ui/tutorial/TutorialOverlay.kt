package com.v20charactermanager.ui.tutorial

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.v20charactermanager.R

private val tutorialSteps = listOf(
    SpotlightStep(
        null,
        R.string.tutorial_welcome_title,
        R.string.tutorial_welcome_body
    ),
    SpotlightStep(
        "home_chars",
        R.string.tutorial_home_title,
        R.string.tutorial_home_body
    ),
    SpotlightStep(
        "home_create",
        R.string.tutorial_creation_title,
        R.string.tutorial_creation_body
    ),
    SpotlightStep(
        "home_dice",
        R.string.tutorial_dice_title,
        R.string.tutorial_dice_body
    ),
    SpotlightStep(
        "home_chronicles",
        R.string.tutorial_chronicle_title,
        R.string.tutorial_chronicle_body
    ),
    SpotlightStep(
        "home_combat",
        R.string.tutorial_combat_title,
        R.string.tutorial_combat_body
    ),
    SpotlightStep(
        "home_table",
        R.string.tutorial_table_title,
        R.string.tutorial_table_body
    ),
    SpotlightStep(
        "home_settings",
        R.string.tutorial_settings_title,
        R.string.tutorial_settings_body
    )
)

@Composable
fun TutorialOverlay() {
    val context = LocalContext.current

    val finish = {
        TutorialPrefs.setDone(context)
        TutorialState.visible = false
    }

    SpotlightOverlay(
        steps = tutorialSteps,
        stepIndex = TutorialState.stepIndex,
        onSkip = finish,
        onNext = {
            if (TutorialState.stepIndex >= tutorialSteps.lastIndex) {
                finish()
            } else {
                TutorialState.stepIndex++
            }
        },
        lastButtonRes = R.string.tutorial_start
    )
}
