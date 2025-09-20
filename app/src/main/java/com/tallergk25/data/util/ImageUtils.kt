package com.tallergk25.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun createImageFile(context: Context, prefix: String = "IMG"): File {
    val imagesDir = File(context.filesDir, "images").apply { mkdirs() }
    val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return File(imagesDir, "${prefix}_${time}.jpg")
}

fun uriForFile(context: Context, file: File): Uri {
    // Debe coincidir con el authorities del Manifest
    return FileProvider.getUriForFile(context, "com.tallergk25.fileprovider", file)
}
