package ani.internal
/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 *发送消息
 *
 */
interface OnSendMessageListener {
    fun onSuccess()
    fun onProgress(progress: Int, cur: Long, total: Long)
    fun onError(err: String)
}