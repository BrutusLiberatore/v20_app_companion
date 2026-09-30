package com.v20charactermanager.ui.combat

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Short beep + vibration fired when the turn timer expires. */
object CombatTimerAlert {

    fun fire(context: Context) {
        try {
            ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90)
                .startTone(ToneGenerator.TONE_PROP_ACK, 350)
        } catch (_: Exception) {
            // Audio unavailable: the visual alert still shows
        }
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(VibratorManager::class.java)?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (vibrator?.hasVibrator() == true) {
                vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } catch (_: Exception) {
            // No vibrator: the beep (or the visual alert) still shows
        }
    }
}
