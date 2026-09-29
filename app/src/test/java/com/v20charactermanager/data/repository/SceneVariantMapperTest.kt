package com.v20charactermanager.data.repository

import com.v20charactermanager.data.local.entity.SceneVariantEntity
import com.v20charactermanager.domain.model.SceneVariant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SceneVariantMapperTest {

    @Test
    fun `entity to domain converts csv asset ids`() {
        val entity = SceneVariantEntity(
            id = "v1",
            sceneId = "s1",
            name = "Durante l'attacco",
            assetIds = "a1,a2,a3",
            notes = "Luci rosse",
            isDefault = true,
            createdAt = 42L
        )
        val domain = entity.toDomain()
        assertEquals(listOf("a1", "a2", "a3"), domain.assetIds)
        assertEquals("Durante l'attacco", domain.name)
        assertEquals("Luci rosse", domain.notes)
        assertEquals(true, domain.isDefault)
        assertEquals(42L, domain.createdAt)
    }

    @Test
    fun `empty asset ids map to empty list`() {
        val domain = SceneVariantEntity(id = "v", sceneId = "s", name = "n", assetIds = "").toDomain()
        assertEquals(emptyList<String>(), domain.assetIds)
        assertNull(domain.notes)
        assertEquals(false, domain.isDefault)
    }

    @Test
    fun `round trip preserves all fields`() {
        val original = SceneVariant(
            id = "v9",
            sceneId = "s9",
            name = "Dopo l'incendio",
            assetIds = listOf("m1", "m2"),
            notes = "Cenere ovunque",
            isDefault = false,
            createdAt = 7L
        )
        assertEquals(original, original.toEntity().toDomain())
    }
}
