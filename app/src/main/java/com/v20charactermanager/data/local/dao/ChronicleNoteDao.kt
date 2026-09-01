package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.ChronicleNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChronicleNoteDao {
    @Query("SELECT * FROM chronicle_notes WHERE chronicleId = :chronicleId ORDER BY updatedAt DESC")
    fun getNotesByChronicle(chronicleId: String): Flow<List<ChronicleNoteEntity>>

    @Query("SELECT * FROM chronicle_notes WHERE id = :id")
    fun getNoteById(id: String): Flow<ChronicleNoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ChronicleNoteEntity)

    @Update
    suspend fun updateNote(note: ChronicleNoteEntity)

    @Query("DELETE FROM chronicle_notes WHERE id = :id")
    suspend fun deleteNote(id: String)
}
