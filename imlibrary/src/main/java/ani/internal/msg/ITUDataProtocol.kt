package ani.internal.msg

import ani.bean.receive.ReceiveMessageBean
/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 消息处理协议
 *
 */
interface ITUDataProtocol {
    fun enable()
    fun disable()
    fun listenerError(err: String)
    fun listenerSuccess(msg: ReceiveMessageBean)
}