package com.lg.im.imlibrary.utils

import android.content.Context
import android.net.wifi.WifiInfo
import android.content.Context.WIFI_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.net.wifi.WifiManager

/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 网络工具类
 *
 */
/**
 * 网络工具类
 */
object NetUtil {

    /**
     *
     * 获取WIFI下ip地址
     */
    fun getLocalIpAddress(): String {
        val wifiManager = GlobalContext.ctx?.applicationContext?.getSystemService(WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiManager!!.connectionInfo
        // 获取32位整型IP地址
        val ipAddress = wifiInfo.ipAddress

        //返回整型地址转换成“*.*.*.*”地址
        return String.format(
            "%d.%d.%d.%d",
            ipAddress and 0xff, ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff, ipAddress shr 24 and 0xff
        )
    }

    /**
     * 改变WIFI状态
     */
    fun changeWifiStatus(on: Boolean) {
        val mWifiManager = GlobalContext.ctx!!.getSystemService(WIFI_SERVICE) as WifiManager
        mWifiManager.isWifiEnabled = on
    }

    /**
     * 判断WIFI状态
     */
    fun getWifiStatus(): Boolean {
        val mWifiManager = GlobalContext.ctx!!.getSystemService(WIFI_SERVICE) as WifiManager
        val status = mWifiManager.isWifiEnabled && mWifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED
        return status
    }
}