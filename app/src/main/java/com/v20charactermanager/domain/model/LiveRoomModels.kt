package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LiveRoom(
    val id: String,
    val name: String,
    val masterName: String,
    val chronicleId: String,
    val port: Int,
    val host: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class ConnectedPlayer(
    val id: String,
    val name: String,
    val characterId: String? = null,
    val characterName: String? = null,
    val connectedAt: Long = System.currentTimeMillis()
)

@Serializable
data class LiveRoomState(
    val room: LiveRoom? = null,
    val isMaster: Boolean = false,
    val connectedPlayers: List<ConnectedPlayer> = emptyList(),
    val localPlayer: ConnectedPlayer? = null,
    val presentedFile: PresentedFile? = null,
    val isFileFullscreen: Boolean = false,
    val isConnected: Boolean = false,
    val error: String? = null,
    val chronicleAssets: List<MediaAsset> = emptyList(),
    val connectionStatus: String = "",
    val characterPortraits: Map<String, String> = emptyMap()
)

@Serializable
data class PresentedFile(
    val id: String,
    val name: String,
    val mimeType: String,
    val data: ByteArray = byteArrayOf(),
    val presentedAt: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PresentedFile) return false
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

@Serializable
sealed class LiveRoomMessage {
    @Serializable
    data class Join(val playerName: String, val characterId: String? = null) : LiveRoomMessage()

    @Serializable
    data class RoomInfo(val roomName: String, val masterName: String, val chronicleId: String) : LiveRoomMessage()

    @Serializable
    data class PlayerJoined(val playerName: String, val playerId: String, val characterId: String? = null) : LiveRoomMessage()

    @Serializable
    data class PlayerLeft(val playerName: String, val playerId: String) : LiveRoomMessage()

    @Serializable
    data class CharacterData(val characterId: String, val playerName: String, val characterJson: String) : LiveRoomMessage()

    @Serializable
    data class RequestCharacter(val characterId: String) : LiveRoomMessage()

    @Serializable
    data class StatUpdate(val characterId: String, val field: String, val intValue: Int? = null, val stringValue: String? = null) : LiveRoomMessage()

    @Serializable
    data class DiceRoll(val characterId: String, val playerName: String, val pool: String, val result: String, val dice: List<Int> = emptyList()) : LiveRoomMessage()

    @Serializable
    data class PresentFile(val fileName: String, val mimeType: String, val base64Data: String) : LiveRoomMessage()

    @Serializable
    data class DismissFile(val dummy: String = "") : LiveRoomMessage()

    @Serializable
    data class FullscreenFile(val isFullscreen: Boolean) : LiveRoomMessage()

    @Serializable
    data class Error(val message: String) : LiveRoomMessage()

    @Serializable
    data class PortraitUpdate(val characterId: String, val portraitUri: String) : LiveRoomMessage()

    @Serializable
    data class Welcome(val playerId: String, val roomName: String, val masterName: String, val players: List<PlayerInfo>) : LiveRoomMessage()

    @Serializable
    data class PlayerInfo(val id: String, val name: String, val characterId: String? = null, val characterName: String? = null)

    @Serializable
    object Ping : LiveRoomMessage()

    @Serializable
    object Pong : LiveRoomMessage()

    @Serializable
    object RoomClosed : LiveRoomMessage()
}
