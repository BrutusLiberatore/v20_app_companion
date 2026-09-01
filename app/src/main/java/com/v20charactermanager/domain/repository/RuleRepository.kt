package com.v20charactermanager.domain.repository

import com.v20charactermanager.domain.definition.*

interface RuleRepository {
    fun getAllAttributeDefinitions(): List<AttributeId>
    fun getAllAbilityDefinitions(): List<AbilityId>
    fun getAllDisciplineDefinitions(): List<DisciplineId>
    fun getAllClanDefinitions(): List<ClanId>
    fun getAllBackgroundDefinitions(): List<BackgroundId>
    fun getAllVirtueDefinitions(): List<VirtueId>
    fun getAllNatureDefinitions(): List<NatureId>
    fun getAllDemeanorDefinitions(): List<DemeanorId>
    fun getAllMeritDefinitions(): List<MeritId>
    fun getAllFlawDefinitions(): List<FlawId>
    fun getGenerationDefinition(generation: Int): GenerationDefinition?
    fun getClanById(id: ClanId): ClanId?
    fun getDisciplineById(id: DisciplineId): DisciplineId?
    fun getMeritById(id: MeritId): MeritId?
    fun getFlawById(id: FlawId): FlawId?
    fun getMeritsByCategory(category: MeritCategory): List<MeritId>
    fun getFlawsByCategory(category: FlawCategory): List<FlawId>
}
