package com.pzx.downloader.utils

import android.content.Context
import android.util.Log
import com.pzx.downloader.http.ApiService
import com.pzx.downloader.http.ApiService.Companion.BASE_URL
import com.pzx.downloader.http.FileInfo
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun htmlFormat(context: Context, htmlContent: String?): List<FileInfo> =
    mutableListOf<FileInfo>().apply {
        htmlContent?.let {
            for (match in ApiService.Companion.HTML_FILE_PATTERN.toRegex().findAll(it)) {
                val date = match.groupValues[1]
                val time = match.groupValues[2]
                val size = match.groupValues[3].toLong()
                val url = match.groupValues[4]
                val name = match.groupValues[5]
                if (name.endsWith(".apk", ignoreCase = true)) {
                    add(
                        FileInfo(
                            name = name,
                            url = "$BASE_URL${url.removePrefix("/")}",
                            size = size,
                            date = "$date $time",
                            isExists = isFileDownloaded(context, name, size)
                        )
                    )
                }
            }
        }
    }.sortedWith(compareByDescending { fileInfo ->
        // 解析日期时间字符串并用于排序
        try {
            val dateTimeStr = fileInfo.date
            val formatter = SimpleDateFormat("yyyy/M/d H:mm", Locale.getDefault())
            formatter.parse(dateTimeStr)
        } catch (e: Exception) {
            // 如果解析失败，返回一个很早的时间，使其排在最后
            Date(0)
        }
    })


/**
 * 获取下载的文件名
 */
fun getFileName(url: String): String = url.substringAfterLast("/", "unknown_file")


/**
 * 获取缓存文件夹
 */
fun getFolder(context: Context): File = File(context.getExternalFilesDir(null), "DownloadFile")


/**
 * 创建文件夹
 */
fun createFile(context: Context, url: String): File {
    val folder = getFolder(context)
    if (!folder.exists()) {
        val result = folder.mkdirs()
        Log.d("Pan", "创建文件夹结果: $result")
        if (!result) {
            Log.e("Pan", "创建文件夹失败: ${folder.absolutePath}")
        }
    }
    return File(folder, getFileName(url))
}

fun isFileDownloaded(context: Context, fileName: String, fileSize: Long): Boolean {
    val file = File(getFolder(context), fileName)
    // 检查文件是否存在
    if (!file.exists()) {
        return false
    }
    // 如果有文件大小信息，检查文件大小是否匹配
    try {
        if (file.length() == fileSize) {
            return true
        }
    } catch (_: NumberFormatException) {
        // 如果无法解析文件大小，则只检查文件是否存在
        return true
    }

    return false
}