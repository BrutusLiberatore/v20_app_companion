package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = ChronicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["chronicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("chronicleId")]
)
data class SessionEntity(
    @PrimaryKey
    val id: String,
    val chronicleId: String,
    val number: Int,
    val title: String,
    val date: Long,
    val status: String = "PLANNED",
    val realStartDateTime: Long? = null,
    val realEndDateTime: Long? = null,
    val inGameDate: String? = null,
    val activeSceneId: String? = null,
    val plannedSceneIds: String = "",
    val participantCharacterIds: String = "",
    val preparationNotes: String = "",
    val liveNotes: String = "",
    val recap: String = "",
    val xpAwarded: Int = 0,
    val unresolvedThreadIds: String = "",
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long
)
