package com.v20charactermanager.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object PortraitManager {

    private const val PORTRAIT_DIR = "portraits"
    private const val MAX_SIZE = 512

    private fun getPortraitDir(context: Context): File {
        val dir = File(context.filesDir, PORTRAIT_DIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun savePortrait(context: Context, characterId: String, sourceUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val scaled = scaleBitmap(original, MAX_SIZE)
            original.recycle()

            val file = File(getPortraitDir(context), "$characterId.jpg")
            FileOutputStream(file).use { out ->
                scaled.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            scaled.recycle()

            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun getPortraitFile(context: Context, portraitPath: String?): File? {
        if (portraitPath == null) return null
        val file = File(portraitPath)
        return if (file.exists()) file else null
    }

    fun deletePortrait(context: Context, portraitPath: String?) {
        if (portraitPath != null) {
            File(portraitPath).delete()
        }
    }

    fun deletePortraitForCharacter(context: Context, characterId: String) {
        val file = File(getPortraitDir(context), "$characterId.jpg")
        if (file.exists()) file.delete()
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
