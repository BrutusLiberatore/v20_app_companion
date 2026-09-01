package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.ImageDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDocumentDao {
    @Query("SELECT * FROM image_documents WHERE mediaAssetId = :mediaAssetId")
    fun getByMediaAssetId(mediaAssetId: String): Flow<ImageDocumentEntity?>

    @Query("SELECT * FROM image_documents WHERE id = :id")
    suspend fun getById(id: String): ImageDocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: ImageDocumentEntity)

    @Update
    suspend fun update(doc: ImageDocumentEntity)

    @Query("DELETE FROM image_documents WHERE id = :id")
    suspend fun deleteById(id: String)
}
