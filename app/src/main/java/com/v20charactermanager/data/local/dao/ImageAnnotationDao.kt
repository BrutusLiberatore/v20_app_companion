package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.ImageAnnotationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageAnnotationDao {
    @Query("SELECT * FROM image_annotations WHERE imageDocumentId = :documentId")
    fun getByDocumentId(documentId: String): Flow<List<ImageAnnotationEntity>>

    @Query("SELECT * FROM image_annotations WHERE layerId = :layerId")
    fun getByLayerId(layerId: String): Flow<List<ImageAnnotationEntity>>

    @Query("SELECT * FROM image_annotations WHERE id = :id")
    suspend fun getById(id: String): ImageAnnotationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(annotation: ImageAnnotationEntity)

    @Update
    suspend fun update(annotation: ImageAnnotationEntity)

    @Query("DELETE FROM image_annotations WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM image_annotations WHERE imageDocumentId = :documentId")
    suspend fun deleteByDocumentId(documentId: String)

    @Query("DELETE FROM image_annotations WHERE layerId = :layerId")
    suspend fun deleteByLayerId(layerId: String)
}
