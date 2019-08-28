package com.lg.im.imlibrary.data

import com.lg.im.imlibrary.utils.GlobalContext
import net.grandcentrix.tray.AppPreferences

/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 数据存储
 *
 */
internal object TrayDataUtil {
    private const val ACCOUNT_ID = "ACCOUNT_ID"

    fun saveAccountId(id: String?) {
        val pref = AppPreferences(GlobalContext.ctx)
        if(id.isNullOrEmpty()) pref.remove(ACCOUNT_ID) else pref.put(ACCOUNT_ID, id)
    }

    fun loadAccountId(): String? {
        val pref = AppPreferences(GlobalContext.ctx)
        return pref.getString(ACCOUNT_ID)
    }
}