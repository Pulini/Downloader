package com.pzx.downloader

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pzx.downloader.http.ApiService
import com.pzx.downloader.http.ApiService.Companion.BASE_URL
import com.pzx.downloader.http.ApiService.Companion.DIRECTORY_URL
import com.pzx.downloader.http.DoHttp.create
import com.pzx.downloader.http.FileInfo
import com.pzx.downloader.utils.Downloader
import com.pzx.downloader.utils.FileOpenUtils.openFile
import com.pzx.downloader.utils.OnDownLoadListener
import com.pzx.downloader.utils.htmlFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(private val application: Application) : AndroidViewModel(application) {


    private val _msg = MutableStateFlow("")
    val msg: StateFlow<String> = _msg

    private val _isDownloadingFile = MutableStateFlow(false)
    val isDownloadingFile: StateFlow<Boolean> = _isDownloadingFile

    private val _fileSize = MutableStateFlow(0)
    val fileSize: StateFlow<Int> = _fileSize

    private val _downloadProgress = MutableStateFlow(100)
    val downloadProgress: StateFlow<Int> = _downloadProgress

    private val _fileList = MutableStateFlow<List<FileInfo>>(emptyList())
    val fileList: StateFlow<List<FileInfo>> = _fileList

    private val _downloadUrl = MutableStateFlow("$BASE_URL$DIRECTORY_URL")
    val downloadUrl: StateFlow<String> = _downloadUrl.asStateFlow()


    init {
        loadFileList()
    }

    fun updateDownloadUrl(url: String) {
        _downloadUrl.value = url
    }

    fun loadFileList() {
        viewModelScope.launch {
            try {
                create(ApiService::class.java).getFileList().run {
                    if (isSuccessful) {
                        _msg.value = ""
                        _fileList.value = htmlFormat(application, body()?.string())
                    } else {
                        _msg.value = "获取文件列表失败"
                        _fileList.value = emptyList()
                    }
                }
            } catch (e: Exception) {
                _msg.value = "获取文件列表失败"
                e.printStackTrace()
            }
        }
    }

    fun downloadFile() {
        Log.e("Pan", "downloadFile${downloadUrl.value}")
        if (!isDownloadingFile.value) {
            Downloader(
                application, downloadUrl.value,
                object : OnDownLoadListener {
                    override fun onExists(file: File) {
                        Log.e("Pan", "------onExists")
                        _isDownloadingFile.value = false
                        openFile(application, file)
                    }

                    override fun onStart(fileName: String) {
                        Log.e("Pan", "------onStart")
                        _isDownloadingFile.value = true
                        _downloadProgress.value = 0
                    }

                    override fun onStop() {
                        Log.e("Pan", "------onStop")
                        _isDownloadingFile.value = false
                    }

                    override fun onProgress(progress: Long, length: Long) {
                        _fileSize.value = (length / 1024).toInt()
                        _downloadProgress.value = (progress / 1024).toInt()
                    }

                    override fun onSuccess(file: File) {
                        Log.e("Pan", "------onSuccess")
                        _isDownloadingFile.value = false
                        openFile(application, file)
                    }

                    override fun onError(msg: String) {
                        Log.e("Pan", "------onError")
                        _isDownloadingFile.value = false
                    }
                },
            ).start()
        }
    }


    companion object {
        fun factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return MainViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }

    }
}