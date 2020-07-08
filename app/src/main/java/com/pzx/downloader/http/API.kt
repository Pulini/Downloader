package com.pzx.downloader.http

import com.pzx.downloader.model.CanteenScanVersionModel
import com.pzx.downloader.model.GoldEmperorVersionModel
import retrofit2.Call
import retrofit2.http.GET

/**
 * File Name : API
 * Created by : PanZX on 2020/03/10
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 接口
 */
interface API {
    @GET("AndroidUpdate/GoldEmperor/GoldEmperorUpData.xml")
    fun getGoldEmperorApkPath(): Call<GoldEmperorVersionModel>

    @GET("AndroidUpdate/GoldEmperor/CanteenScanUpData.xml")
    fun getCanteenScanApkPath(): Call<CanteenScanVersionModel>
}