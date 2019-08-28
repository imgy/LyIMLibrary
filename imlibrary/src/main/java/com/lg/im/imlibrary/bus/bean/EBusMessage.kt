package com.lg.im.imlibrary.bus.bean

/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * Event bus消息体
 *
 */
class EBusMessage<T>(
    val type: Int,     //消息类型
    val data: T        //消息内容
)