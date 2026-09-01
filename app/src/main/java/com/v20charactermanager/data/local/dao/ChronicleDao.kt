package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.ChronicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChronicleDao {
    @Query("SELECT * FROM chronicles ORDER BY updatedAt DESC")
    fun getAllChronicles(): Flow<List<ChronicleEntity>>

    @Query("SELECT * FROM chronicles WHERE id = :id")
    fun getChronicleById(id: String): Flow<ChronicleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChronicle(chronicle: ChronicleEntity)

    @Update
    suspend fun updateChronicle(chronicle: ChronicleEntity)

    @Query("DELETE FROM chronicles WHERE id = :id")
    suspend fun deleteChronicle(id: String)

    @Query("SELECT COUNT(*) FROM chronicles")
    suspend fun getChronicleCount(): Int
}
