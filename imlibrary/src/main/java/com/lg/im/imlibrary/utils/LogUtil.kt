package com.lg.im.imlibrary.utils

import android.util.Log

/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 日志
 *
 */
internal object LogUtil {
    private const val DEBUG = true
    private const val TAG = "mylog"

    fun d(str: String) {
        if(DEBUG) Log.d(TAG, str)
    }
}