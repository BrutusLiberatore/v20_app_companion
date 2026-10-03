package com.v20charactermanager.ui.tutorial

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.v20charactermanager.R
import kotlinx.coroutines.launch

private data class TutorialPage(
    val icon: ImageVector,
    val titleRes: Int,
    val bodyRes: Int
)

private val tutorialPages = listOf(
    TutorialPage(
        Icons.Default.LocalFireDepartment,
        R.string.tutorial_welcome_title,
        R.string.tutorial_welcome_body
    ),
    TutorialPage(
        Icons.Default.People,
        R.string.tutorial_home_title,
        R.string.tutorial_home_body
    ),
    TutorialPage(
        Icons.Default.ContactPage,
        R.string.tutorial_sheet_title,
        R.string.tutorial_sheet_body
    ),
    TutorialPage(
        Icons.Default.PersonAdd,
        R.string.tutorial_creation_title,
        R.string.tutorial_creation_body
    ),
    TutorialPage(
        Icons.Default.Casino,
        R.string.tutorial_dice_title,
        R.string.tutorial_dice_body
    ),
    TutorialPage(
        Icons.Default.MenuBook,
        R.string.tutorial_chronicle_title,
        R.string.tutorial_chronicle_body
    ),
    TutorialPage(
        Icons.Default.Shield,
        R.string.tutorial_combat_title,
        R.string.tutorial_combat_body
    ),
    TutorialPage(
        Icons.Default.TableRestaurant,
        R.string.tutorial_table_title,
        R.string.tutorial_table_body
    ),
    TutorialPage(
        Icons.Default.Settings,
        R.string.tutorial_settings_title,
        R.string.tutorial_settings_body
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TutorialOverlay() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { tutorialPages.size })
    val isLast = pagerState.currentPage == tutorialPages.lastIndex

    val finish = {
        TutorialPrefs.setDone(context)
        TutorialState.visible = false
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10f),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = finish) {
                    Text(stringResource(R.string.tutorial_skip))
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val item = tutorialPages[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(104.dp)
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        text = stringResource(item.titleRes),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(item.bodyRes),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                tutorialPages.forEachIndexed { index, _ ->
                    val selected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (selected) 10.dp else 8.dp)
                            .background(
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                },
                                shape = CircleShape
                            )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    if (isLast) {
                        finish()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }) {
                    Text(
                        stringResource(
                            if (isLast) R.string.tutorial_start else R.string.tutorial_next
                        )
                    )
                }
            }
        }
    }
}
