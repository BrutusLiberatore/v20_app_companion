package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class V20File(
    val formatId: String = "v20-character",
    val schemaVersion: Int = 1,
    val character: Character
)

object CharacterExporter {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun export(character: Character): String {
        val file = V20File(character = character)
        return json.encodeToString(file)
    }
}

object CharacterImporter {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    data class ImportResult(
        val success: Boolean,
        val character: Character? = null,
        val error: String? = null
    )

    fun import(jsonString: String): ImportResult {
        return try {
            val file = json.decodeFromString<V20File>(jsonString)
            if (file.formatId != "v20-character") {
                return ImportResult(false, error = "Invalid format ID")
            }
            if (file.schemaVersion > 1) {
                return ImportResult(false, error = "Incompatible schema version")
            }
            val character = file.character.copy(
                id = java.util.UUID.randomUUID().toString(),
                portraitUri = null,
                importMetadata = ImportMetadata(
                    importedAt = System.currentTimeMillis(),
                    sourceSchemaVersion = file.schemaVersion
                )
            )
            ImportResult(true, character = character)
        } catch (e: Exception) {
            ImportResult(false, error = "Invalid file: ${e.message}")
        }
    }
}

object CharacterFileValidator {

    fun validate(jsonString: String): Boolean {
        return try {
            val file = Json.decodeFromString<V20File>(jsonString)
            file.formatId == "v20-character" && file.schemaVersion <= 1
        } catch (e: Exception) {
            false
        }
    }
}
