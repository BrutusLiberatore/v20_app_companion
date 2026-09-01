package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.v20charactermanager.domain.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LinkableItem(
    val id: String,
    val type: String,
    val name: String
)

enum class LinkCategory(val keyword: String, val label: String) {
    PG("PG", "Personaggi Giocanti"),
    NPC("NPC", "Personaggi Non Giocanti"),
    LUOGHI("LUOGHI", "Luoghi"),
    MAPPE("MAPPE", "Mappe e Immagini"),
    SEGRETI("SEGRETI", "Segreti"),
    INDIZI("INDIZI", "Indizi"),
    NOTE("NOTE", "Note"),
    SESSIONI("SESSIONI", "Sessioni"),
    FAZIONI("FAZIONI", "Fazioni"),
    EVENTI("EVENTI", "Eventi"),
    SCENE("SCENE", "Scene"),
    OGGETTI("OGGETTI", "Oggetti")
}

fun ChronicleDetailUiState.toLinkableItems(): List<LinkableItem> {
    val items = mutableListOf<LinkableItem>()
    members.filter { it.role == ChronicleMemberRole.PLAYER_CHARACTER }.forEach { m ->
        availableCharacters.find { it.id == m.characterId }?.let {
            items.add(LinkableItem(it.id, "PG", it.identity.name))
        }
    }
    npcs.forEach { items.add(LinkableItem(it.id, "NPC", it.name)) }
    locations.forEach { items.add(LinkableItem(it.id, "LUOGHI", it.name)) }
    secrets.forEach { items.add(LinkableItem(it.id, "SEGRETI", it.title)) }
    clues.forEach { items.add(LinkableItem(it.id, "INDIZI", it.title)) }
    notes.forEach { items.add(LinkableItem(it.id, "NOTE", it.text.take(40).ifEmpty { "Nota" })) }
    sessions.forEach { items.add(LinkableItem(it.id, "SESSIONI", "Sessione #${it.number}")) }
    factions.forEach { items.add(LinkableItem(it.id, "FAZIONI", it.name)) }
    events.forEach { items.add(LinkableItem(it.id, "EVENTI", it.title)) }
    scenes.forEach { items.add(LinkableItem(it.id, "SCENE", it.title)) }
    return items
}

private val LINK_REGEX = Regex("""\[(\w+):([^:]+):([^\]]+)\]""")

fun parseLinks(text: String): List<Pair<String, String>> {
    val results = mutableListOf<Pair<String, String>>()
    var lastEnd = 0
    for (match in LINK_REGEX.findAll(text)) {
        if (match.range.first > lastEnd) {
            results.add(Pair(text.substring(lastEnd, match.range.first), ""))
        }
        results.add(Pair("[${match.groupValues[1]}:${match.groupValues[2]}:${match.groupValues[3]}]", match.groupValues[3]))
        lastEnd = match.range.last + 1
    }
    if (lastEnd < text.length) {
        results.add(Pair(text.substring(lastEnd), ""))
    }
    return results
}

fun stripLinks(text: String): String {
    return text.replace(LINK_REGEX) { it.groupValues[3] }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkedTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    linkableItems: List<LinkableItem>,
    modifier: Modifier = Modifier,
    label: String? = null,
    minLines: Int = 2,
    placeholder: String? = null
) {
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }
    var showPopup by remember { mutableStateOf(false) }
    var activeCategory by remember { mutableStateOf<LinkCategory?>(null) }
    var query by remember { mutableStateOf("") }
    var cursorPosition by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    val filteredItems = remember(activeCategory, query, linkableItems) {
        if (activeCategory == null) return@remember emptyList()
        linkableItems.filter { item ->
            item.type.equals(activeCategory!!.keyword, ignoreCase = true) &&
                    item.name.contains(query, ignoreCase = true)
        }
    }

    Box(modifier) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                val oldText = textFieldValue.text
                val newText = newValue.text
                val newCursor = newValue.selection.end

                if (newCursor > 0 && newCursor <= newText.length) {
                    val textBeforeCursor = newText.substring(0, newCursor)
                    val atMatch = Regex("""@(\w*)$""").find(textBeforeCursor)

                    if (atMatch != null) {
                        val typed = atMatch.groupValues[1].uppercase()
                        val matchedCategory = LinkCategory.entries.find {
                            it.keyword.startsWith(typed, ignoreCase = true)
                        }
                        if (matchedCategory != null && typed.isNotEmpty()) {
                            activeCategory = matchedCategory
                            query = ""
                            showPopup = true
                            cursorPosition = newCursor
                        } else if (typed.isEmpty()) {
                            showPopup = false
                            activeCategory = null
                        } else {
                            debounceJob?.cancel()
                            debounceJob = scope.launch {
                                delay(150)
                                val cat = LinkCategory.entries.find {
                                    it.keyword.startsWith(typed, ignoreCase = true)
                                }
                                if (cat != null) {
                                    activeCategory = cat
                                    query = ""
                                    showPopup = true
                                    cursorPosition = newCursor
                                } else {
                                    showPopup = false
                                }
                            }
                        }
                    } else {
                        showPopup = false
                        activeCategory = null
                    }
                }

                textFieldValue = newValue
                onValueChange(newText)
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        showPopup = false
                    }
                },
            label = label?.let { { Text(it) } },
            minLines = minLines,
            placeholder = placeholder?.let { { Text(it) } }
        )

        if (showPopup && filteredItems.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.heightIn(max = 240.dp)) {
                    Text(
                        text = activeCategory?.label ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                    HorizontalDivider()
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(filteredItems) { item ->
                            ListItem(
                                headlineContent = { Text(item.name) },
                                supportingContent = { Text(item.type, style = MaterialTheme.typography.bodySmall) },
                                modifier = Modifier.clickable {
                                    val beforeCursor = textFieldValue.text.substring(0, cursorPosition)
                                    val afterCursor = textFieldValue.text.substring(cursorPosition)
                                    val tag = "[${item.type}:${item.id}:${item.name}]"
                                    val prefix = beforeCursor.substringBeforeLast("@")
                                    val newText = "$prefix$tag$afterCursor"
                                    val newPos = prefix.length + tag.length

                                    textFieldValue = TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newPos)
                                    )
                                    onValueChange(newText)
                                    showPopup = false
                                    activeCategory = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LinkedTextDisplay(
    text: String,
    linkableItems: List<LinkableItem>,
    onLinkClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val parts = remember(text) { parseLinks(text) }

    Column(modifier) {
        parts.forEach { (segment, linkName) ->
            if (linkName.isNotEmpty()) {
                val item = linkableItems.find { it.name == linkName }
                val typeName = segment.substringAfter("[").substringBefore(":")
                AssistChip(
                    onClick = {
                        item?.let { onLinkClick(it.type, it.id) }
                    },
                    label = { Text(linkName) },
                    leadingIcon = {
                        Text(
                            text = when (typeName) {
                                "PG" -> "👤"
                                "NPC" -> "🎭"
                                "LUOGHI" -> "📍"
                                "SEGRETI" -> "🔒"
                                "INDIZI" -> "🔍"
                                "NOTE" -> "📝"
                                "SESSIONI" -> "📅"
                                "FAZIONI" -> "⚔️"
                                "EVENTI" -> "⚡"
                                "SCENE" -> "🎬"
                                else -> "📎"
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.padding(2.dp)
                )
            } else if (segment.isNotBlank()) {
                Text(
                    text = segment,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}
