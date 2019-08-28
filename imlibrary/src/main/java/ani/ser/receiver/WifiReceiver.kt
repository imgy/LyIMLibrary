package ani.ser.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Handler
import ani.ext.IMCenter
import com.lg.im.imlibrary.utils.LogUtil
import com.lg.im.imlibrary.utils.NetUtil


/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * WIFI检查
 *
 */
class WifiReceiver: BroadcastReceiver() {

    companion object {
        private val hdler = Handler()
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == WifiManager.RSSI_CHANGED_ACTION) {
            LogUtil.d("wifi信号强度变化")
        }
        //wifi连接上与否
        if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {

            val info = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
            if (info!!.state == NetworkInfo.State.DISCONNECTED) {
                LogUtil.d("wifi断开")
            } else if (info.state == NetworkInfo.State.CONNECTED) {
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                //获取当前wifi名称
                LogUtil.d("连接到网络 " + wifiInfo.ssid)
            }
            delayProcess()
        }
        //wifi打开与否
        else if (intent.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
            val wifistate =
                intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED)
            if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                LogUtil.d("系统关闭wifi")
            } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                LogUtil.d("系统开启wifi")
            }
        }
    }

    /**
     * 延时处理任务
     */
    private fun delayProcess() {
        hdler.removeCallbacks(delayRunnable)
        hdler.postDelayed(delayRunnable, 5000)
    }

    private val delayRunnable = Runnable {
        val wifiEnable = NetUtil.getWifiStatus()
        IMCenter.getIMCenter().stopHeartTask()
        if (wifiEnable) {
            IMCenter.getIMCenter().startHeartTask()
        } else {
            IMCenter.getIMCenter().syncDataCenter().netNotAvailable()
        }
    }

}