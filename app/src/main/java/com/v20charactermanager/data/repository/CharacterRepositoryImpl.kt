package com.v20charactermanager.data.repository

import com.v20charactermanager.data.local.dao.CharacterDao
import com.v20charactermanager.data.local.entity.CharacterEntity
import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CharacterRepositoryImpl(
    private val characterDao: CharacterDao
) : CharacterRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun getAllCharacters(): Flow<List<Character>> {
        return characterDao.getAllCharacters().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCharacterById(id: String): Flow<Character?> {
        return characterDao.getCharacterById(id).map { it?.toDomain() }
    }

    override suspend fun getCharacterByIdOnce(id: String): Character? {
        return characterDao.getCharacterByIdSync(id)?.toDomain()
    }

    override suspend fun insertCharacter(character: Character) {
        characterDao.insertCharacter(character.toEntity())
    }

    override suspend fun updateCharacter(character: Character) {
        characterDao.updateCharacter(character.toEntity())
    }

    override suspend fun deleteCharacter(id: String) {
        characterDao.deleteCharacter(id)
    }

    override suspend fun duplicateCharacter(id: String): Character? {
        val original = characterDao.getCharacterByIdSync(id) ?: return null
        val newId = java.util.UUID.randomUUID().toString()
        val duplicate = original.toDomain().copy(
            id = newId,
            identity = original.toDomain().identity.copy(
                name = original.toDomain().identity.name + " (Copy)"
            ),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            importMetadata = null,
            portraitUri = null
        )
        insertCharacter(duplicate)
        return duplicate
    }

    override suspend fun getCharacterCount(): Int {
        return characterDao.getCharacterCount()
    }

    private fun CharacterEntity.toDomain(): Character {
        return Character(
            id = id,
            identity = CharacterIdentity(
                name = name,
                player = player,
                chronicle = chronicle,
                profile = profile,
                clan = ClanId.fromId(clanId) ?: ClanId.BRUAH,
                generation = generation,
                nature = NatureId.fromId(natureId) ?: NatureId.REBEL,
                demeanor = DemeanorId.fromId(demeanorId) ?: DemeanorId.CONFORMIST,
                sire = sire,
                haven = haven,
                concept = concept
            ),
            attributes = json.decodeFromString(attributesJson),
            abilities = json.decodeFromString(abilitiesJson),
            disciplines = json.decodeFromString(disciplinesJson),
            backgrounds = json.decodeFromString(backgroundsJson),
            virtues = json.decodeFromString(virtuesJson),
            moralPath = json.decodeFromString(moralPathJson),
            merits = json.decodeFromString(meritsJson),
            flaws = json.decodeFromString(flawsJson),
            health = json.decodeFromString(healthJson),
            bloodPool = json.decodeFromString(bloodPoolJson),
            willpower = json.decodeFromString(willpowerJson),
            experience = json.decodeFromString(experienceJson),
            equipment = json.decodeFromString(equipmentJson),
            narrative = json.decodeFromString(narrativeJson),
            notes = notes,
            portraitUri = portraitUri,
            creationStep = creationStep,
            isComplete = isComplete,
            importMetadata = importMetadataJson?.let { json.decodeFromString(it) },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun Character.toEntity(): CharacterEntity {
        return CharacterEntity(
            id = id,
            name = identity.name,
            player = identity.player,
            chronicle = identity.chronicle,
            profile = identity.profile,
            clanId = identity.clan.id,
            generation = identity.generation,
            natureId = identity.nature.id,
            demeanorId = identity.demeanor.id,
            sire = identity.sire,
            haven = identity.haven,
            concept = identity.concept,
            attributesJson = json.encodeToString(attributes),
            abilitiesJson = json.encodeToString(abilities),
            disciplinesJson = json.encodeToString(disciplines),
            backgroundsJson = json.encodeToString(backgrounds),
            virtuesJson = json.encodeToString(virtues),
            moralPathJson = json.encodeToString(moralPath),
            meritsJson = json.encodeToString(merits),
            flawsJson = json.encodeToString(flaws),
            healthJson = json.encodeToString(health),
            bloodPoolJson = json.encodeToString(bloodPool),
            willpowerJson = json.encodeToString(willpower),
            experienceJson = json.encodeToString(experience),
            equipmentJson = json.encodeToString(equipment),
            narrativeJson = json.encodeToString(narrative),
            notes = notes,
            portraitUri = portraitUri,
            creationStep = creationStep,
            isComplete = isComplete,
            importMetadataJson = importMetadata?.let { json.encodeToString(it) },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
