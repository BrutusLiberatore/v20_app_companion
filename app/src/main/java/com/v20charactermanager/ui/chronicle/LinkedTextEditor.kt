package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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

enum class LinkCategory(val keywords: List<String>, val label: String) {
    PG(listOf("PG", "PC"), "Personaggi Giocanti"),
    NPC(listOf("NPC"), "Personaggi Non Giocanti"),
    LUOGHI(listOf("LUOGHI", "LUOGO", "LOC", "LOCATION"), "Luoghi"),
    MAPPE(listOf("MAPPE", "MAPPA", "MAP", "MAPS"), "Mappe e Immagini"),
    SEGRETI(listOf("SEGRETI", "SEGRETO", "SECRET"), "Segreti"),
    INDIZI(listOf("INDIZI", "INDIZIO", "CLUE"), "Indizi"),
    NOTE(listOf("NOTE", "NOTA", "NOTE"), "Note"),
    SESSIONI(listOf("SESSIONI", "SESSIONE", "SESSION"), "Sessioni"),
    FAZIONI(listOf("FAZIONI", "FAZIONE", "FACTION"), "Fazioni"),
    EVENTI(listOf("EVENTI", "EVENTO", "EVENT"), "Eventi"),
    SCENE(listOf("SCENE", "SCENA", "SCENE"), "Scene"),
    OGGETTI(listOf("OGGETTI", "OGGETTO", "ITEM", "OBJECT"), "Oggetti");

    companion object {
        fun findByKeyword(query: String): LinkCategory? {
            val upper = query.uppercase()
            return entries.find { cat ->
                cat.keywords.any { kw -> kw.startsWith(upper) }
            }
        }
    }
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
    notes.forEach { items.add(LinkableItem(it.id, "NOTE", it.text.take(40).ifEmpty { "Note" })) }
    sessions.forEach { items.add(LinkableItem(it.id, "SESSIONI", "Session #${it.number}")) }
    factions.forEach { items.add(LinkableItem(it.id, "FAZIONI", it.name)) }
    events.forEach { items.add(LinkableItem(it.id, "EVENTI", it.title)) }
    scenes.forEach { items.add(LinkableItem(it.id, "SCENE", it.title)) }
    return items
}

private val LINK_REGEX = Regex("""\[(\w+):([^:]+):([^\]]+)\]""")
private val TAG_REGEX = Regex("""#(\w+)""")
private val COMBINED_REGEX = Regex("""#(\w+)|\[(\w+):([^:]+):([^\]]+)\]""")

data class TextSegment(
    val text: String,
    val type: SegmentType,
    val id: String? = null
)

enum class SegmentType {
    PLAIN,
    LINK,
    TAG
}

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

fun parseTags(text: String): List<String> {
    return TAG_REGEX.findAll(text).map { it.groupValues[1] }.toList()
}

fun parseSegments(text: String): List<TextSegment> {
    val results = mutableListOf<TextSegment>()
    var lastEnd = 0
    for (match in COMBINED_REGEX.findAll(text)) {
        if (match.range.first > lastEnd) {
            results.add(TextSegment(text.substring(lastEnd, match.range.first), SegmentType.PLAIN))
        }
        if (match.groupValues[1].isNotEmpty()) {
            // Tag match
            results.add(TextSegment("#${match.groupValues[1]}", SegmentType.TAG))
        } else {
            // Link match
            val typeName = match.groupValues[2]
            val id = match.groupValues[3]
            val displayName = match.groupValues[4]
            results.add(TextSegment(displayName, SegmentType.LINK, id))
        }
        lastEnd = match.range.last + 1
    }
    if (lastEnd < text.length) {
        results.add(TextSegment(text.substring(lastEnd), SegmentType.PLAIN))
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
    var isTagMode by remember { mutableStateOf(false) }
    var tagQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    val filteredItems = remember(activeCategory, query, linkableItems) {
        if (activeCategory == null) return@remember emptyList()
        val typeUpper = activeCategory!!.keywords.first().uppercase()
        linkableItems.filter { item ->
            item.type.uppercase() == typeUpper &&
                    item.name.contains(query, ignoreCase = true)
        }
    }

    Box(modifier) {
        Column {
            if (isTagMode && tagQuery.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Text(
                        text = "Tag: #$tagQuery",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            OutlinedTextField(
                value = textFieldValue,
            onValueChange = { newValue ->
                val oldText = textFieldValue.text
                val newText = newValue.text
                val newCursor = newValue.selection.end

                if (newCursor > 0 && newCursor <= newText.length) {
                    val textBeforeCursor = newText.substring(0, newCursor)
                    val atMatch = Regex("""@(\w*)$""").find(textBeforeCursor)
                    val tagMatch = Regex("""#(\w*)$""").find(textBeforeCursor)

                    if (atMatch != null) {
                        val typed = atMatch.groupValues[1].uppercase()
                        val matchedCategory = LinkCategory.findByKeyword(typed)
                        if (matchedCategory != null && typed.isNotEmpty()) {
                            activeCategory = matchedCategory
                            query = ""
                            showPopup = true
                            isTagMode = false
                            cursorPosition = newCursor
                        } else if (typed.isEmpty()) {
                            showPopup = false
                            activeCategory = null
                        } else {
                            debounceJob?.cancel()
                            debounceJob = scope.launch {
                                delay(150)
                                val cat = LinkCategory.findByKeyword(typed)
                                if (cat != null) {
                                    activeCategory = cat
                                    query = ""
                                    showPopup = true
                                    isTagMode = false
                                    cursorPosition = newCursor
                                } else {
                                    showPopup = false
                                }
                            }
                        }
                    } else if (tagMatch != null) {
                        tagQuery = tagMatch.groupValues[1]
                        isTagMode = true
                        showPopup = false
                        activeCategory = null
                        cursorPosition = newCursor
                    } else {
                        showPopup = false
                        activeCategory = null
                        isTagMode = false
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
        }

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LinkedTextDisplay(
    text: String,
    linkableItems: List<LinkableItem>,
    onLinkClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val segments = remember(text) { parseSegments(text) }

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        segments.forEach { segment ->
            when (segment.type) {
                SegmentType.LINK -> {
                    val item = linkableItems.find { it.name == segment.text }
                    AssistChip(
                        onClick = {
                            item?.let { onLinkClick(it.type, it.id) }
                        },
                        label = { Text(segment.text) },
                        leadingIcon = {
                            Text(
                                text = "\uD83D\uDD17",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.padding(2.dp)
                    )
                }
                SegmentType.TAG -> {
                    Text(
                        text = segment.text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
                SegmentType.PLAIN -> {
                    if (segment.text.isNotBlank()) {
                        Text(
                            text = segment.text,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
