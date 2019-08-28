/*
 * Copyright (C) 2013 Sebastian Kaspari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ani.udp.discovery

import ani.bean.receive.ReceiveMessageBean
import ani.cate.MessageTypes
import ani.udp.discovery.lis.UDPDiscoveryListener
import com.lg.im.imlibrary.utils.LogUtil
import com.lg.im.imlibrary.utils.NumberUtil
import com.lg.im.imlibrary.utils.ZipUtil
import java.io.IOException
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.net.UnknownHostException

/**
 * Internal class for handling the network connection of the [UDPDiscovery] class
 * on a background thread.
 */
internal class UDPDiscoveryThread
/**
 * Create a new background thread that handles incoming Intents on the given
 * multicast address and port.
 *
 *
 * Do not instantiate this class yourself. Use the [UDPDiscovery] class
 * instead.
 *
 * @param multicastAddress
 * @param port
 * @param listenerUDP
 */
/* package-private */(
    private val multicastAddress: String,
    private val port: Int,
    private val listenerUDP: UDPDiscoveryListener
) : Thread() {
    private var socket: MulticastSocket? = null

    @Volatile
    private var running: Boolean = false

    override fun run() {
        running = true

        listenerUDP.onDiscoveryStarted()

        try {
            socket = createSocket()
            receiveMessage()
        } catch (exception: IOException) {
            if (running) {
                listenerUDP.onDiscoveryError(exception)
            }
        } finally {
            closeSocket()
        }

        listenerUDP.onDiscoveryStopped()
    }

    @Throws(UnknownHostException::class, IOException::class)
    protected fun createSocket(): MulticastSocket {
        val address = InetAddress.getByName(multicastAddress)

        val socket = MulticastSocket(port)
        socket.joinGroup(address)

        return socket
    }

    private fun closeSocket() {
        if (socket != null) {
            socket!!.close()
        }
    }

    fun stopDiscovery() {
        running = false

        closeSocket()
    }

    @Throws(IOException::class)
    protected fun receiveMessage() {
        while (running) {
            val packet = DatagramPacket(
                ByteArray(MAXIMUM_PACKET_BYTES), MAXIMUM_PACKET_BYTES
            )

            try {
                socket?.receive(packet)
                listenerUDP.onIntentDiscovered(processMessage(packet))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 处理消息
     */
    private fun processMessage(packet: DatagramPacket): ReceiveMessageBean? {
        val data = packet.data
        var len = packet.length
        if(len <= 4) return null

        val dataList = data.take(len).toMutableList()
        val type = NumberUtil.byte2Int(dataList.take(4).toByteArray())
        len -= 4//跳过头部的4个byte

        when(type) {
            MessageTypes.HEART_BEAT -> {
                val account = String(dataList.takeLast(len).toByteArray())
//                UDPMember.memberList[packet.address.hostName] = account
                LogUtil.d("processMessage type=HEART_BEAT len=$len," +
                        " address=${packet.address.hostName}, account=$account")
                return ReceiveMessageBean(type = type,
                    address = packet.address,
                    data = account)
            }
            MessageTypes.SYNC_DATA -> {
                val data = ZipUtil.uncompressByte(dataList.takeLast(len).toByteArray())
//                UDPMember.memberList[packet.address.hostName] = account
                LogUtil.d("processMessage type=SYNC_DATA len=$len," +
                        " address=${packet.address.hostName}, account=$data")
                return ReceiveMessageBean(type = type,
                    address = packet.address,
                    data = data)
            }
            MessageTypes.ASK, MessageTypes.RECEIVE -> {
                val msg = String(data.takeLast(len).toByteArray())

                LogUtil.d("processMessage type=$type len=$len, msg=$msg")
                return ReceiveMessageBean(type = type,
                    address = packet.address,
                    data = msg)

            }
        }

        return null
    }

    companion object {
        private val TAG = "ANI/UDPDiscoveryThread"
        private const val MAXIMUM_PACKET_BYTES = 102400
        const val MAX_BYTE_SIZE = 65507
    }
}
