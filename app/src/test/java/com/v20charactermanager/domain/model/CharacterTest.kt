package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.AbilityId
import com.v20charactermanager.domain.definition.AttributeId
import com.v20charactermanager.domain.definition.VirtueId
import org.junit.Assert.*
import org.junit.Test

class CharacterTest {

    private fun newCharacter() = Character(id = "test")

    @Test
    fun `attribute below minimum is clamped instead of throwing`() {
        val c = newCharacter().setAttributeValue(AttributeId.STRENGTH, 0)
        assertEquals(1, c.getAttributeValue(AttributeId.STRENGTH))
    }

    @Test
    fun `attribute above maximum is clamped`() {
        val c = newCharacter().setAttributeValue(AttributeId.WITS, 9)
        assertEquals(5, c.getAttributeValue(AttributeId.WITS))
    }

    @Test
    fun `elder generation allows traits above five`() {
        val c = Character(id = "test", identity = CharacterIdentity(generation = 7))
            .setAttributeValue(AttributeId.STRENGTH, 6)
        assertEquals(6, c.getAttributeValue(AttributeId.STRENGTH))
    }

    @Test
    fun `nosferatu appearance can be set to zero`() {
        val c = newCharacter().setAttributeValue(AttributeId.APPEARANCE, 0)
        assertEquals(0, c.getAttributeValue(AttributeId.APPEARANCE))
    }

    @Test
    fun `negative ability is clamped to zero instead of throwing`() {
        val c = newCharacter().setAbilityValue(AbilityId.ATHLETICS, -1)
        assertEquals(0, c.getAbilityValue(AbilityId.ATHLETICS))
    }

    @Test
    fun `virtue below minimum is clamped instead of throwing`() {
        val c = newCharacter().setVirtueValue(VirtueId.COURAGE, 0)
        assertEquals(1, c.getVirtueValue(VirtueId.COURAGE))
    }

    @Test
    fun `virtue above maximum is clamped`() {
        val c = newCharacter().setVirtueValue(VirtueId.COURAGE, 7)
        assertEquals(5, c.getVirtueValue(VirtueId.COURAGE))
    }
}
