package com.v20charactermanager.data.repository

import com.v20charactermanager.data.local.entity.*
import com.v20charactermanager.domain.model.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

fun MediaAssetEntity.toDomain() = MediaAsset(
    id = id, chronicleId = chronicleId, type = MediaAssetType.valueOf(type),
    title = title, description = description,
    originalFilePath = originalFilePath, thumbnailFilePath = thumbnailFilePath,
    width = width, height = height,
    tags = tags.split(",").filter { it.isNotEmpty() },
    linkedEntityIds = linkedEntityIds.split(",").filter { it.isNotEmpty() },
    visibility = Visibility.valueOf(visibility),
    createdAt = createdAt, modifiedAt = modifiedAt
)

fun MediaAsset.toEntity() = MediaAssetEntity(
    id = id, chronicleId = chronicleId, type = type.name,
    title = title, description = description,
    originalFilePath = originalFilePath, thumbnailFilePath = thumbnailFilePath,
    width = width, height = height,
    tags = tags.joinToString(","), linkedEntityIds = linkedEntityIds.joinToString(","),
    visibility = visibility.name,
    createdAt = createdAt, modifiedAt = modifiedAt
)

fun ImageDocumentEntity.toDomain() = ImageDocument(
    id = id, mediaAssetId = mediaAssetId,
    currentRevisionId = currentRevisionId,
    zoomDefaults = if (zoomDefaults.isNotEmpty()) json.decodeFromString(zoomDefaults) else null,
    createdAt = createdAt, modifiedAt = modifiedAt
)

fun ImageDocument.toEntity() = ImageDocumentEntity(
    id = id, mediaAssetId = mediaAssetId,
    currentRevisionId = currentRevisionId,
    zoomDefaults = if (zoomDefaults != null) json.encodeToString(zoomDefaults!!) else "",
    createdAt = createdAt, modifiedAt = modifiedAt
)

fun ImageLayerEntity.toDomain() = ImageLayer(
    id = id, imageDocumentId = imageDocumentId,
    name = name, visible = visible,
    visibility = Visibility.valueOf(visibility),
    locked = locked, order = order,
    createdAt = createdAt
)

fun ImageLayer.toEntity() = ImageLayerEntity(
    id = id, imageDocumentId = imageDocumentId,
    name = name, visible = visible,
    visibility = visibility.name,
    locked = locked, order = order,
    createdAt = createdAt
)

fun ImageAnnotationEntity.toDomain() = ImageAnnotation(
    id = id, layerId = layerId, imageDocumentId = imageDocumentId,
    type = AnnotationType.valueOf(type),
    geometry = if (geometryJson.isNotEmpty()) json.decodeFromString(geometryJson) else AnnotationGeometry(),
    style = if (styleJson.isNotEmpty()) json.decodeFromString(styleJson) else AnnotationStyle(),
    text = text,
    pinType = pinType?.let { PinType.valueOf(it) },
    linkedEntityId = linkedEntityId, linkedEntityType = linkedEntityType,
    visibility = Visibility.valueOf(visibility),
    createdAt = createdAt, modifiedAt = modifiedAt
)

fun ImageAnnotation.toEntity() = ImageAnnotationEntity(
    id = id, layerId = layerId, imageDocumentId = imageDocumentId,
    type = type.name,
    geometryJson = json.encodeToString(geometry),
    styleJson = json.encodeToString(style),
    text = text,
    pinType = pinType?.name,
    linkedEntityId = linkedEntityId, linkedEntityType = linkedEntityType,
    visibility = visibility.name,
    createdAt = createdAt, modifiedAt = modifiedAt
)

fun ImageRevisionEntity.toDomain() = ImageRevision(
    id = id, imageDocumentId = imageDocumentId,
    mediaAssetId = mediaAssetId, revisionNumber = revisionNumber,
    createdAt = createdAt, sessionId = sessionId,
    description = description,
    annotationSnapshot = annotationSnapshot,
    layerSnapshot = layerSnapshot
)

fun ImageRevision.toEntity() = ImageRevisionEntity(
    id = id, imageDocumentId = imageDocumentId,
    mediaAssetId = mediaAssetId, revisionNumber = revisionNumber,
    createdAt = createdAt, sessionId = sessionId,
    description = description,
    annotationSnapshot = annotationSnapshot,
    layerSnapshot = layerSnapshot
)
