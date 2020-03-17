package com.pzx.downloader.utils

import android.view.View
import com.pzx.downloader.utils.ViewClickDelay.SPACE_TIME
import com.pzx.downloader.utils.ViewClickDelay.hash
import com.pzx.downloader.utils.ViewClickDelay.lastClickTime

/**
 * File Name : ViewClickDelay
 * Created by : PanZX on 2020/02/20
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： kotlin防止多次点击
 */
object ViewClickDelay {
    var hash: Int = 0
    var lastClickTime: Long = 0
    var SPACE_TIME: Long = 3000
}

infix fun View.setDelayClickListener(clickAction: () -> Unit) {
    this.setOnClickListener {
        if (this.hashCode() != hash) {
            hash = this.hashCode()
            lastClickTime = System.currentTimeMillis()
            clickAction()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > SPACE_TIME) {
                lastClickTime = System.currentTimeMillis()
                clickAction()
            }
        }
    }
}