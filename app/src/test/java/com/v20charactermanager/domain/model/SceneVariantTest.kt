package com.v20charactermanager.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SceneVariantTest {

    private fun variant(id: String, sceneId: String, isDefault: Boolean = false) =
        SceneVariant(id = id, sceneId = sceneId, name = "Variant $id", isDefault = isDefault)

    @Test
    fun `withDefaultSelection sets only the target as default within the scene`() {
        val variants = listOf(
            variant("a", "s1", isDefault = true),
            variant("b", "s1"),
            variant("c", "s2", isDefault = true)
        )
        val result = variants.withDefaultSelection("s1", "b")
        assertTrue(result.first { it.id == "b" }.isDefault)
        assertFalse(result.first { it.id == "a" }.isDefault)
        assertTrue(result.first { it.id == "c" }.isDefault)
    }

    @Test
    fun `withDefaultSelection with null clears the scene default only`() {
        val variants = listOf(
            variant("a", "s1", isDefault = true),
            variant("b", "s1"),
            variant("c", "s2", isDefault = true)
        )
        val result = variants.withDefaultSelection("s1", null)
        assertFalse(result.first { it.id == "a" }.isDefault)
        assertFalse(result.first { it.id == "b" }.isDefault)
        assertTrue(result.first { it.id == "c" }.isDefault)
    }

    @Test
    fun `withDefaultSelection leaves other scenes untouched`() {
        val variants = listOf(
            variant("x", "s2"),
            variant("y", "s2", isDefault = true)
        )
        val result = variants.withDefaultSelection("s1", "missing")
        assertEquals(variants, result)
    }
}
