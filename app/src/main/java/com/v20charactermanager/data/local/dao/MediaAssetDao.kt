package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.MediaAssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaAssetDao {
    @Query("SELECT * FROM media_assets WHERE chronicleId = :chronicleId ORDER BY createdAt DESC")
    fun getByChronicleId(chronicleId: String): Flow<List<MediaAssetEntity>>

    @Query("SELECT * FROM media_assets WHERE chronicleId = :chronicleId AND type = :type ORDER BY createdAt DESC")
    fun getByType(chronicleId: String, type: String): Flow<List<MediaAssetEntity>>

    @Query("SELECT * FROM media_assets WHERE id = :id")
    suspend fun getById(id: String): MediaAssetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: MediaAssetEntity)

    @Update
    suspend fun update(asset: MediaAssetEntity)

    @Query("DELETE FROM media_assets WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM media_assets WHERE chronicleId = :chronicleId")
    suspend fun deleteByChronicleId(chronicleId: String)
}
