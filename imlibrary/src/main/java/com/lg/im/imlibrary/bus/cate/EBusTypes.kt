package com.lg.im.imlibrary.bus.cate

/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * Event bus 消息类型
 *
 */
object EBusTypes {
    const val FILE_HEART_BEAT =         1//心跳包
    const val NETWORK_CONNECTED =       3//网络连接
    const val NETWORK_NOT_CONNECTED =   8//网络未连接
    const val FILE_SEND_PROGRESS =      10//文件发送进度
    const val FILE_RECEIVE_PROGRESS =   20//文件接收进度
}