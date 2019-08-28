package ani.internal.msg

import ani.internal.OnReceiveMessageListener
import ani.ser.connection.abs.ABSSyncDataCenter
/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 消息中心
 *
 */
interface ITUMessageCenter {
    /**
     * 消息发送操作对象
     */
    fun msgTransmitter(): IMsgTransmitter

//    /**
//     * 心跳对象
//     */
//    fun heartCenter()

    /**
     * 同步数据对象
     */
    fun syncDataCenter(): ABSSyncDataCenter

    /**
     * 注册消息监听
     */
    fun register(accountId: String, listener: OnReceiveMessageListener? = null)

    /**
     * 取消消息监听
     */
    fun unRegister(listener: OnReceiveMessageListener? = null)

    /**
     * 开始心跳任务
     */
    fun startHeartTask()

    /**
     * 开始心跳任务
     */
    fun stopHeartTask()
}