package com.lg.im.imlibrary.utils

import android.content.Context
import android.net.wifi.WifiManager
import java.net.NetworkInterface
import java.net.SocketException


/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 系统信息获取工具
 *
 */
internal object SystemUtil {

    /**
     * 得到mac地址
     */
    @Throws(SocketException::class)
    fun macAddress(): String? {
        var address: String? = null
        // 把当前机器上的访问网络接口的存入 Enumeration集合中
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val netWork = interfaces.nextElement()
            // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
            val by = netWork.hardwareAddress
            if (by == null || by.isEmpty()) {
                continue
            }
            val builder = StringBuilder()
            for (b in by) {
                builder.append(String.format("%02X:", b))
            }
            if (builder.isNotEmpty()) {
                builder.deleteCharAt(builder.length - 1)
            }
            val mac = builder.toString()
            LogUtil.d("interfaceName=" + netWork.getName() + ", mac=" + mac)
            // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
            if (netWork.name == "wlan0") {
                address = mac
            }
        }
        LogUtil.d("macAddress address=$address")
        return address
    }
}