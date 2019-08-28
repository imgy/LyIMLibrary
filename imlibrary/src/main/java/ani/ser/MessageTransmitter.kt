package ani.ser

import ani.exp.TransmitterException
import ani.internal.OnSendMessageListener
import ani.internal.msg.IMsgTransmitter
import ani.tcp.transmitter.TCPTransmitter
import ani.udp.transmitter.UDPTransmitter

/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 消息处理者
 *
 */
internal class MessageTransmitter: IMsgTransmitter {
    private var udpTranster: UDPTransmitter? = null//UDP发送

    init {
        udpTranster = UDPTransmitter()
    }

    /**
     * 发送UDP消息
     */
    override fun transmitUDPMessage(data: ByteArray, listener: OnSendMessageListener?) {
        try {
            udpTranster?.transmit(data)
            listener?.onSuccess()
        } catch (e: TransmitterException) {
            e.printStackTrace()
            listener?.onError("Could not transmitMessage: " + e.message)
        }
    }

    /**
     * 发送TCP消息
     */
    override fun transmitTCPMessage(ip: String, msg: String, listener: OnSendMessageListener?) {
        try {
            val tcpTranster = TCPTransmitter(ip, listener = listener)//"172.30.230.37"
            tcpTranster.transmit(msg.toByteArray())
        } catch (e: TransmitterException) {
            e.printStackTrace()
            listener?.onError("Could not transmitFile: " + e.message)
        }

    }

    /**
     * 发送文件
     */
    override fun transmitFile(ip: String, path: String, listener: OnSendMessageListener?) {
        try {
            val tcpTranster = TCPTransmitter(ip, listener = listener)//"172.30.230.37"
            tcpTranster.transmit(path)
        } catch (e: TransmitterException) {
            e.printStackTrace()
            listener?.onError("Could not transmitFile: " + e.message)
        }

    }
}