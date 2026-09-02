package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.AudioTrackEntity

@Dao
interface AudioTrackDao {
    @Query("SELECT * FROM audio_tracks WHERE chronicleId = :chronicleId ORDER BY createdAt ASC")
    suspend fun getByChronicleId(chronicleId: String): List<AudioTrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: AudioTrackEntity)

    @Update
    suspend fun update(track: AudioTrackEntity)

    @Query("DELETE FROM audio_tracks WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM audio_tracks WHERE chronicleId = :chronicleId")
    suspend fun deleteAllForChronicle(chronicleId: String)
}
