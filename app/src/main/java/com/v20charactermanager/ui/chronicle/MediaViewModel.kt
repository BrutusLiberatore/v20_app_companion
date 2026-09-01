package com.v20charactermanager.ui.chronicle

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.data.local.ChronicleImageManager
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.domain.repository.MediaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class MediaLibraryUiState(
    val assets: List<MediaAsset> = emptyList(),
    val selectedCategory: MediaAssetCategory = MediaAssetCategory.ALL,
    val isLoading: Boolean = true
)

data class ImageViewerUiState(
    val asset: MediaAsset? = null,
    val document: ImageDocument? = null,
    val layers: List<ImageLayer> = emptyList(),
    val annotations: List<ImageAnnotation> = emptyList(),
    val revisions: List<ImageRevision> = emptyList(),
    val currentAnnotationType: AnnotationType = AnnotationType.PEN_STROKE,
    val activeLayerId: String? = null,
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

    fun filterByCategory(category: MediaAssetCategory) {
        _libraryUiState.update { it.copy(selectedCategory = category) }
    }

    fun importImage(chronicleId: String, uri: Uri, title: String, type: MediaAssetType, visibility: Visibility) {
        viewModelScope.launch {
            val assetId = UUID.randomUUID().toString()
            val savedPath = ChronicleImageManager.saveImage(context, assetId, uri) ?: return@launch
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
        }
    }

    fun deleteAsset(assetId: String) {
        viewModelScope.launch {
            val asset = mediaRepository.getAssetById(assetId) ?: return@launch
            ChronicleImageManager.deleteImage(context, asset.originalFilePath)
            asset.thumbnailFilePath?.let { ChronicleImageManager.deleteImage(context, it) }
            mediaRepository.deleteAsset(assetId)
        }
    }

    fun loadAssetForViewing(assetId: String) {
        viewModelScope.launch {
            val asset = mediaRepository.getAssetById(assetId) ?: return@launch
            _viewerUiState.update { it.copy(asset = asset, isLoading = true) }
            mediaRepository.getDocumentByAssetId(assetId).collect { doc ->
                if (doc != null) {
                    _viewerUiState.update { it.copy(document = doc) }
                    mediaRepository.getLayersByDocument(doc.id).collect { layers ->
                        _viewerUiState.update { it.copy(layers = layers) }
                    }
                    mediaRepository.getAnnotationsByDocument(doc.id).collect { annotations ->
                        _viewerUiState.update { it.copy(annotations = annotations, isLoading = false) }
                    }
                    mediaRepository.getRevisionsByDocument(doc.id).collect { revisions ->
                        _viewerUiState.update { it.copy(revisions = revisions) }
                    }
                }
            }
        }
    }

    fun addAnnotation(annotation: ImageAnnotation) {
        viewModelScope.launch {
            mediaRepository.insertAnnotation(annotation)
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
            val annotations = _viewerUiState.value.annotations
            val layers = _viewerUiState.value.layers
            val revision = ImageRevision(
                id = UUID.randomUUID().toString(),
                imageDocumentId = imageDocumentId,
                mediaAssetId = mediaAssetId,
                revisionNumber = maxRev + 1,
                sessionId = sessionId,
                description = description,
                annotationSnapshot = "[]",
                layerSnapshot = "[]"
            )
            mediaRepository.insertRevision(revision)
        }
    }

    fun selectAnnotationTool(type: AnnotationType) {
        _viewerUiState.update { it.copy(currentAnnotationType = type) }
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
