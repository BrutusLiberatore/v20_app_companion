package com.v20charactermanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v20charactermanager.domain.engine.DiceResult
import com.v20charactermanager.ui.theme.V20ErrorBright
import com.v20charactermanager.ui.theme.V20Gold
import com.v20charactermanager.ui.theme.V20GoldBright
import com.v20charactermanager.ui.theme.V20Success

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun V20DiceResult(
    result: DiceResult,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            result.individualResults.forEach { die ->
                val isOne = die == 1
                val isTen = die == 10

                val backgroundColor = when {
                    isOne -> V20ErrorBright
                    isTen -> V20GoldBright
                    else -> V20Gold.copy(alpha = 0.3f)
                }

                val textColor = when {
                    isOne -> Color.White
                    isTen -> Color(0xFF1A1206)
                    else -> MaterialTheme.colorScheme.onSurface
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(backgroundColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = die.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }

        val verdictText = when {
            result.isBotch -> "BOTCH"
            result.isSuccess -> "SUCCESS"
            else -> "FAILURE"
        }

        val verdictColor = when {
            result.isBotch -> V20ErrorBright
            result.isSuccess -> V20Success
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }

        Text(
            text = verdictText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = verdictColor,
            modifier = Modifier.padding(top = 12.dp)
        )

        Text(
            text = "Successes: ${result.successes} | Ones: ${result.ones}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
