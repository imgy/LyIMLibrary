package ani.ser.connection.abs

import ani.bean.sync.SyncDataBean


/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 *连接管理
 *
 */
abstract class ABSSyncDataCenter: ABSConnectionCenter() {

    /**
     * 同步数据
     */
    abstract fun syncData(str: String?)

    /**
     * 存入同步数据
     */
    abstract fun putSyncData(key: String, date: Long? = null, data: String? = null)
}