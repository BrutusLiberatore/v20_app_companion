package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.QuickNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickNoteDao {
    @Query("SELECT * FROM quick_notes WHERE chronicleId = :chronicleId ORDER BY createdAt DESC")
    fun getNotesByChronicle(chronicleId: String): Flow<List<QuickNoteEntity>>

    @Query("SELECT * FROM quick_notes WHERE chronicleId = :chronicleId AND scopeType = :scopeType ORDER BY createdAt DESC")
    fun getNotesByScope(chronicleId: String, scopeType: String): Flow<List<QuickNoteEntity>>

    @Query("SELECT * FROM quick_notes WHERE chronicleId = :chronicleId AND scopeId = :scopeId ORDER BY createdAt DESC")
    fun getNotesByScopeId(chronicleId: String, scopeId: String): Flow<List<QuickNoteEntity>>

    @Query("SELECT * FROM quick_notes WHERE id = :id")
    suspend fun getNoteById(id: String): QuickNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: QuickNoteEntity)

    @Update
    suspend fun updateNote(note: QuickNoteEntity)

    @Query("DELETE FROM quick_notes WHERE id = :id")
    suspend fun deleteNote(id: String)

    @Query("DELETE FROM quick_notes WHERE chronicleId = :chronicleId")
    suspend fun deleteAllNotes(chronicleId: String)
}
