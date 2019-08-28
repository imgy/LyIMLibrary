package ani.ser.connection

import ani.bean.sync.SyncDataBean
import ani.cate.MessageTypes
import ani.ext.IMCenter
import ani.ser.connection.abs.ABSSyncDataCenter
import com.google.gson.reflect.TypeToken
import com.lg.im.imlibrary.bus.bean.EBusMessage
import com.lg.im.imlibrary.bus.cate.EBusTypes
import com.lg.im.imlibrary.utils.NetUtil
import com.lg.im.imlibrary.utils.NumberUtil
import com.lg.im.imlibrary.utils.ZipUtil
import com.zqkh.commlibrary.utilslibrary.utils.JsonUtils
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 同步数据管理
 *
 */
class SyncDataCenter: ABSSyncDataCenter() {

    init {
        initCountTimer(Long.MAX_VALUE, 45 * 1000)
    }

    private val dataMap = Hashtable<String, SyncDataBean>()
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
        if (dataMap.isNotEmpty()) {
            doAsync {
//                val bts = ArrayList<Byte>()
//                bts.addAll(NumberUtil.int2Byte(MessageTypes.SYNC_DATA).toList())
//
//                val orgMap = HashMap<String, String>()
//                dataMap.forEach { orgMap[it.key] = ZipUtil.uncompressByte(it.value) }
//                bts.addAll(ZipUtil.compressToByte(JsonUtils.toJsonString(orgMap)!!).toList())
//
//                IMCenter.getIMCenter().msgTransmitter().transmitUDPMessage(bts.toByteArray())

                val orgMap = HashMap<String, String>()
                dataMap.forEach { orgMap[it.key] = ZipUtil.uncompressByte(it.value.data) }

                val bts = ArrayList<Byte>()
                orgMap.toList().sortedBy { it.first }.forEach {
                    bts.clear()
                    bts.addAll(NumberUtil.int2Byte(MessageTypes.SYNC_DATA).toList())
                    bts.addAll(ZipUtil.compressToByte(JsonUtils.toJsonString(it)!!).toList())

                    IMCenter.getIMCenter().msgTransmitter().transmitUDPMessage(bts.toByteArray())

                    Thread.sleep(3000)
                }
            }
        }
    }

    /**
     * 同步最新的数据
     */
    @Synchronized override fun syncData(str: String?) {
        if(str.isNullOrEmpty()) return

        val data = JsonUtils.fromJson<Pair<String, SyncDataBean>>(str, object : TypeToken<Pair<String, SyncDataBean>>(){}.type)
        data?.let {
            val orgData = dataMap[data.first]
            orgData?.apply {
                if(this.date < data.second.date)
                    dataMap[data.first] = data.second
            }
        }
    }

    /**
     * 存入同步数据
     */
    @Synchronized override fun putSyncData(key: String, date: Long?, data: String?) {
        if(data.isNullOrEmpty() || date == null)
            dataMap.remove(key)
        else
            dataMap[key] = SyncDataBean(date = date, data = ZipUtil.compressToByte(data))
    }

    override fun netAvailable(bus: Boolean) {
        super.netAvailable(bus)
        if(!netAvailable)//只发一次
            EventBus.getDefault().post(EBusMessage(EBusTypes.NETWORK_CONNECTED, 0))
//        EventBus.getDefault().post(EBusMessage(EBusTypes.NETWORK_CONNECTED, 0))

        netAvailable = true
    }

    override fun netNotAvailable(bus: Boolean) {
        super.netNotAvailable(bus)

        if(netAvailable && bus)//只发一次
            EventBus.getDefault().post(EBusMessage(EBusTypes.NETWORK_NOT_CONNECTED, 0))
//        if(bus)//只发一次
//            EventBus.getDefault().post(EBusMessage(EBusTypes.NETWORK_NOT_CONNECTED, 0))
        netAvailable = false
    }

}