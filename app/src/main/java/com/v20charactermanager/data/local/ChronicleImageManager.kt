package com.v20charactermanager.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

object ChronicleImageManager {

    private const val TAG = "ChronicleImageManager"
    private const val IMAGES_DIR = "chronicle_images"
    private const val THUMBNAILS_DIR = "chronicle_thumbnails"
    private const val FULL_MAX_SIZE = 1920
    private const val THUMB_MAX_SIZE = 256
    private const val JPEG_QUALITY = 92

    private val BITMAP_MIMES = setOf("image/jpeg", "image/png", "image/webp")

    private fun getImagesDir(context: Context): File {
        val dir = File(context.filesDir, IMAGES_DIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun getThumbnailsDir(context: Context): File {
        val dir = File(context.filesDir, THUMBNAILS_DIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun extensionForMime(mime: String?): String = when (mime) {
        "image/jpeg" -> "jpg"
        "image/png" -> "png"
        "image/gif" -> "gif"
        "image/svg+xml" -> "svg"
        "image/webp" -> "webp"
        else -> "jpg"
    }

    fun saveImage(context: Context, entityId: String, sourceUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri)
            if (inputStream == null) {
                Log.e(TAG, "Failed to open input stream for URI: $sourceUri")
                return null
            }

            val mimeType = context.contentResolver.getType(sourceUri)
            val ext = extensionForMime(mimeType)

            if (mimeType !in BITMAP_MIMES) {
                val imagesDir = getImagesDir(context)
                if (!imagesDir.exists()) imagesDir.mkdirs()
                val outFile = File(imagesDir, "$entityId.$ext")
                outFile.outputStream().use { out -> inputStream.use { input -> input.copyTo(out) } }
                Log.d(TAG, "Raw file saved: ${outFile.absolutePath} (mime=$mimeType)")
                return outFile.absolutePath
            }

            val original = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (original == null) {
                Log.e(TAG, "BitmapFactory returned null for URI: $sourceUri (mimeType=$mimeType)")
                return null
            }

            val rotation = getRotation(context, sourceUri)
            val rotated = if (rotation != 0) {
                val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
                    .also { original.recycle() }
            } else {
                original
            }

            val full = scaleBitmap(rotated, FULL_MAX_SIZE)
            rotated.recycle()

            val imagesDir = getImagesDir(context)
            if (!imagesDir.exists()) imagesDir.mkdirs()

            val fullFile = File(imagesDir, "$entityId.$ext")
            val compressFormat = when (ext) {
                "png" -> Bitmap.CompressFormat.PNG
                "webp" -> Bitmap.CompressFormat.WEBP_LOSSY
                else -> Bitmap.CompressFormat.JPEG
            }
            FileOutputStream(fullFile).use { out ->
                full.compress(compressFormat, JPEG_QUALITY, out)
            }
            full.recycle()

            val thumbDir = getThumbnailsDir(context)
            if (!thumbDir.exists()) thumbDir.mkdirs()

            val thumbFile = File(thumbDir, "$entityId.jpg")
            val thumbBmp = BitmapFactory.decodeStream(fullFile.inputStream())
            if (thumbBmp != null) {
                val thumb = scaleBitmap(thumbBmp, THUMB_MAX_SIZE)
                thumbBmp.recycle()
                FileOutputStream(thumbFile).use { out ->
                    thumb.compress(Bitmap.CompressFormat.JPEG, 80, out)
                }
                thumb.recycle()
            }

            Log.d(TAG, "Image saved: ${fullFile.absolutePath} (rotation=$rotation, mime=$mimeType)")
            fullFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save image for entityId=$entityId", e)
            null
        }
    }

    fun getImageFile(context: Context, imagePath: String?): File? {
        if (imagePath == null) return null
        val file = File(imagePath)
        return if (file.exists()) file else null
    }

    fun getThumbnailFile(context: Context, entityId: String): File? {
        val file = File(getThumbnailsDir(context), "$entityId.jpg")
        return if (file.exists()) file else null
    }

    fun deleteImage(context: Context, imagePath: String?) {
        if (imagePath != null) {
            File(imagePath).delete()
        }
    }

    fun deleteAllForEntity(context: Context, entityId: String) {
        val imagesDir = getImagesDir(context)
        imagesDir.listFiles()?.filter { it.name.startsWith(entityId) }?.forEach { it.delete() }
        File(getThumbnailsDir(context), "$entityId.jpg").delete()
    }

    private fun getRotation(context: Context, uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = ExifInterface(stream)
                when (exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            } ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxSize && height <= maxSize) return bitmap

        val ratio = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
