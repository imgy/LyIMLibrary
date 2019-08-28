package ani.internal

import ani.bean.receive.ReceiveMessageBean
/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 接收消息
 *
 */
interface OnReceiveMessageListener {
    fun onMessage(msg: ReceiveMessageBean)
    fun onError(err: String)
}