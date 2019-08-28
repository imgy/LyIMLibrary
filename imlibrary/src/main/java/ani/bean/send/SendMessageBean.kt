package ani.bean.send

import java.net.InetAddress

/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 普通消息格式
 */
internal class SendMessageBean(
    val type: Int = 0,          //消息类型
    val uniqueId: Int = 0,  //消息唯一识别ID
    var address: InetAddress? = null,  //对方的地址
    var data: String? = null,           //消息内容
    var file: String? = null           //文件
)