package com.vivaran.shared.utils

import io.ktor.util.*

object Base64Utils {
    fun encodeToString(bytes: ByteArray): String {
        return bytes.encodeBase64()
    }
    
    fun decodeFromString(base64String: String): ByteArray {
        return base64String.decodeBase64Bytes()
    }
    
    fun getFileFormat(fileName: String): String {
        return when (fileName.substringAfterLast('.', "").lowercase()) {
            "pdf" -> "pdf"
            "jpg", "jpeg" -> "jpeg"
            "png" -> "png"
            else -> "pdf" // default
        }
    }
    
    fun getMimeType(fileName: String): String {
        return when (fileName.substringAfterLast('.', "").lowercase()) {
            "pdf" -> "application/pdf"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "application/pdf"
        }
    }
}