
package com.pzx.downloader.utils

import android.content.Context
import android.util.Log
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * File Name : DownloadUtil
 * Created by : PanZX on 2020/03/06
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 下载工具
 */
interface OnDownLoadListener {
    fun onExists(file: File)
    fun onStart(fileName: String)
    fun onStop()
    fun onProgress(progress: Long, length: Long)
    fun onSuccess(file: File)
    fun onError(msg: String)
}
class Downloader(val context: Context, val downloadUrl: String, val listener: OnDownLoadListener) {
    private var cache: File? = null

    @Volatile
    private var close = true

    fun start() {
        Log.e("Pan","开始下载=${downloadUrl}")
        close = false
        DownloadThread().start()
    }

    fun stop() {
        close = true
    }

    internal inner class DownloadThread : Thread() {
        override fun run() {
            super.run()
            try {
                if (downloadUrl.isEmpty()) {
                    listener.onError("下载链接为空")
                    return
                }

                // 对URL进行解码处理，解决包含特殊字符的URL无法下载的问题
                val decodedUrl = URLDecoder.decode(downloadUrl, StandardCharsets.UTF_8.toString())
                Log.d("Pan", "原始URL: $downloadUrl")
                Log.d("Pan", "解码后URL: $decodedUrl")

                cache = createFile(context, decodedUrl)
                val url = URL(decodedUrl)
                val connection: URLConnection = url.openConnection()
                connection.connect()
                val contentLength = connection.contentLengthLong
                if (cache?.exists() == true && cache?.length() == contentLength && contentLength > 0) {
                    listener.onExists(cache!!)
                    return
                }
                var count: Int
                var total: Long = 0
                val buffer = ByteArray(8192)
                val input: InputStream = BufferedInputStream(url.openStream())
                val output: OutputStream = FileOutputStream(cache)

                listener.onStart(getFileName(decodedUrl))

                try {
                    while (input.read(buffer).also { count = it } != -1 && !close) {
                        total += count.toLong()
                        listener.onProgress(total, contentLength)
                        output.write(buffer, 0, count)
                    }
                    output.flush()
                } finally {
                    try {
                        output.close()
                    } catch (e: Exception) {
                        Log.e("Pan", "关闭输出流失败: ${e.message}")
                    }
                    try {
                        input.close()
                    } catch (e: Exception) {
                        Log.e("Pan", "关闭输入流失败: ${e.message}")
                    }
                }
                if (close) {
                    cache?.delete()
                    listener.onError("取消下载")
                } else {
                    cache?.let { listener.onSuccess(it) }
                }
            } catch (e: Exception) {
                Log.e("Pan", "下载错误:${e.toString()}")
                listener.onError(e.toString())
                cache?.delete()
            }
        }
    }

}