package ani.ext

import ani.internal.msg.ITUMessageCenter
import ani.ser.MessageCenter

/**

 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 消息处理中心
 *
 */
object IMCenter {
    private val msgCenter = MessageCenter()

    /**
     * 得到消息中心
     */
    fun getIMCenter(): ITUMessageCenter {
        return msgCenter
    }
}