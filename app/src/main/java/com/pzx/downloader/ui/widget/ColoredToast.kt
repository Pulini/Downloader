package com.pzx.downloader.ui.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.pzx.downloader.R
import java.lang.Exception

/**
 * File Name : ColoredToast
 * Created by : PanZX on 2020/03/05
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 彩色吐司
 */
class ColoredToast private constructor(context: Context) : Toast(context) {

    companion object {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        fun long(mContext: Context, msg: String) {
            try{
                Maker(mContext)
                    .setColor(0xFFFFFFFF.toInt(), 0xFF2FCAFA.toInt())
                    .long(msg)
                    .show()
            }catch (e:Exception){
                Looper.prepare()
                Maker(mContext)
                    .setColor(0xFFFFFFFF.toInt(), 0xFF2FCAFA.toInt())
                    .long(msg)
                    .show()
                Looper.loop()
            }
        }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        fun long(mContext: Context, msg: Int) {
            try{
                Maker(mContext)
                    .setColor(0xFFFFFFFF.toInt(), 0xFF2FCAFA.toInt())
                    .long(msg)
                    .show()
            }catch (e:Exception){
                Looper.prepare()
                Maker(mContext)
                    .setColor(0xFFFFFFFF.toInt(), 0xFF2FCAFA.toInt())
                    .long(msg)
                    .show()
                Looper.loop()
            }
        }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        fun short(mContext: Context, msg: String) {
            try{
                Maker(mContext)
                    .setColor(0xFFFFFFFF.toInt(), 0xFF2FCAFA.toInt())
                    .short(msg)
                    .show()
            }catch (e:Exception){
                Looper.prepare()
                Maker(mContext)
                    .setColor(0xFFFFFFFF.toInt(), 0xFF2FCAFA.toInt())
                    .short(msg)
                    .show()
                Looper.loop()
            }
        }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        fun short(mContext: Context, msg: Int) {
            try{
                Maker(mContext)
                    .setColor(0xFFFFFFFF.toInt(), 0xFF2FCAFA.toInt())
                    .short(msg)
                    .show()
            }catch (e:Exception){
                Looper.prepare()
                Maker(mContext)
                    .setColor(0xFFFFFFFF.toInt(), 0xFF2FCAFA.toInt())
                    .short(msg)
                    .show()
                Looper.loop()
            }
        }
    }

    class Maker(private var mContext: Context) {
        private val mToast: ColoredToast =
            ColoredToast(mContext)
        private var mToastView: View =
            LayoutInflater.from(mContext).inflate(R.layout.view_color_toast, null)
        private var mTextMessage: TextView = mToastView.findViewById(R.id.tv_message)


        /**
         * Set text color and background color for toast by resource id
         */
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        fun setColor(textColor: Int, backgroundColor: Int): Maker {
            val drawable = GradientDrawable()
            drawable.setColor(backgroundColor)
            drawable.cornerRadius = mTextMessage.layoutParams.height / 2.toFloat()
            mToastView.background = drawable
            mTextMessage.setTextColor(textColor)
            return this
        }

        fun long(resId: Int): ColoredToast {
            mTextMessage.text = mContext.resources.getString(resId)
            mToast.view = mToastView
            mToast.duration = LENGTH_LONG
            return mToast
        }

        fun long(text: String): ColoredToast {
            mTextMessage.text = text
            mToast.view = mToastView
            mToast.duration = LENGTH_LONG
            return mToast
        }

        fun short(resId: Int): ColoredToast {
            mTextMessage.text = mContext.resources.getString(resId)
            mToast.view = mToastView
            mToast.duration = LENGTH_SHORT
            return mToast
        }

        fun short(text: String): ColoredToast {
            mTextMessage.text = text
            mToast.view = mToastView
            mToast.duration = LENGTH_SHORT
            return mToast
        }

    }
}