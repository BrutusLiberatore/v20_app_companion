package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chronicles")
data class ChronicleEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val storytellerName: String,
    val userRole: String,
    val createdAt: Long,
    val updatedAt: Long
)
