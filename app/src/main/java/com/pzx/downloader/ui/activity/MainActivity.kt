package com.pzx.downloader.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.pzx.downloader.ui.widget.ColoredToast
import com.pzx.downloader.R
import com.pzx.downloader.http.API
import com.pzx.downloader.http.DoHttp
import com.pzx.downloader.http.Downloader
import com.pzx.downloader.http.VersionModel
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    var path = "http://geapp.goldemperor.com:8020/AndroidUpdate/GoldEmperor/GoldEmperor.apk"

    companion object {
        @RequiresApi(Build.VERSION_CODES.M)
        val PERMS_WRITE = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES,
            Manifest.permission.VIBRATE,
            Manifest.permission.CAMERA
        )
        const val REQUEST_PERMISSIONS_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions()
    }

    fun checkVersion(v: View) {
        getVersion()
    }

    fun scan(v: View) {
        startActivityForResult(
            Intent(this, ScanActivity::class.java),
            ScanActivity.ScanQRCode
        )
    }

    fun download(v: View) {
      val path=  et_path.text.toString()
        if(path.isEmpty()){
            ColoredToast.long(applicationContext, "请填写下载地址")
            return
        }
        bt_download.isEnabled = false
        Downloader(
            this,
            path,
            object :
                Downloader.OnDownLoadDialgListener {
                override fun onSuccess(file: File) {
                    runOnUiThread {
                        bt_download.isEnabled = true
                        ColoredToast.long(
                            applicationContext,
                            "下载完成"
                        )
                    }
                }

                override fun onError(msg: String) {
                    runOnUiThread {
                        bt_download.isEnabled = true
                        ColoredToast.long(
                            applicationContext,
                            "下载失败:$msg"
                        )
                    }
                }
            }).start()
    }

    private fun getVersion() {
        DoHttp.create(API::class.java).getApkPath().enqueue(object : Callback<VersionModel> {
            override fun onFailure(call: Call<VersionModel>, t: Throwable) {
                Log.e("Pan", "onFailure")
                path = DoHttp.path
            }

            override fun onResponse(call: Call<VersionModel>, response: Response<VersionModel>) {
                Log.e("Pan", "Url=${response.body()?.Url}")
                val vm = response.body()
                path = vm!!.Url
                ColoredToast.long(applicationContext, "最新版本:${vm.VersionName}")
                tv_msg.text="最新版本:${vm.VersionName}\n版本说明:${vm.Description}"
                et_path.setText(path)
            }
        })
    }

    private fun requestPermissions() {
        if (EasyPermissions.hasPermissions(this, *PERMS_WRITE)) {
            Log.e("Pan", "有权限")
        } else {
            EasyPermissions.requestPermissions(
                this,
                "APP需要网络、本地读写和安装APK的权限",
                REQUEST_PERMISSIONS_REQUEST_CODE,
                *PERMS_WRITE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.e("Pan", "用户授权成功${perms}")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.e("Pan", "用户授权失败${perms}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("Pan", "requestCode=" + requestCode + "resultCode=" + resultCode)
        if (RESULT_OK == resultCode && requestCode == ScanActivity.ScanQRCode)
            et_path.setText(data!!.extras!!.getString("QRCode"))

    }
}
