package com.pzx.downloader.http

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    companion object {

        const val BASE_URL = "https://geapp.goldemperor.com:8021/"
        const val DIRECTORY_URL = "AndroidUpdate/GoldEmperor/"
        const val HTML_FILE_PATTERN = """(\d{4}/\d{1,2}/\d{1,2})\s+(\d{1,2}:\d{2})\s+(\d+)\s+<A HREF="([^"]+)">([^<]+)</A>"""
    }

    // 获取文件列表的示例接口
    @GET(DIRECTORY_URL)
    suspend fun getFileList(): Response<ResponseBody>

}