package com.v20charactermanager.domain.repository

import com.v20charactermanager.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getAllCharacters(): Flow<List<Character>>
    fun getCharacterById(id: String): Flow<Character?>
    suspend fun getCharacterByIdOnce(id: String): Character?
    suspend fun insertCharacter(character: Character)
    suspend fun updateCharacter(character: Character)
    suspend fun deleteCharacter(id: String)
    suspend fun duplicateCharacter(id: String): Character?
    suspend fun getCharacterCount(): Int
}
