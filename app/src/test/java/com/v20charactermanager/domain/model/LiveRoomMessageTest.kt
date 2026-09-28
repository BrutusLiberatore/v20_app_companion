package com.v20charactermanager.domain.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveRoomMessageTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `reveal handout round trip preserves fields`() {
        val msg = LiveRoomMessage.RevealHandout(
            id = "clue-1",
            title = "Chiave arrugginita",
            content = "Una chiave di ferro con il monogramma dei Ventrue.",
            kind = "clue"
        )
        val encoded = json.encodeToString(LiveRoomMessage.serializer(), msg)
        val decoded = json.decodeFromString(LiveRoomMessage.serializer(), encoded)
        assertEquals(msg, decoded)
    }

    @Test
    fun `reveal handout defaults kind to clue when field is absent`() {
        val encoded = json.encodeToString(
            LiveRoomMessage.serializer(),
            LiveRoomMessage.RevealHandout(id = "s1", title = "Segreto", content = "...", kind = "clue")
        )
        val withoutKind = encoded.replace(",\"kind\":\"clue\"", "")
        val decoded = json.decodeFromString(LiveRoomMessage.serializer(), withoutKind)
        assertTrue(decoded is LiveRoomMessage.RevealHandout)
        assertEquals("clue", (decoded as LiveRoomMessage.RevealHandout).kind)
    }

    @Test
    fun `reveal handout decodes kind secret`() {
        val encoded = json.encodeToString(
            LiveRoomMessage.serializer(),
            LiveRoomMessage.RevealHandout(id = "sec-1", title = "Segreto", content = "Il Principe mente.", kind = "secret")
        )
        val decoded = json.decodeFromString(LiveRoomMessage.serializer(), encoded)
        assertEquals("secret", (decoded as LiveRoomMessage.RevealHandout).kind)
    }

    @Test
    fun `live room state defaults revealed handout to null`() {
        assertEquals(null, LiveRoomState().revealedHandout)
    }
}
