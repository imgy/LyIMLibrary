package ani.udp.discovery.lis

/**
 * 扫描出来的成员们
 */
internal object UDPMember {
    val memberList = HashMap<String, String>()// ip -> account

    /**
     * 得到IP地址
     */
    fun getMemberAccount(address: String?): String? {
        return memberList[address]
    }
    /**
     * 得到IP地址
     */
    fun getMemberAddress(account: String?): String? {
        val result = memberList.filterValues { it == account }
        return if(result.isNullOrEmpty()) null else result.keys.toList()[0]
    }
}