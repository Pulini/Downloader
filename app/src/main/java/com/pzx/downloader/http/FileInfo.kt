package com.pzx.downloader.http

import android.content.Context
import com.pzx.downloader.utils.getFolder
import java.io.File


data class FileInfo(
    val name: String,
    val url: String,
    val size: Long,
    val date: String,
    var isExists: Boolean=false,
){
    fun fileSize(): String = when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${String.format("%.2f", size / 1024.0)} KB"
        size < 1024 * 1024 * 1024 -> "${String.format("%.2f", size / (1024.0 * 1024.0))} MB"
        else -> "${String.format("%.2f", size / (1024.0 * 1024.0 * 1024.0))} GB"
    }
    fun getFile(context: Context)=File(getFolder(context), name)

}