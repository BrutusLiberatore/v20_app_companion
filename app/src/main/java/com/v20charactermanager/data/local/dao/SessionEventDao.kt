package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.SessionEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionEventDao {
    @Query("SELECT * FROM session_events WHERE chronicleId = :chronicleId ORDER BY timestamp DESC")
    fun getEventsByChronicle(chronicleId: String): Flow<List<SessionEventEntity>>

    @Query("SELECT * FROM session_events WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getEventsBySession(sessionId: String): Flow<List<SessionEventEntity>>

    @Query("SELECT * FROM session_events WHERE sessionId = :sessionId AND sceneId = :sceneId ORDER BY timestamp ASC")
    fun getEventsByScene(sessionId: String, sceneId: String): Flow<List<SessionEventEntity>>

    @Query("SELECT * FROM session_events WHERE id = :id")
    suspend fun getEventById(id: String): SessionEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: SessionEventEntity)

    @Update
    suspend fun updateEvent(event: SessionEventEntity)

    @Query("DELETE FROM session_events WHERE id = :id")
    suspend fun deleteEvent(id: String)

    @Query("DELETE FROM session_events WHERE chronicleId = :chronicleId")
    suspend fun deleteAllEvents(chronicleId: String)
}
