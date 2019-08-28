package ani.internal.msg

import ani.internal.OnSendMessageListener
/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 消息处理者
 *
 */
interface IMsgTransmitter {
    fun transmitUDPMessage(data: ByteArray, listener: OnSendMessageListener? = null)
    fun transmitTCPMessage(ip: String, msg: String, listener: OnSendMessageListener? = null)
    fun transmitFile(ip: String, path: String, listener: OnSendMessageListener? = null)
}