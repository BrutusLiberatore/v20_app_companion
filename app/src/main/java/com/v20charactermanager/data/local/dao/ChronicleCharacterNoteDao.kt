package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.ChronicleCharacterNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChronicleCharacterNoteDao {
    @Query("SELECT * FROM chronicle_character_notes WHERE chronicleId = :chronicleId AND characterId = :characterId")
    fun getNotesForCharacter(chronicleId: String, characterId: String): Flow<List<ChronicleCharacterNoteEntity>>

    @Query("SELECT * FROM chronicle_character_notes WHERE chronicleId = :chronicleId")
    fun getAllNotesByChronicle(chronicleId: String): Flow<List<ChronicleCharacterNoteEntity>>

    @Query("SELECT * FROM chronicle_character_notes WHERE id = :id")
    fun getNoteById(id: String): Flow<ChronicleCharacterNoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ChronicleCharacterNoteEntity)

    @Update
    suspend fun updateNote(note: ChronicleCharacterNoteEntity)

    @Query("DELETE FROM chronicle_character_notes WHERE id = :id")
    suspend fun deleteNote(id: String)
}
