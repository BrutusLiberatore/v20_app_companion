package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.ImageRevisionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageRevisionDao {
    @Query("SELECT * FROM image_revisions WHERE imageDocumentId = :documentId ORDER BY revisionNumber DESC")
    fun getByDocumentId(documentId: String): Flow<List<ImageRevisionEntity>>

    @Query("SELECT * FROM image_revisions WHERE id = :id")
    suspend fun getById(id: String): ImageRevisionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(revision: ImageRevisionEntity)

    @Query("DELETE FROM image_revisions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM image_revisions WHERE imageDocumentId = :documentId")
    suspend fun deleteByDocumentId(documentId: String)

    @Query("SELECT MAX(revisionNumber) FROM image_revisions WHERE imageDocumentId = :documentId")
    suspend fun getMaxRevisionNumber(documentId: String): Int?
}
