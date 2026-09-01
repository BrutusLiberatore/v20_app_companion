package com.v20charactermanager.ui.compendium

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.ui.theme.V20Gold
import com.v20charactermanager.util.LocaleHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompendiumDetailScreen(
    item: CompendiumItem,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isItalian = LocaleHelper.isItalian(context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isItalian) item.nameIt else item.nameEn,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isItalian) item.nameIt else item.nameEn,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            if (item.cost != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.compendium_cost),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${item.cost} ${stringResource(R.string.compendium_points)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = V20Gold
                    )
                }
            }

            val subCategory = if (isItalian) item.subCategoryIt else item.subCategoryEn
            if (subCategory != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.compendium_category),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = subCategory,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Text(
                text = stringResource(R.string.compendium_description),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = V20Gold
            )
            Text(
                text = if (isItalian) item.descriptionIt else item.descriptionEn,
                style = MaterialTheme.typography.bodyLarge
            )

            val extraInfo = if (isItalian) item.extraInfoIt else item.extraInfoEn
            if (extraInfo != null) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Text(
                    text = stringResource(R.string.compendium_additional_info),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = V20Gold
                )
                Text(
                    text = extraInfo,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
