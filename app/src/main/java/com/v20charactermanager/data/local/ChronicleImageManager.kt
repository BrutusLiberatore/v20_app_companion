package com.v20charactermanager.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

object ChronicleImageManager {

    private const val IMAGES_DIR = "chronicle_images"
    private const val THUMBNAILS_DIR = "chronicle_thumbnails"
    private const val FULL_MAX_SIZE = 1920
    private const val THUMB_MAX_SIZE = 256
    private const val JPEG_QUALITY = 92

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

    fun saveImage(context: Context, entityId: String, sourceUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

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

            val fullFile = File(getImagesDir(context), "$entityId.jpg")
            FileOutputStream(fullFile).use { out ->
                full.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }
            full.recycle()

            val thumb = scaleBitmap(
                BitmapFactory.decodeStream(fullFile.inputStream()),
                THUMB_MAX_SIZE
            )
            val thumbFile = File(getThumbnailsDir(context), "$entityId.jpg")
            FileOutputStream(thumbFile).use { out ->
                thumb.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }
            thumb.recycle()

            fullFile.absolutePath
        } catch (e: Exception) {
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
        File(getImagesDir(context), "$entityId.jpg").delete()
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
