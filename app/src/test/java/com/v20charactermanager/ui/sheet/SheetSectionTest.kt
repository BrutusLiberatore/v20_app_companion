package com.v20charactermanager.ui.sheet

import org.junit.Assert.*
import org.junit.Test

class SheetSectionTest {

    @Test
    fun `empty order falls back to default`() {
        assertEquals(SheetSection.defaultOrder, SheetSection.parseOrder(""))
    }

    @Test
    fun `custom order is preserved and missing sections appended`() {
        val parsed = SheetSection.parseOrder("notes,attributes")

        assertEquals(SheetSection.NOTES, parsed[0])
        assertEquals(SheetSection.ATTRIBUTES, parsed[1])
        assertEquals(SheetSection.entries.size, parsed.size)
        assertEquals(parsed.size, parsed.distinct().size)
        SheetSection.entries.forEach { assertTrue(it in parsed) }
    }

    @Test
    fun `unknown tokens are dropped without crashing`() {
        val parsed = SheetSection.parseOrder("bogus, ,notes,bogus2")

        assertEquals(SheetSection.NOTES, parsed.first())
        assertEquals(SheetSection.entries.size, parsed.size)
        assertEquals(parsed.size, parsed.distinct().size)
    }

    @Test
    fun `serialize parse round trip keeps custom order`() {
        val custom = listOf(
            SheetSection.NOTES,
            SheetSection.OVERVIEW,
            SheetSection.ATTRIBUTES,
            SheetSection.ABILITIES,
            SheetSection.ADVANTAGES,
            SheetSection.DETAILS,
            SheetSection.MERITS_FLAWS,
            SheetSection.EQUIPMENT
        )
        assertEquals(custom, SheetSection.parseOrder(SheetSection.serializeOrder(custom)))
    }
}
