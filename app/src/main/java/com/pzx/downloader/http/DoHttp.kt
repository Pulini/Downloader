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
    var path: String = "http://geapp.goldemperor.com:8020/AndroidUpdate/GoldEmperor/GoldEmperor.apk"
    val APKpath = "http://geapp.goldemperor.com:8020/"

    fun <T> create(service: Class<T>): T = Retrofit.Builder()
        .baseUrl(APKpath)
        .addConverterFactory(GsonConverterFactory.create())
        .client(createHttpClient())
        .build()
        .create(service)

    private fun createHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val builder = OkHttpClient.Builder()
        builder.readTimeout(1000 * 20, TimeUnit.SECONDS)
        builder.writeTimeout(1000 * 20, TimeUnit.SECONDS)
        builder.connectTimeout(1000 * 20, TimeUnit.SECONDS)
        builder.retryOnConnectionFailure(true)
        builder.addInterceptor(interceptor)
        return builder.build()
    }
}