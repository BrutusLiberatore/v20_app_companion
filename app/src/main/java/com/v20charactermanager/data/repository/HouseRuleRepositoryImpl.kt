package com.v20charactermanager.data.repository

import com.v20charactermanager.data.local.dao.HouseRuleDao
import com.v20charactermanager.data.local.entity.HouseRuleEntity
import com.v20charactermanager.domain.model.HouseRules
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HouseRuleRepositoryImpl(
    private val houseRuleDao: HouseRuleDao
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getHouseRules(chronicleId: String): HouseRules {
        val entity = houseRuleDao.getByChronicleId(chronicleId)
        return if (entity != null) {
            try { json.decodeFromString(entity.rulesJson) } catch (_: Exception) { HouseRules.defaults(chronicleId) }
        } else {
            HouseRules.defaults(chronicleId)
        }
    }

    suspend fun saveHouseRules(rules: HouseRules) {
        val entity = HouseRuleEntity(
            chronicleId = rules.chronicleId,
            rulesJson = json.encodeToString(rules),
            updatedAt = System.currentTimeMillis()
        )
        houseRuleDao.insert(entity)
    }

    suspend fun deleteHouseRules(chronicleId: String) {
        houseRuleDao.deleteByChronicleId(chronicleId)
    }
}
