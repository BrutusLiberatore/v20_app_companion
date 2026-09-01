package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.ImageLayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageLayerDao {
    @Query("SELECT * FROM image_layers WHERE imageDocumentId = :documentId ORDER BY `order` ASC")
    fun getByDocumentId(documentId: String): Flow<List<ImageLayerEntity>>

    @Query("SELECT * FROM image_layers WHERE id = :id")
    suspend fun getById(id: String): ImageLayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(layer: ImageLayerEntity)

    @Update
    suspend fun update(layer: ImageLayerEntity)

    @Query("DELETE FROM image_layers WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM image_layers WHERE imageDocumentId = :documentId")
    suspend fun deleteByDocumentId(documentId: String)
}
