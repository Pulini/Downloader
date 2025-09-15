package com.pzx.downloader.http

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * File Name : DoHttp
 * Created by : PanZX on 2020/03/10
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 接口请求
 */

object DoHttp {
    fun <T> create(service: Class<T>): T = Retrofit.Builder()
        .baseUrl(ApiService.Companion.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().apply {
            readTimeout(1000 * 20, TimeUnit.SECONDS)
            writeTimeout(1000 * 20, TimeUnit.SECONDS)
            connectTimeout(1000 * 20, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
            addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
        }.build())
        .build()
        .create(service)
}