package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class CharacterExporterTest {

    private val character = Character(
        id = "test-export-1",
        identity = CharacterIdentity(
            name = "Export Test",
            clan = ClanId.BRUAH,
            generation = 13
        )
    )

    @Test
    fun `export produces valid JSON`() {
        val json = CharacterExporter.export(character)
        assertTrue(json.contains("v20-character"))
        assertTrue(json.contains("schemaVersion"))
        assertTrue(json.contains("Export Test"))
    }

    @Test
    fun `export contains all required fields`() {
        val json = CharacterExporter.export(character)
        assertTrue(json.contains("\"formatId\""))
        assertTrue(json.contains("\"schemaVersion\""))
        assertTrue(json.contains("\"character\""))
    }
}

class CharacterImporterTest {

    private val character = Character(
        id = "test-import-1",
        identity = CharacterIdentity(
            name = "Import Test",
            clan = ClanId.TOREADOR,
            generation = 10
        )
    )

    @Test
    fun `import valid file succeeds`() {
        val json = CharacterExporter.export(character)
        val result = CharacterImporter.import(json)
        assertTrue(result.success)
        assertNotNull(result.character)
        assertEquals("Import Test", result.character?.identity?.name)
    }

    @Test
    fun `import invalid JSON fails`() {
        val result = CharacterImporter.import("invalid json")
        assertFalse(result.success)
        assertNotNull(result.error)
    }

    @Test
    fun `import wrong format fails`() {
        val json = """{"formatId":"wrong","schemaVersion":1,"character":{"id":"x"}}"""
        val result = CharacterImporter.import(json)
        assertFalse(result.success)
        assertTrue(result.error?.contains("Invalid format ID") == true)
    }
}

class CharacterFileValidatorTest {

    @Test
    fun `valid file passes validation`() {
        val character = Character(
            id = "test-validate-1",
            identity = CharacterIdentity(name = "Validate Test")
        )
        val json = CharacterExporter.export(character)
        assertTrue(CharacterFileValidator.validate(json))
    }

    @Test
    fun `invalid JSON fails validation`() {
        assertFalse(CharacterFileValidator.validate("invalid json"))
    }

    @Test
    fun `wrong format fails validation`() {
        val json = """{"formatId":"wrong","schemaVersion":1,"character":{}}"""
        assertFalse(CharacterFileValidator.validate(json))
    }
}
