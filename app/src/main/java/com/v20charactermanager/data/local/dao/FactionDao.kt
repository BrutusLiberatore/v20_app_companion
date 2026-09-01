package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.FactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FactionDao {
    @Query("SELECT * FROM factions WHERE chronicleId = :chronicleId ORDER BY name ASC")
    fun getFactionsByChronicle(chronicleId: String): Flow<List<FactionEntity>>

    @Query("SELECT * FROM factions WHERE id = :id")
    suspend fun getFactionById(id: String): FactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaction(faction: FactionEntity)

    @Update
    suspend fun updateFaction(faction: FactionEntity)

    @Query("DELETE FROM factions WHERE id = :id")
    suspend fun deleteFaction(id: String)

    @Query("DELETE FROM factions WHERE chronicleId = :chronicleId")
    suspend fun deleteAllFactionsByChronicle(chronicleId: String)
}
