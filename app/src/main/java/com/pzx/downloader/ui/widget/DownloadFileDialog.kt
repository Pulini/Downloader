package com.pzx.downloader.ui.widget

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.pzx.downloader.R
import com.pzx.downloader.utils.setDelayClickListener


/**
 * File Name : DownloadFileDialog
 * Created by : PanZX on 2020/03/04
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 下载弹窗
 */
class DownloadFileDialog(var fa: FragmentActivity, var isShowClose: Boolean) : DialogFragment() {


    private var mTitle: TextView? = null
    private var mOpen: TextView? = null
    private var mBack: TextView? = null
    private var numberProgressBar: NumberProgressBar? = null
    private var mReDownload: TextView? = null
    private var mMsg: TextView? = null
    private var mReturn: TextView? = null
    private var mOpenClickListener: OnClickListener? = null
    private var mReDownloadClickListener: OnClickListener? = null
    private var mCloseClickListener: OnClickListener? = null


    interface OnClickListener {
        fun onClick(dialog: DownloadFileDialog)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window
        val params = window!!.attributes
        params.gravity = Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = params
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mRootView = inflater.inflate(R.layout.dialog_download, container, false);
        mTitle = mRootView?.findViewById(R.id.tv_title)
        mOpen = mRootView?.findViewById(R.id.tv_open)
        mBack = mRootView?.findViewById(R.id.tv_back)
        numberProgressBar = mRootView?.findViewById(R.id.npb_progress)
        mReDownload = mRootView?.findViewById(R.id.tv_reDownload)
        mMsg = mRootView?.findViewById(R.id.tv_msg)
        mReturn = mRootView?.findViewById(R.id.tv_return)

        mOpen?.setDelayClickListener { mOpenClickListener?.onClick(this) }
        mReDownload?.setDelayClickListener { mReDownloadClickListener?.onClick(this) }
        mBack?.setDelayClickListener { mCloseClickListener?.onClick(this) }
        mReturn?.setDelayClickListener { dismiss() }
        setAttribute()
        setStyles()
        return mRootView
    }

    private fun setAttribute() {
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true
                }
                return false
            }
        })
    }

    private fun setStyles() {
        val window = this.dialog!!.window
        //去掉dialog默认的padding
        window!!.decorView.setPadding(0, 0, 0, 0)
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        //设置dialog的位置在底部
        lp.gravity = Gravity.BOTTOM
        //设置dialog的动画
        lp.windowAnimations = R.style.BottomDialogAnimation;
        window.attributes = lp
        window.setBackgroundDrawable(ColorDrawable())

    }

    fun setTitle(title: String) {
        fa.runOnUiThread {
            mTitle?.text = title
        }
    }

    fun fileExists() {
        fa.runOnUiThread {
            numberProgressBar?.progress = 100
            mTitle?.text = "文件已存在！！"
            mBack?.visibility = View.GONE
            mMsg?.visibility = View.GONE
            mReturn?.visibility = if (isShowClose) View.VISIBLE else View.GONE
            mOpen?.visibility = if (mOpenClickListener != null) View.VISIBLE else View.GONE
            mReDownload?.visibility =
                if (mReDownloadClickListener != null) View.VISIBLE else View.GONE
        }
    }

    fun setMsg(msg: String) {
        fa.runOnUiThread {
            mMsg?.text = msg
        }
    }

    fun setProgress(progress: Int) {
        fa.runOnUiThread {
            numberProgressBar?.progress = progress
            if (progress < numberProgressBar!!.max) {
                mOpen?.visibility = View.GONE
                mReDownload?.visibility = View.GONE
                mReturn?.visibility = View.GONE
                mMsg?.visibility = View.VISIBLE
                mBack?.visibility = if (isShowClose) View.VISIBLE else View.GONE
            }
            if (progress == numberProgressBar!!.max) {
                mOpen?.visibility = if (mOpenClickListener != null) View.VISIBLE else View.GONE
                mReDownload?.visibility =
                    if (mReDownloadClickListener != null) View.VISIBLE else View.GONE
                mBack?.visibility = View.GONE
                mMsg?.visibility = View.GONE
                mReturn?.visibility = if (isShowClose) View.VISIBLE else View.GONE
            }
        }
    }

    fun setOpenButtonListener(cl: OnClickListener): DownloadFileDialog {
        mOpenClickListener = cl
        return this
    }

    fun setReDownloadListener(cl: OnClickListener): DownloadFileDialog {
        mReDownloadClickListener = cl
        return this
    }

    fun setCloseListener(cl: OnClickListener): DownloadFileDialog {
        mCloseClickListener = cl
        return this
    }

    fun showDialog() {
        numberProgressBar?.max = 100
        numberProgressBar?.progress = 0
        mTitle?.text = "title"
        mMsg?.text = "msg"

        mOpen?.visibility = View.GONE
        mReDownload?.visibility = View.GONE
        mReturn?.visibility = View.GONE
        mMsg?.visibility = View.VISIBLE
        mBack?.visibility = if (isShowClose) View.VISIBLE else View.GONE

        show(fa.supportFragmentManager, "DownloadDialog")
    }

}