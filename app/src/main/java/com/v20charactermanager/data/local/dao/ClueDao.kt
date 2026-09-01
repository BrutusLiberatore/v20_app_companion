package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.ClueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClueDao {
    @Query("SELECT * FROM clues WHERE chronicleId = :chronicleId ORDER BY createdAt ASC")
    fun getCluesByChronicle(chronicleId: String): Flow<List<ClueEntity>>

    @Query("SELECT * FROM clues WHERE id = :id")
    suspend fun getClueById(id: String): ClueEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClue(clue: ClueEntity)

    @Update
    suspend fun updateClue(clue: ClueEntity)

    @Query("DELETE FROM clues WHERE id = :id")
    suspend fun deleteClue(id: String)

    @Query("DELETE FROM clues WHERE chronicleId = :chronicleId")
    suspend fun deleteAllCluesByChronicle(chronicleId: String)
}
