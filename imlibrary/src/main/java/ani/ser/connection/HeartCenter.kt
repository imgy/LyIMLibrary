package ani.ser.connection

import ani.cate.MessageTypes
import ani.ext.IMCenter
import ani.ser.connection.abs.ABSConnectionCenter
import com.lg.im.imlibrary.bus.bean.EBusMessage
import com.lg.im.imlibrary.bus.cate.EBusTypes
import com.lg.im.imlibrary.utils.NetUtil
import com.lg.im.imlibrary.utils.NumberUtil
import com.lg.im.imlibrary.utils.SystemUtil
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 *心跳包管理
 *
 */
internal class HeartCenter: ABSConnectionCenter() {

    init {
        initCountTimer(Long.MAX_VALUE, 10 * 1000)
    }

    private var netAvailable = true//网络是否可用
    private var retryWIFI = false//开闭一次WIFI

    override fun checkConnection() {
        doAsync {
            netNotAvailable()

            if (retryWIFI) {
                NetUtil.changeWifiStatus(false)
                Thread.sleep(500)
                uiThread {
                    NetUtil.changeWifiStatus(true)
                    netAvailable(false)
                }
            }
        }
    }

    override fun sendMessage() {
        doAsync {
            val bts = ArrayList<Byte>()
            bts.addAll(NumberUtil.int2Byte(MessageTypes.HEART_BEAT).toList())
            SystemUtil.macAddress()?.apply {
                bts.addAll(this.toByteArray().toList())
            }
            IMCenter.getIMCenter().msgTransmitter().transmitUDPMessage(bts.toByteArray())
        }
    }

    override fun netAvailable(bus: Boolean) {
        super.netAvailable(bus)
//        if(!netAvailable)//只发一次
//            EventBus.getDefault().post(EBusMessage(EBusTypes.NETWORK_CONNECTED, 0))
        EventBus.getDefault().post(EBusMessage(EBusTypes.NETWORK_CONNECTED, 0))

        netAvailable = true
    }

    override fun netNotAvailable(bus: Boolean) {
        super.netNotAvailable(bus)

//        if(netAvailable && bus)//只发一次
//            EventBus.getDefault().post(EBusMessage(EBusTypes.NETWORK_NOT_CONNECTED, 0))
        if(bus)//只发一次
            EventBus.getDefault().post(EBusMessage(EBusTypes.NETWORK_NOT_CONNECTED, 0))
        netAvailable = false
    }
}