package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.AudioPresetEntity

@Dao
interface AudioPresetDao {
    @Query("SELECT * FROM audio_presets WHERE chronicleId = :chronicleId ORDER BY createdAt ASC")
    suspend fun getByChronicleId(chronicleId: String): List<AudioPresetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: AudioPresetEntity)

    @Update
    suspend fun update(preset: AudioPresetEntity)

    @Query("DELETE FROM audio_presets WHERE id = :id")
    suspend fun deleteById(id: String)
}
