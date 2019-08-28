package ani.ser.connection.abs

import android.os.CountDownTimer

/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 *连接管理
 *
 */
abstract class ABSConnectionCenter {

    private var missingCount = 0

    private lateinit var downTimer: CountDownTimer

    protected fun initCountTimer(millisInFuture: Long, countDownInterval: Long) {
        downTimer = object : CountDownTimer(millisInFuture, countDownInterval){
            override fun onFinish() {

            }

            override fun onTick(millisUntilFinished: Long) {
                sendMessage()
                missingCount++
                if (missingCount > 3) {//3次没有收到心跳包，就检查心跳
                    checkConnection()
                }
            }
        }
    }

    abstract fun checkConnection()
    abstract fun sendMessage()

    open fun startHeartBeat() {
        missingCount = 0
        stopHeartBeat()
        downTimer.start()
    }

    open fun stopHeartBeat() {
        downTimer.cancel()
    }

    /**
     * 重置心跳标志
     */
    open fun netAvailable(bus: Boolean = true) {
        missingCount = 0
    }

    /**
     * 重置心跳标志
     */
    open fun netNotAvailable(bus: Boolean = true) {

    }
}