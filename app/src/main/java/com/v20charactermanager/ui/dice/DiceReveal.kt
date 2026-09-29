package com.v20charactermanager.ui.dice

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.DiceRevealMode

/** Local persistence for the cinematic dice reveal preference. */
object DiceRevealPrefs {
    private const val FILE_NAME = "dice_reveal"
    private const val KEY_MODE = "mode"

    fun load(context: Context): DiceRevealMode {
        val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return DiceRevealMode.fromPrefValue(prefs.getString(KEY_MODE, null))
    }

    fun save(context: Context, mode: DiceRevealMode) {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_MODE, DiceRevealMode.toPrefValue(mode))
            .apply()
    }
}

/** Everything the cinematic reveal needs to present one roll. */
data class DiceRevealData(
    val playerName: String,
    val label: String,
    val verdict: String,
    val dice: List<Int>,
    val difficulty: Int,
    val isBotch: Boolean,
    val isCritical: Boolean
)

/** Radio list used both in the table menu dialog and in Settings. */
@Composable
fun DiceRevealModePicker(
    current: DiceRevealMode,
    onSelect: (DiceRevealMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        DiceRevealMode.entries.forEach { mode ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                RadioButton(
                    selected = current == mode,
                    onClick = { onSelect(mode) }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (mode) {
                        DiceRevealMode.OFF -> stringResource(R.string.dice_reveal_off)
                        DiceRevealMode.CRITICAL -> stringResource(R.string.dice_reveal_critical)
                        DiceRevealMode.EVERY -> stringResource(R.string.dice_reveal_every)
                    }
                )
            }
        }
    }
}
