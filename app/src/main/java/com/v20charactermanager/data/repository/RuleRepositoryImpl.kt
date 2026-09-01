package com.v20charactermanager.data.repository

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.repository.RuleRepository

class RuleRepositoryImpl : RuleRepository {

    override fun getAllAttributeDefinitions(): List<AttributeId> =
        AttributeId.entries.toList()

    override fun getAllAbilityDefinitions(): List<AbilityId> =
        AbilityId.entries.toList()

    override fun getAllDisciplineDefinitions(): List<DisciplineId> =
        DisciplineId.entries.toList()

    override fun getAllClanDefinitions(): List<ClanId> =
        ClanId.entries.toList()

    override fun getAllBackgroundDefinitions(): List<BackgroundId> =
        BackgroundId.entries.toList()

    override fun getAllVirtueDefinitions(): List<VirtueId> =
        VirtueId.entries.toList()

    override fun getAllNatureDefinitions(): List<NatureId> =
        NatureId.entries.toList()

    override fun getAllDemeanorDefinitions(): List<DemeanorId> =
        DemeanorId.entries.toList()

    override fun getAllMeritDefinitions(): List<MeritId> =
        MeritId.entries.toList()

    override fun getAllFlawDefinitions(): List<FlawId> =
        FlawId.entries.toList()

    override fun getGenerationDefinition(generation: Int): GenerationDefinition? =
        GenerationDefinition.forGeneration(generation)

    override fun getClanById(id: ClanId): ClanId? =
        ClanId.fromId(id.id)

    override fun getDisciplineById(id: DisciplineId): DisciplineId? =
        DisciplineId.fromId(id.id)

    override fun getMeritById(id: MeritId): MeritId? =
        MeritId.fromId(id.id)

    override fun getFlawById(id: FlawId): FlawId? =
        FlawId.fromId(id.id)

    override fun getMeritsByCategory(category: MeritCategory): List<MeritId> =
        MeritId.getByCategory(category)

    override fun getFlawsByCategory(category: FlawCategory): List<FlawId> =
        FlawId.getByCategory(category)
}
