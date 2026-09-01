package com.v20charactermanager.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ChronicleImageManager {

    private const val IMAGES_DIR = "chronicle_images"
    private const val MAX_SIZE = 512

    private fun getImagesDir(context: Context): File {
        val dir = File(context.filesDir, IMAGES_DIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun saveImage(context: Context, entityId: String, sourceUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val scaled = scaleBitmap(original, MAX_SIZE)
            original.recycle()

            val file = File(getImagesDir(context), "$entityId.jpg")
            FileOutputStream(file).use { out ->
                scaled.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            scaled.recycle()

            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun getImageFile(context: Context, imagePath: String?): File? {
        if (imagePath == null) return null
        val file = File(imagePath)
        return if (file.exists()) file else null
    }

    fun deleteImage(context: Context, imagePath: String?) {
        if (imagePath != null) {
            File(imagePath).delete()
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
