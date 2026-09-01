package com.v20charactermanager.ui.chronicle

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.data.local.ChronicleImageManager
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.domain.repository.MediaRepository
import com.v20charactermanager.ui.components.V20ErrorType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

data class MediaLibraryUiState(
    val assets: List<MediaAsset> = emptyList(),
    val selectedCategory: MediaAssetCategory = MediaAssetCategory.ALL,
    val isLoading: Boolean = true,
    val message: String? = null,
    val errorType: V20ErrorType? = null,
    val errorDetails: String? = null
)

data class ImageViewerUiState(
    val asset: MediaAsset? = null,
    val document: ImageDocument? = null,
    val layers: List<ImageLayer> = emptyList(),
    val annotations: List<ImageAnnotation> = emptyList(),
    val revisions: List<ImageRevision> = emptyList(),
    val currentAnnotationType: AnnotationType = AnnotationType.PEN_STROKE,
    val activeLayerId: String? = null,
    val isDrawingEnabled: Boolean = false,
    val undoStack: List<ImageAnnotation> = emptyList(),
    val redoStack: List<ImageAnnotation> = emptyList(),
    val isLoading: Boolean = true
)

class MediaViewModel(
    private val mediaRepository: MediaRepository,
    private val context: Context
) : ViewModel() {

    private val _libraryUiState = MutableStateFlow(MediaLibraryUiState())
    val libraryUiState: StateFlow<MediaLibraryUiState> = _libraryUiState.asStateFlow()

    private val _viewerUiState = MutableStateFlow(ImageViewerUiState())
    val viewerUiState: StateFlow<ImageViewerUiState> = _viewerUiState.asStateFlow()

    fun loadAssets(chronicleId: String) {
        viewModelScope.launch {
            mediaRepository.getAssetsByChronicle(chronicleId).collect { assets ->
                _libraryUiState.update { it.copy(assets = assets, isLoading = false) }
            }
        }
    }

    fun findAssetForLocation(chronicleId: String, locationId: String, onResult: (MediaAsset?) -> Unit) {
        viewModelScope.launch {
            mediaRepository.getAssetsByChronicle(chronicleId).first { true }
                .find { asset ->
                    asset.linkedEntityIds.contains(locationId)
                }?.let { onResult(it) } ?: onResult(null)
        }
    }

    fun filterByCategory(category: MediaAssetCategory) {
        _libraryUiState.update { it.copy(selectedCategory = category) }
    }

    fun importImage(chronicleId: String, uri: Uri, title: String, type: MediaAssetType, visibility: Visibility) {
        viewModelScope.launch {
            try {
                _libraryUiState.update { it.copy(isLoading = true, errorType = null) }
                val assetId = UUID.randomUUID().toString()
                val savedPath = ChronicleImageManager.saveImage(context, assetId, uri)
                if (savedPath == null) {
                    _libraryUiState.update {
                        it.copy(
                            isLoading = false,
                            errorType = V20ErrorType.IMAGE_SAVE_FAILED,
                            errorDetails = "URI: $uri\nMIME: ${context.contentResolver.getType(uri)}"
                        )
                    }
                    return@launch
                }
                val asset = MediaAsset(
                    id = assetId, chronicleId = chronicleId,
                    type = type, title = title,
                    originalFilePath = savedPath,
                    visibility = visibility
                )
                mediaRepository.insertAsset(asset)
                val doc = ImageDocument(
                    id = UUID.randomUUID().toString(),
                    mediaAssetId = assetId
                )
                mediaRepository.insertDocument(doc)
                val layer = ImageLayer(
                    id = UUID.randomUUID().toString(),
                    imageDocumentId = doc.id,
                    name = "Annotations",
                    visibility = Visibility.PUBLIC
                )
                mediaRepository.insertLayer(layer)
                loadAssets(chronicleId)
                _libraryUiState.update { it.copy(isLoading = false, message = "Image imported") }
            } catch (e: OutOfMemoryError) {
                _libraryUiState.update {
                    it.copy(
                        isLoading = false,
                        errorType = V20ErrorType.MEMORY_ERROR,
                        errorDetails = e.message
                    )
                }
            } catch (e: SecurityException) {
                _libraryUiState.update {
                    it.copy(
                        isLoading = false,
                        errorType = V20ErrorType.PERMISSION_DENIED,
                        errorDetails = e.message
                    )
                }
            } catch (e: Exception) {
                _libraryUiState.update {
                    it.copy(
                        isLoading = false,
                        errorType = V20ErrorType.IMAGE_IMPORT_FAILED,
                        errorDetails = e.message
                    )
                }
            }
        }
    }

    fun importDocument(chronicleId: String, uri: Uri, title: String) {
        viewModelScope.launch {
            try {
                _libraryUiState.update { it.copy(isLoading = true, errorType = null) }
                val assetId = UUID.randomUUID().toString()
                val fileName = "doc_${assetId}.pdf"
                val savedPath = try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream == null) {
                        _libraryUiState.update {
                            it.copy(
                                isLoading = false,
                                errorType = V20ErrorType.DOCUMENT_IMPORT_FAILED,
                                errorDetails = "ContentResolver returned null stream for URI: $uri"
                            )
                        }
                        return@launch
                    }
                    val dir = java.io.File(context.filesDir, "chronicle_documents")
                    dir.mkdirs()
                    val file = java.io.File(dir, fileName)
                    file.outputStream().use { output -> inputStream.use { input -> input.copyTo(output) } }
                    file.absolutePath
                } catch (e: SecurityException) {
                    _libraryUiState.update {
                        it.copy(
                            isLoading = false,
                            errorType = V20ErrorType.PERMISSION_DENIED,
                            errorDetails = e.message
                        )
                    }
                    return@launch
                } catch (e: Exception) {
                    _libraryUiState.update {
                        it.copy(
                            isLoading = false,
                            errorType = V20ErrorType.DOCUMENT_IMPORT_FAILED,
                            errorDetails = e.message
                        )
                    }
                    return@launch
                }

                val asset = MediaAsset(
                    id = assetId, chronicleId = chronicleId,
                    type = MediaAssetType.DOCUMENT, title = title,
                    originalFilePath = savedPath,
                    visibility = Visibility.GM_ONLY
                )
                mediaRepository.insertAsset(asset)
                loadAssets(chronicleId)
                _libraryUiState.update { it.copy(isLoading = false, message = "Document imported") }
            } catch (e: Exception) {
                _libraryUiState.update {
                    it.copy(
                        isLoading = false,
                        errorType = V20ErrorType.DOCUMENT_IMPORT_FAILED,
                        errorDetails = e.message
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _libraryUiState.update { it.copy(message = null) }
    }

    fun clearError() {
        _libraryUiState.update { it.copy(errorType = null, errorDetails = null) }
    }

    fun deleteAsset(assetId: String) {
        viewModelScope.launch {
            val asset = mediaRepository.getAssetById(assetId) ?: return@launch
            ChronicleImageManager.deleteImage(context, asset.originalFilePath)
            asset.thumbnailFilePath?.let { ChronicleImageManager.deleteImage(context, it) }
            mediaRepository.deleteAsset(assetId)
        }
    }

    fun renameAsset(assetId: String, newTitle: String) {
        viewModelScope.launch {
            val asset = mediaRepository.getAssetById(assetId) ?: return@launch
            mediaRepository.updateAsset(asset.copy(title = newTitle, modifiedAt = System.currentTimeMillis()))
        }
    }

    fun updateAssetDescription(assetId: String, description: String) {
        viewModelScope.launch {
            val asset = mediaRepository.getAssetById(assetId) ?: return@launch
            mediaRepository.updateAsset(asset.copy(description = description, modifiedAt = System.currentTimeMillis()))
        }
    }

    fun loadAssetForViewing(assetId: String) {
        viewModelScope.launch {
            val asset = mediaRepository.getAssetById(assetId) ?: return@launch
            _viewerUiState.update { it.copy(asset = asset, isLoading = true) }
            mediaRepository.getDocumentByAssetId(assetId).flatMapLatest { doc ->
                if (doc != null) {
                    _viewerUiState.update { it.copy(document = doc) }
                    combine(
                        mediaRepository.getLayersByDocument(doc.id),
                        mediaRepository.getAnnotationsByDocument(doc.id),
                        mediaRepository.getRevisionsByDocument(doc.id)
                    ) { layers, annotations, revisions ->
                        Triple(layers, annotations, revisions)
                    }
                } else {
                    flowOf(Triple(emptyList(), emptyList(), emptyList()))
                }
            }.collect { (layers, annotations, revisions) ->
                _viewerUiState.update { state ->
                    val newActiveLayer = state.activeLayerId ?: layers.firstOrNull()?.id
                    state.copy(
                        layers = layers,
                        annotations = annotations,
                        revisions = revisions,
                        activeLayerId = newActiveLayer,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun addAnnotation(annotation: ImageAnnotation) {
        viewModelScope.launch {
            mediaRepository.insertAnnotation(annotation)
            _viewerUiState.update { state ->
                state.copy(
                    undoStack = state.undoStack + annotation,
                    redoStack = emptyList()
                )
            }
        }
    }

    fun saveAnnotationImmediate(annotation: ImageAnnotation) {
        viewModelScope.launch {
            mediaRepository.insertAnnotation(annotation.copy(modifiedAt = System.currentTimeMillis()))
            _viewerUiState.update { state ->
                state.copy(
                    undoStack = state.undoStack + annotation,
                    redoStack = emptyList()
                )
            }
        }
    }

    fun importImageForLocation(chronicleId: String, locationId: String, uri: Uri, title: String) {
        viewModelScope.launch {
            val assetId = UUID.randomUUID().toString()
            val savedPath = ChronicleImageManager.saveImage(context, assetId, uri) ?: return@launch
            val asset = MediaAsset(
                id = assetId, chronicleId = chronicleId,
                type = MediaAssetType.OTHER, title = title,
                originalFilePath = savedPath,
                linkedEntityIds = listOf(locationId),
                visibility = Visibility.GM_ONLY
            )
            mediaRepository.insertAsset(asset)
            val doc = ImageDocument(
                id = UUID.randomUUID().toString(),
                mediaAssetId = assetId
            )
            mediaRepository.insertDocument(doc)
            val baseLayer = ImageLayer(
                id = UUID.randomUUID().toString(),
                imageDocumentId = doc.id,
                name = "Mappa",
                visibility = Visibility.GM_ONLY,
                order = 0
            )
            mediaRepository.insertLayer(baseLayer)
            val annotationLayer = ImageLayer(
                id = UUID.randomUUID().toString(),
                imageDocumentId = doc.id,
                name = "Annotazioni",
                visibility = Visibility.PUBLIC,
                order = 1
            )
            mediaRepository.insertLayer(annotationLayer)
        }
    }

    fun updateAnnotation(annotation: ImageAnnotation) {
        viewModelScope.launch {
            mediaRepository.updateAnnotation(annotation.copy(modifiedAt = System.currentTimeMillis()))
        }
    }

    fun deleteAnnotation(id: String) {
        viewModelScope.launch {
            mediaRepository.deleteAnnotation(id)
        }
    }

    fun undo(): Boolean {
        val state = _viewerUiState.value
        if (state.undoStack.isEmpty()) return false
        val last = state.undoStack.last()
        viewModelScope.launch {
            mediaRepository.deleteAnnotation(last.id)
        }
        _viewerUiState.update {
            it.copy(
                undoStack = it.undoStack.dropLast(1),
                redoStack = it.redoStack + last
            )
        }
        return true
    }

    fun redo(): Boolean {
        val state = _viewerUiState.value
        if (state.redoStack.isEmpty()) return false
        val last = state.redoStack.last()
        viewModelScope.launch {
            mediaRepository.insertAnnotation(last)
        }
        _viewerUiState.update {
            it.copy(
                redoStack = it.redoStack.dropLast(1),
                undoStack = it.undoStack + last
            )
        }
        return true
    }

    fun toggleDrawingMode() {
        _viewerUiState.update { it.copy(isDrawingEnabled = !it.isDrawingEnabled) }
    }

    fun setActiveLayer(layerId: String?) {
        _viewerUiState.update { it.copy(activeLayerId = layerId) }
    }

    fun clearActiveLayerAnnotations() {
        val state = _viewerUiState.value
        val layerId = state.activeLayerId ?: return
        val layerAnnotations = state.annotations.filter { it.layerId == layerId }
        viewModelScope.launch {
            layerAnnotations.forEach { mediaRepository.deleteAnnotation(it.id) }
        }
        _viewerUiState.update {
            it.copy(
                undoStack = it.undoStack + layerAnnotations,
                redoStack = emptyList()
            )
        }
    }

    fun addLayer(layer: ImageLayer) {
        viewModelScope.launch {
            mediaRepository.insertLayer(layer)
        }
    }

    fun updateLayer(layer: ImageLayer) {
        viewModelScope.launch {
            mediaRepository.updateLayer(layer)
        }
    }

    fun deleteLayer(id: String) {
        viewModelScope.launch {
            mediaRepository.deleteLayer(id)
        }
    }

    fun saveRevision(imageDocumentId: String, mediaAssetId: String, sessionId: String? = null, description: String? = null) {
        viewModelScope.launch {
            val maxRev = mediaRepository.getMaxRevisionNumber(imageDocumentId)
            val state = _viewerUiState.value
            val revision = ImageRevision(
                id = UUID.randomUUID().toString(),
                imageDocumentId = imageDocumentId,
                mediaAssetId = mediaAssetId,
                revisionNumber = maxRev + 1,
                sessionId = sessionId,
                description = description,
                annotationSnapshot = Json.encodeToString(state.annotations),
                layerSnapshot = Json.encodeToString(state.layers)
            )
            mediaRepository.insertRevision(revision)
        }
    }

    fun selectAnnotationTool(type: AnnotationType) {
        _viewerUiState.update { it.copy(currentAnnotationType = type) }
    }

    fun restoreRevision(revision: ImageRevision) {
        val state = _viewerUiState.value
        val docId = state.document?.id ?: return
        viewModelScope.launch {
            state.annotations.forEach { mediaRepository.deleteAnnotation(it.id) }
            try {
                val restoredAnnotations = Json.decodeFromString<List<ImageAnnotation>>(revision.annotationSnapshot)
                restoredAnnotations.forEach { mediaRepository.insertAnnotation(it) }
            } catch (_: Exception) { }
            try {
                val restoredLayers = Json.decodeFromString<List<ImageLayer>>(revision.layerSnapshot)
                restoredLayers.forEach { mediaRepository.insertLayer(it) }
            } catch (_: Exception) { }
            _viewerUiState.update {
                it.copy(
                    undoStack = emptyList(),
                    redoStack = emptyList()
                )
            }
        }
    }

    fun toggleLayerVisibility(layerId: String) {
        val state = _viewerUiState.value
        val layer = state.layers.find { it.id == layerId } ?: return
        viewModelScope.launch {
            mediaRepository.updateLayer(layer.copy(visible = !layer.visible))
        }
    }
}

class MediaViewModelFactory(
    private val mediaRepository: MediaRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MediaViewModel(mediaRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
