package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.model.EquipmentItem
import com.v20charactermanager.domain.model.EquipmentLibraryFile
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

object EquipmentLibraryEngine {
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    data class ExportResult(
        val success: Boolean,
        val jsonString: String? = null,
        val error: String? = null
    )

    data class ImportResult(
        val success: Boolean,
        val items: List<EquipmentItem> = emptyList(),
        val name: String = "",
        val description: String = "",
        val error: String? = null
    )

    fun export(
        items: List<EquipmentItem>,
        name: String = "Custom Equipment",
        description: String = ""
    ): ExportResult {
        return try {
            val library = EquipmentLibraryFile(
                name = name,
                description = description,
                items = items
            )
            ExportResult(
                success = true,
                jsonString = json.encodeToString(library)
            )
        } catch (e: Exception) {
            ExportResult(success = false, error = "Export failed: ${e.message}")
        }
    }

    fun import(jsonString: String): ImportResult {
        return try {
            val library = json.decodeFromString<EquipmentLibraryFile>(jsonString)
            if (library.formatId != "v20-equipment-library") {
                return ImportResult(success = false, error = "Invalid format: expected v20-equipment-library")
            }
            if (library.schemaVersion > 1) {
                return ImportResult(success = false, error = "Incompatible schema version: ${library.schemaVersion}")
            }
            val items = library.items.map {
                it.copy(id = java.util.UUID.randomUUID().toString())
            }
            ImportResult(
                success = true,
                items = items,
                name = library.name,
                description = library.description
            )
        } catch (e: Exception) {
            ImportResult(success = false, error = "Invalid file: ${e.message}")
        }
    }

    fun validate(jsonString: String): Boolean {
        return try {
            val library = json.decodeFromString<EquipmentLibraryFile>(jsonString)
            library.formatId == "v20-equipment-library" && library.schemaVersion <= 1
        } catch (e: Exception) {
            false
        }
    }
}
