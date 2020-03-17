package com.pzx.downloader.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.pzx.downloader.ui.widget.ColoredToast
import com.pzx.downloader.R
import kotlinx.android.synthetic.main.activity_scan.*

/**
 * File Name : ProcessReportScanActivity
 * Created by : PanZX on  2019/5/9 14:55
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：送货单送货单扫码
 */
class ScanActivity : AppCompatActivity(), QRCodeView.Delegate {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        Zbar.setDelegate(this)
        Zbar.startSpot()
        Zbar.changeToScanQRCodeStyle()
    }

    override fun onStart() {
        super.onStart()
        Zbar.startCamera()
        Zbar.showScanRect()
    }

    override fun onDestroy() {
        Zbar.onDestroy()
        super.onDestroy()
    }

    override fun onStop() {
        Zbar.stopCamera()
        super.onStop()
    }
    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(
                VibrationEffect.createOneShot(
                    200,
                    100
                )
            )
        } else {
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(200)
        }
    }

    override fun onScanQRCodeSuccess(result: String) {
        vibrate()
        Zbar.startSpot()
        setResult(Activity.RESULT_OK, Intent().putExtra("QRCode", result))
        finish()
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {}
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onScanQRCodeOpenCameraError() {
        ColoredToast.long(
            applicationContext,
            "打开相机出错"
        )
    }
    companion object {
        const val ScanQRCode = 11111 //扫码
    }
}