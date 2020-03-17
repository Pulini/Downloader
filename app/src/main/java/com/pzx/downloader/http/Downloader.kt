package com.pzx.downloader.http

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.pzx.downloader.ui.widget.DownloadFileDialog
import com.pzx.downloader.utils.FileOpenUtils


import java.io.*

import java.net.URL
import java.net.URLConnection

/**
 * File Name : DownloadUtil
 * Created by : PanZX on 2020/03/06
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 下载工具
 */
class Downloader {
    private var mFActivity: FragmentActivity? = null
    private var context: Context? = null
    private var downloadUrl: String? = null
    private var mListener: OnDownLoadListener? = null
    private var mDialog: DownloadFileDialog? = null
    private var mDialogListener: OnDownLoadDialgListener? = null
    private var cache: File? = null

    internal constructor(context: Context, downloadUrl: String, mListener: OnDownLoadListener) {
        this.context = context
        this.downloadUrl = downloadUrl
        this.mListener = mListener
    }

    internal constructor(
        activity: FragmentActivity,
        downloadUrl: String,
        mDialogListener: OnDownLoadDialgListener
    ) {
        this.mFActivity = activity
        context = mFActivity
        this.downloadUrl = downloadUrl
        this.mDialogListener = mDialogListener
        this.mDialog =
            DownloadFileDialog(mFActivity!!, true)
        setDialogListener()
    }

    private var close = true

    interface OnDownLoadListener {
        fun onExists(file: File)
        fun onStart(fileName: String)
        fun onStop()
        fun onProgress(progress: Long, length: Long)
        fun onSuccess(file: File)
        fun onError(msg: String)
    }

    interface OnDownLoadDialgListener {
        fun onSuccess(file: File)
        fun onError(msg: String)
    }

    fun start() {
        close = false
        DownloadThread().start()
        mDialog?.showDialog()
    }

    fun stop() {
        close = true
        mDialog?.dismiss()
    }

    private fun setDialogListener() {
        mDialog!!.setOpenButtonListener(object :
            DownloadFileDialog.OnClickListener {
            override fun onClick(dialog: DownloadFileDialog) {
                FileOpenUtils.openFile(mFActivity!!, cache!!)
            }
        }).setReDownloadListener(object :
            DownloadFileDialog.OnClickListener {
            override fun onClick(dialog: DownloadFileDialog) {
                if (cache!!.delete()) {
                    close = false
                    DownloadThread().start()
                } else {
                    mDialogListener!!.onError("删除失败")
                }
            }
        }).setCloseListener(object :
            DownloadFileDialog.OnClickListener {
            override fun onClick(dialog: DownloadFileDialog) {
                dialog.dismiss()
                close = true
            }
        })
        mListener = object :
            OnDownLoadListener {
            override fun onStart(fileName: String) {
                mDialog!!.setTitle("正在努力下载$fileName")
            }

            override fun onExists(file: File) {
                mDialog!!.fileExists()
            }

            override fun onStop() {
                mDialog!!.setMsg("暂停下载")
            }

            override fun onProgress(progress: Long, length: Long) {
                mDialog!!.setMsg("${b2mb(progress)}/${b2mb(length)}")
                mDialog!!.setProgress((100 * (progress / length.toDouble())).toInt())
            }

            override fun onSuccess(file: File) {
                mDialogListener!!.onSuccess(file)
            }

            override fun onError(msg: String) {
                mDialog!!.dismiss()
                mDialogListener!!.onError(msg)
            }
        }
    }

    internal inner class DownloadThread : Thread() {
        override fun run() {
            super.run()
            try {
                //检查并创建文件夹
                cache = createFile()
                //-------------------------准备下载---------------------------
                val url = URL(downloadUrl)//下载链接
                val connection: URLConnection = url.openConnection()//url控制器
                connection.connect()//开启链接
                val contentLength = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    connection.contentLengthLong
                } else {
                    connection.contentLength.toLong()
                }//文件长度
                //判断文件是否已存在
                if (cache!!.exists() && cache!!.length() == contentLength) {
                    mListener!!.onExists(cache!!)
                } else {
                    //设置读取参数
                    var count: Int
                    var total: Long = 0
                    val buffer = ByteArray(1024)
                    //初始化IO流
                    val input: InputStream = BufferedInputStream(url.openStream())
                    val output: OutputStream = FileOutputStream(cache)
                    mListener!!.onStart(getFileName())
                    //循环读取
                    while (input.read(buffer).also { count = it } != -1) {//不为-1则文件未读完
                        total += count.toLong()//当前读取量
                        mListener!!.onProgress(total, contentLength)
                        output.write(buffer, 0, count)//写入本地
                        if (close) {
                            break
                        }
                    }
                    //下载完成后关闭IO
                    output.flush()
                    output.close()
                    input.close()
                    if (close) {
                        mListener!!.onError("取消下载")
                    } else {
                        mListener!!.onSuccess(cache!!)
                    }
                }
            } catch (e: Exception) {
                Log.e("Pan","下载错误:${e.toString()}")
                mListener!!.onError(e.toString())
            }

        }

        /**
         * 获取下载的文件名
         */
        private fun getFileName() =
            downloadUrl!!.substring(downloadUrl!!.lastIndexOf("/") + 1)

        /**
         * 获取缓存文件夹
         */
        private fun getFolder() =
            File("${context!!.getExternalFilesDir(null)}${File.separator}DownloadFile")


        /**
         * 创建文件夹
         */
        private fun createFile(): File {
            val folder = getFolder()
            if (!folder.exists()) Log.e("Pan", "创建文件夹${folder.mkdirs()}")
            //截取链接末尾/后的文件名称
            val fileName = getFileName()
            //拼接保存文件的完整路径
            return File("${folder.path}${File.separator}$fileName")
        }

    }
    fun b2mb(b: Long): String {
        var size = b
        val rest: Long
        size /= if (size < 1024) {
            return size.toString() + "B"
        } else {
            1024
        }
        if (size < 1024) {
            return size.toString() + "KB"
        } else {
            rest = size % 1024
            size /= 1024
        }
        return if (size < 1024) {
            size *= 100
            (size / 100).toString() + "." + (rest * 100 / 1024 % 100).toString() + "MB"
        } else {
            size = size * 100 / 1024
            (size / 100).toString() + "." + (size % 100).toString() + "GB"
        }
    }
}