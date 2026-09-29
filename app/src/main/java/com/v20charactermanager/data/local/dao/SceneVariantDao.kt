package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.SceneVariantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneVariantDao {
    @Query("SELECT * FROM scene_variants WHERE sceneId IN (SELECT id FROM scenes WHERE chronicleId = :chronicleId) ORDER BY createdAt ASC")
    fun getVariantsByChronicle(chronicleId: String): Flow<List<SceneVariantEntity>>

    @Query("SELECT * FROM scene_variants WHERE sceneId = :sceneId ORDER BY createdAt ASC")
    fun getVariantsByScene(sceneId: String): Flow<List<SceneVariantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariant(variant: SceneVariantEntity)

    @Update
    suspend fun updateVariant(variant: SceneVariantEntity)

    @Query("DELETE FROM scene_variants WHERE id = :id")
    suspend fun deleteVariant(id: String)

    @Query("DELETE FROM scene_variants WHERE sceneId = :sceneId")
    suspend fun deleteVariantsByScene(sceneId: String)
}
