package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE chronicleId = :chronicleId ORDER BY number DESC")
    fun getSessionsByChronicle(chronicleId: String): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE id = :id")
    fun getSessionById(id: String): Flow<SessionEntity?>

    @Query("SELECT * FROM sessions WHERE chronicleId = :chronicleId AND status = :status ORDER BY number DESC")
    fun getSessionsByStatus(chronicleId: String, status: String): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE chronicleId = :chronicleId AND status = 'ACTIVE' LIMIT 1")
    fun getActiveSession(chronicleId: String): Flow<SessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Update
    suspend fun updateSession(session: SessionEntity)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteSession(id: String)

    @Query("SELECT MAX(number) FROM sessions WHERE chronicleId = :chronicleId")
    suspend fun getMaxSessionNumber(chronicleId: String): Int?
}
