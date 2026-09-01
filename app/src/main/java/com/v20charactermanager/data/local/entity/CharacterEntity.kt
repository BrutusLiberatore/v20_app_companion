package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val player: String,
    val chronicle: String,
    val profile: String,
    val clanId: String,
    val generation: Int,
    val natureId: String,
    val demeanorId: String,
    val sire: String,
    val haven: String,
    val concept: String,
    val attributesJson: String,
    val abilitiesJson: String,
    val disciplinesJson: String,
    val backgroundsJson: String,
    val virtuesJson: String,
    val moralPathJson: String,
    val meritsJson: String,
    val flawsJson: String,
    val healthJson: String,
    val bloodPoolJson: String,
    val willpowerJson: String,
    val experienceJson: String,
    val equipmentJson: String,
    val narrativeJson: String,
    val notes: String,
    val portraitUri: String?,
    val creationStep: Int,
    val isComplete: Boolean,
    val importMetadataJson: String?,
    val createdAt: Long,
    val updatedAt: Long
)
