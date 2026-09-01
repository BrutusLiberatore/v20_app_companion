package com.v20charactermanager.domain.repository

import com.v20charactermanager.domain.model.*
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getAssetsByChronicle(chronicleId: String): Flow<List<MediaAsset>>
    fun getAssetsByType(chronicleId: String, type: MediaAssetType): Flow<List<MediaAsset>>
    suspend fun getAssetById(id: String): MediaAsset?
    suspend fun insertAsset(asset: MediaAsset)
    suspend fun updateAsset(asset: MediaAsset)
    suspend fun deleteAsset(id: String)

    fun getDocumentByAssetId(mediaAssetId: String): Flow<ImageDocument?>
    suspend fun getDocumentById(id: String): ImageDocument?
    suspend fun insertDocument(doc: ImageDocument)
    suspend fun updateDocument(doc: ImageDocument)

    fun getLayersByDocument(documentId: String): Flow<List<ImageLayer>>
    suspend fun insertLayer(layer: ImageLayer)
    suspend fun updateLayer(layer: ImageLayer)
    suspend fun deleteLayer(id: String)

    fun getAnnotationsByDocument(documentId: String): Flow<List<ImageAnnotation>>
    fun getAnnotationsByLayer(layerId: String): Flow<List<ImageAnnotation>>
    suspend fun insertAnnotation(annotation: ImageAnnotation)
    suspend fun updateAnnotation(annotation: ImageAnnotation)
    suspend fun deleteAnnotation(id: String)

    fun getRevisionsByDocument(documentId: String): Flow<List<ImageRevision>>
    suspend fun insertRevision(revision: ImageRevision)
    suspend fun getMaxRevisionNumber(documentId: String): Int
}
