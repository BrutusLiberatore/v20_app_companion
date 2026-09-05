package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "house_rules")
data class HouseRuleEntity(
    @PrimaryKey
    val chronicleId: String,
    val rulesJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
