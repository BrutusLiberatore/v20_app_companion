package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.NpcEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NpcDao {
    @Query("SELECT * FROM npcs WHERE chronicleId = :chronicleId ORDER BY name ASC")
    fun getNpcsByChronicle(chronicleId: String): Flow<List<NpcEntity>>

    @Query("SELECT * FROM npcs WHERE id = :id")
    suspend fun getNpcById(id: String): NpcEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNpc(npc: NpcEntity)

    @Update
    suspend fun updateNpc(npc: NpcEntity)

    @Query("DELETE FROM npcs WHERE id = :id")
    suspend fun deleteNpc(id: String)

    @Query("DELETE FROM npcs WHERE chronicleId = :chronicleId")
    suspend fun deleteAllNpcsByChronicle(chronicleId: String)
}
