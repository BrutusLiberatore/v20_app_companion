package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class AnnotationType {
    PEN_STROKE, HIGHLIGHTER, LINE, ARROW, CIRCLE, RECTANGLE, TEXT, PIN, ERASER
}

enum class PinType {
    LOCATION, NPC, EVENT, SECRET, CLUE, TERRITORY, SHELTER, CUSTOM
}

@Serializable
data class ImageAnnotation(
    val id: String,
    val layerId: String,
    val imageDocumentId: String,
    val type: AnnotationType,
    val geometry: AnnotationGeometry,
    val style: AnnotationStyle = AnnotationStyle(),
    val text: String? = null,
    val pinType: PinType? = null,
    val linkedEntityId: String? = null,
    val linkedEntityType: String? = null,
    val visibility: Visibility = Visibility.GM_ONLY,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)

@Serializable
data class AnnotationGeometry(
    val points: List<NormalizedPoint> = emptyList(),
    val startPoint: NormalizedPoint? = null,
    val endPoint: NormalizedPoint? = null,
    val center: NormalizedPoint? = null,
    val radiusX: Float = 0f,
    val radiusY: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f,
    val position: NormalizedPoint? = null
)

@Serializable
data class NormalizedPoint(
    val x: Float,
    val y: Float
)

@Serializable
data class AnnotationStyle(
    val strokeWidth: Float = 3f,
    val strokeColor: Long = 0xFFFF0000,
    val fillColor: Long = 0x00000000,
    val opacity: Float = 1.0f,
    val fontSize: Float = 16f,
    val fontColor: Long = 0xFFFF0000
)
