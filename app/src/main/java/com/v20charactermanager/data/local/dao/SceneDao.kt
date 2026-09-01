package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.SceneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes WHERE chronicleId = :chronicleId ORDER BY `order` ASC, createdAt ASC")
    fun getScenesByChronicle(chronicleId: String): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE sessionId = :sessionId ORDER BY `order` ASC")
    fun getScenesBySession(sessionId: String): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :id")
    suspend fun getSceneById(id: String): SceneEntity?

    @Query("SELECT * FROM scenes WHERE sessionId = :sessionId AND status = 'ACTIVE' LIMIT 1")
    suspend fun getActiveScene(sessionId: String): SceneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScene(scene: SceneEntity)

    @Update
    suspend fun updateScene(scene: SceneEntity)

    @Query("DELETE FROM scenes WHERE id = :id")
    suspend fun deleteScene(id: String)

    @Query("DELETE FROM scenes WHERE chronicleId = :chronicleId")
    suspend fun deleteAllScenesByChronicle(chronicleId: String)

    @Query("SELECT MAX(`order`) FROM scenes WHERE sessionId = :sessionId")
    suspend fun getMaxOrder(sessionId: String): Int?
}
