package com.pzx.downloader.http

/**
 * File Name : VersionModel
 * Created by : PanZX on  2018/12/8 14:19
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：
 */
class VersionModel(
    var VersionCode:Int = 0,
    var VersionName: String = "",
    var Url: String = "",
    var Description: String = "",
    var Force:Boolean = false
)