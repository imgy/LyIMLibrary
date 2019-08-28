package ani.ser

import android.text.TextUtils
import ani.bean.receive.ReceiveMessageBean
import ani.cate.MessageTypes
import ani.internal.OnReceiveMessageListener
import ani.internal.msg.IMsgTransmitter
import ani.internal.msg.ITUDataProtocol
import ani.internal.msg.ITUMessageCenter
import ani.ser.connection.SyncDataCenter
import ani.ser.connection.abs.ABSSyncDataCenter
import ani.tcp.discovery.TCPDiscovery
import ani.tcp.discovery.lis.TCPDiscoveryListener
import ani.tcp.transmitter.TCPTransmitter
import ani.udp.discovery.UDPDiscovery
import ani.udp.discovery.lis.UDPDiscoveryListener
import ani.udp.transmitter.UDPTransmitter
import com.lg.im.imlibrary.data.TrayDataUtil
import com.lg.im.imlibrary.utils.LogUtil
import com.lg.im.imlibrary.utils.NetUtil

/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 消息处理中心
 *
 */
internal class MessageCenter: ITUMessageCenter {
    private val listenerList = ArrayList<OnReceiveMessageListener>()//监听器列表

//    private var heartBeat = HeartCenter()//心跳数据
    private var syncData = SyncDataCenter()//同步数据
    private var udp: UDPCenter//UDP连接
    private var tcp: TCPCenter//TCP发送
    private var transmitter: IMsgTransmitter//消息发送

    init {

        udp = UDPCenter(listenerList)
        tcp = TCPCenter(listenerList)
        transmitter = MessageTransmitter()
    }

    /**
     * 消息发送操作对象
     */
    override fun msgTransmitter(): IMsgTransmitter {
        return transmitter
    }

//    /**
//     * 心跳对象
//     */
//    fun heartCenter(): ABSConnectionCenter {
//        return heartBeat
//    }

    /**
     * 同步数据对象
     */
    override fun syncDataCenter(): ABSSyncDataCenter {
        return syncData
    }

    /**
     * 注册消息监听
     */
    override fun register(accountId: String, listener: OnReceiveMessageListener?) {
        listener?.apply { listenerList.add(listener) }
        TrayDataUtil.saveAccountId(accountId)

        stopHeartTask()
        startHeartTask()
    }

    /**
     * 取消消息监听
     */
    override fun unRegister(listener: OnReceiveMessageListener?) {
        stopHeartTask()

        listenerList.remove(listener)
        TrayDataUtil.saveAccountId(null)
    }

    /**
     * 开始心跳任务
     */
    override fun startHeartTask() {
        udp.enable()
        tcp.enable()

//        heartCenter().startHeartBeat()
        syncDataCenter().startHeartBeat()
    }

    /**
     * 开始心跳任务
     */
    override fun stopHeartTask() {
//        heartCenter().stopHeartBeat()
        syncDataCenter().stopHeartBeat()

        udp.disable()
        tcp.disable()
    }

//    private fun listenerError(err: String) {
//        listenerList.forEach { it.onError(err) }
//    }
//
//    private fun listenerSuccess(msg: ReceiveMessageBean) {
//        listenerList.forEach { it.onMessage(msg) }
//    }

    /**
     * UDP消息中心
     */
    private inner class UDPCenter(val listenerList: List<OnReceiveMessageListener>) :
        ITUDataProtocol {

        private var udpDiscover: UDPDiscovery? = null//UDP连接
        private var udpTranster: UDPTransmitter? = null//UDP发送

        init {
            udpDiscover = UDPDiscovery()
            udpDiscover?.setDisoveryListener(object : UDPDiscoveryListener {

                override fun onDiscoveryError(exception: Exception) {
                    listenerError("* (!) udpDiscover error: " + exception.message)
                }

                override fun onIntentDiscovered(message: ReceiveMessageBean?) {
                    if (!TextUtils.isEmpty(message?.data)) {

                        //重置心跳
//                        heartCenter().netAvailable()

                        syncDataCenter().netAvailable()
                        syncDataCenter().syncData(message?.data)

                        if (message?.address?.hostAddress != NetUtil.getLocalIpAddress()
                            && message?.type != MessageTypes.HEART_BEAT
                        ) {//是自己或者心跳就忽略
                            listenerSuccess(message!!)
                        }
                    } else {
                        listenerError("* (!) Received Intent without message")
                    }


                }

                override fun onDiscoveryStarted() {
                    LogUtil.d("* (>) udpDiscover started")
                }

                override fun onDiscoveryStopped() {
                    LogUtil.d("* (<) udpDiscover stopped")
                }
            })
            udpTranster = UDPTransmitter()
        }

        override fun enable() {
            udpDiscover?.enable()
        }

        override fun disable() {
            udpDiscover?.disable()
        }

        override fun listenerError(err: String) {
            listenerList.forEach { it.onError(err) }
        }

        override fun listenerSuccess(msg: ReceiveMessageBean) {
            listenerList.forEach { it.onMessage(msg) }
        }
    }

    /**
     * TCP消息中心
     */
    private inner class TCPCenter(val listenerList: List<OnReceiveMessageListener>) :
        ITUDataProtocol {
        private var tcpDiscover: TCPDiscovery? = null//TCP连接
        private var tcpTranster: TCPTransmitter? = null//TCP发送

        init {
            tcpDiscover = TCPDiscovery()
            tcpDiscover?.setDisoveryListener(object : TCPDiscoveryListener {
                override fun onProgress(progress: Int, cur: Long, total: Long) {

                }

                override fun onDiscoveryError(exception: Exception) {
                    listenerError("* (!) tcpDiscover error: " + exception?.message)
                }

                override fun onIntentDiscovered(message: ReceiveMessageBean?) {
                    if (!TextUtils.isEmpty(message?.data)) {
                        listenerSuccess(message!!)
                    } else {
                        LogUtil.d("* (!) Received Intent without message")
                    }


                }

                override fun onDiscoveryStarted() {
                    LogUtil.d("* (>) tcpDiscover started")
                }

                override fun onDiscoveryStopped() {
                    LogUtil.d("* (<) tcpDiscover stopped")
                }
            })
            tcpTranster = TCPTransmitter("172.30.230.37")
        }

        override fun enable() {
            tcpDiscover?.enable()
        }

        override fun disable() {
            tcpDiscover?.disable()
        }

        override fun listenerError(err: String) {
            listenerList.forEach { it.onError(err) }
        }

        override fun listenerSuccess(msg: ReceiveMessageBean) {
            listenerList.forEach { it.onMessage(msg) }
        }
    }
}