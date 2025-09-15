package com.pzx.downloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pzx.downloader.ui.theme.DownloaderTheme
import com.pzx.downloader.http.FileInfo
import com.pzx.downloader.utils.Downloader
import com.pzx.downloader.utils.FileOpenUtils
import com.pzx.downloader.utils.OnDownLoadListener
import com.pzx.downloader.widget.NumberProgressBar
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DownloaderTheme {
                val viewModel: MainViewModel =
                    viewModel(factory = MainViewModel.factory(application))
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DownloadPage(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadPage(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    Column(
        modifier = modifier.absolutePadding(
            left = 10.dp,
            top = 30.dp,
            right = 10.dp,
            bottom = 10.dp
        )
    ) {
        Input(viewModel = viewModel)
        FileList(viewModel = viewModel)
    }
}

@Composable
fun Input(viewModel: MainViewModel) {
    val downloadUrl = viewModel.downloadUrl.collectAsState()
    val isDownloadingFile = viewModel.isDownloadingFile.collectAsState()
    val fileSize = viewModel.fileSize.collectAsState()
    val downloadProgress = viewModel.downloadProgress.collectAsState()
    if (isDownloadingFile.value) {
        NumberProgressBar(
            progress = downloadProgress.value,
            max = fileSize.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
    }
    Row {
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, end = 20.dp),
            value = downloadUrl.value,
            label = { Text("请输入下载地址") },
            onValueChange = { viewModel.updateDownloadUrl(it) }
        )
        if (!isDownloadingFile.value) {
            Button(onClick = { viewModel.downloadFile() }) {
                Text("下载")
            }
        }
    }

}

@Composable
fun FileList(viewModel: MainViewModel) {
    val fileList = viewModel.fileList.collectAsState()
    LazyColumn {
        items(fileList.value.size) { index -> FileItem(fileList.value[index]) }
    }
}

@Composable
fun FileItem(fileInfo: FileInfo) {
    val context = LocalContext.current
    var isDownloading by remember { mutableStateOf(false) }
    var numberProgress by remember { mutableIntStateOf(0) }
    var maxProgress by remember { mutableIntStateOf(100) }
    var downloader by remember {
        mutableStateOf(
            Downloader(
                context, fileInfo.url,
                object : OnDownLoadListener {
                    override fun onExists(file: File) {
                        isDownloading = false
                    }

                    override fun onStart(fileName: String) {
                        isDownloading = true
                        numberProgress = 0
                    }

                    override fun onStop() {
                        isDownloading = false
                    }

                    override fun onProgress(progress: Long, length: Long) {
                        maxProgress = (length / 1024).toInt()
                        numberProgress = (progress / 1024).toInt()
                    }

                    override fun onSuccess(file: File) {
                        isDownloading = false
                        fileInfo.isExists = true
                    }

                    override fun onError(msg: String) {
                        isDownloading = false
                    }
                },
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = fileInfo.name,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "大小: ${fileInfo.fileSize()}",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "修改日期: ${fileInfo.date}"
                )
            }
            if (isDownloading) {
                NumberProgressBar(
                    progress = numberProgress,
                    max = maxProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // 根据文件状态显示不同按钮
            if (fileInfo.isExists && !isDownloading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            fileInfo.getFile(context).delete()
                            downloader.start()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    ) {
                        Text("重新下载")
                    }

                    Button(
                        onClick = {
                            FileOpenUtils.openFile(context, fileInfo.getFile(context))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    ) {
                        Text("安装")
                    }
                }
            } else {
                Button(
                    onClick = {
                        if (isDownloading) {
                            downloader.stop()
                            isDownloading = false
                        } else {
                            downloader.start()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isDownloading) "取消下载" else "下载")
                }
            }
        }
    }
}
