package com.v20charactermanager.data.repository

import com.v20charactermanager.data.local.dao.*
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MediaRepositoryImpl(
    private val mediaAssetDao: MediaAssetDao,
    private val imageDocumentDao: ImageDocumentDao,
    private val imageLayerDao: ImageLayerDao,
    private val imageAnnotationDao: ImageAnnotationDao,
    private val imageRevisionDao: ImageRevisionDao
) : MediaRepository {

    override fun getAssetsByChronicle(chronicleId: String): Flow<List<MediaAsset>> {
        return mediaAssetDao.getByChronicleId(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAssetsByType(chronicleId: String, type: MediaAssetType): Flow<List<MediaAsset>> {
        return mediaAssetDao.getByType(chronicleId, type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAssetById(id: String): MediaAsset? {
        return mediaAssetDao.getById(id)?.toDomain()
    }

    override suspend fun insertAsset(asset: MediaAsset) {
        mediaAssetDao.insert(asset.toEntity())
    }

    override suspend fun updateAsset(asset: MediaAsset) {
        mediaAssetDao.update(asset.toEntity())
    }

    override suspend fun deleteAsset(id: String) {
        mediaAssetDao.deleteById(id)
    }

    override fun getDocumentByAssetId(mediaAssetId: String): Flow<ImageDocument?> {
        return imageDocumentDao.getByMediaAssetId(mediaAssetId).map { it?.toDomain() }
    }

    override suspend fun getDocumentById(id: String): ImageDocument? {
        return imageDocumentDao.getById(id)?.toDomain()
    }

    override suspend fun insertDocument(doc: ImageDocument) {
        imageDocumentDao.insert(doc.toEntity())
    }

    override suspend fun updateDocument(doc: ImageDocument) {
        imageDocumentDao.update(doc.toEntity())
    }

    override fun getLayersByDocument(documentId: String): Flow<List<ImageLayer>> {
        return imageLayerDao.getByDocumentId(documentId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertLayer(layer: ImageLayer) {
        imageLayerDao.insert(layer.toEntity())
    }

    override suspend fun updateLayer(layer: ImageLayer) {
        imageLayerDao.update(layer.toEntity())
    }

    override suspend fun deleteLayer(id: String) {
        imageLayerDao.deleteById(id)
    }

    override fun getAnnotationsByDocument(documentId: String): Flow<List<ImageAnnotation>> {
        return imageAnnotationDao.getByDocumentId(documentId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAnnotationsByLayer(layerId: String): Flow<List<ImageAnnotation>> {
        return imageAnnotationDao.getByLayerId(layerId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertAnnotation(annotation: ImageAnnotation) {
        imageAnnotationDao.insert(annotation.toEntity())
    }

    override suspend fun updateAnnotation(annotation: ImageAnnotation) {
        imageAnnotationDao.update(annotation.toEntity())
    }

    override suspend fun deleteAnnotation(id: String) {
        imageAnnotationDao.deleteById(id)
    }

    override fun getRevisionsByDocument(documentId: String): Flow<List<ImageRevision>> {
        return imageRevisionDao.getByDocumentId(documentId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertRevision(revision: ImageRevision) {
        imageRevisionDao.insert(revision.toEntity())
    }

    override suspend fun getMaxRevisionNumber(documentId: String): Int {
        return imageRevisionDao.getMaxRevisionNumber(documentId) ?: 0
    }
}
