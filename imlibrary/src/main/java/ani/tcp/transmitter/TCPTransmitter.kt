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

package ani.tcp.transmitter

import android.content.Intent
import ani.cate.MessageTypes
import ani.exp.TransmitterException

import java.io.IOException

import ani.tcp.discovery.TCPDiscovery
import ani.internal.NetworkConfig
import ani.internal.OnSendMessageListener
import com.lg.im.imlibrary.utils.FileUtil.FILE_SLICE_SIZE
import com.lg.im.imlibrary.utils.LogUtil
import com.lg.im.imlibrary.utils.NumberUtil
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.*

/**
 * TCPTransmitter class for sending [Intent]s through network.
 */
internal class TCPTransmitter
/**
 * Creates a new [TCPTransmitter] instance that will sent [Intent]s to
 * the given address and port.
 *
 * @param address The destination address,
 * @param port             The destination network port.
 */
@JvmOverloads constructor(
    private val address: String,
    private val port: Int = NetworkConfig.TCP_DEFAULT_PORT,
    private val listener: OnSendMessageListener? = null
) {

    /**
     * Sends an [Intent] through the network to any listening [TCPDiscovery]
     * instance.
     *
     * @param data The data to send.
     * @throws TransmitterException if intent could not be transmitted.
     */
    fun transmit(data: ByteArray) {
        var socket: Socket? = null

        try {
            socket = createSocket()
            val sktAddress = InetSocketAddress(address, port)
            socket.connect(sktAddress, 3000)
            transmit(socket, data)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            throw TransmitterException("Unknown host", e)
        } catch (e: SocketException) {
            e.printStackTrace()
            throw TransmitterException("Can't create DatagramSocket", e)
        } catch (e: IOException) {
            e.printStackTrace()
            throw TransmitterException("IOException during sending intent", e)
        } finally {
            if (socket != null) {
                try {
                    socket.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * Sends an [Intent] through the network to any listening [TCPDiscovery]
     * instance.
     *
     * @param data The data to send.
     * @throws TransmitterException if intent could not be transmitted.
     */
    fun transmit(path: String) {
        var socket: Socket? = null

        try {
            socket = createSocket()
            val sktAddress = InetSocketAddress(address, port)
            socket.connect(sktAddress, 3000)
            transmit(socket, path)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            throw TransmitterException("Unknown host", e)
        } catch (e: SocketException) {
            e.printStackTrace()
            throw TransmitterException("Can't create DatagramSocket", e)
        } catch (e: IOException) {
            e.printStackTrace()
            throw TransmitterException("IOException during sending intent", e)
        } finally {
            if (socket != null) {
                try {
                    socket.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    @Throws(IOException::class)
    protected fun createSocket(): Socket {
        return Socket()
    }

    /**
     * Actual (private) implementation that serializes the [Intent] and sends
     * it as [DatagramPacket]. Used to separate the implementation from the
     * error handling code.
     */
    @Throws(IOException::class)
    private fun transmit(socket: Socket, data: ByteArray) {
        val outStream = BufferedOutputStream(socket.getOutputStream())
        outStream.write(data)
        outStream.flush()
        outStream.close()
        listener?.onSuccess()
    }

    /**
     * Actual (private) implementation that serializes the [Intent] and sends
     * it as [DatagramPacket]. Used to separate the implementation from the
     * error handling code.
     */
    @Throws(IOException::class)
    private fun transmit(socket: Socket, path: String) {
        try {
            val outStream = BufferedOutputStream(socket.getOutputStream())
            //申明文件切割后的文件磁盘
            val srcFile = File(path)
            val fileLength = srcFile.length()
            var curLength = 0L//当前传输的数据
            val fielInStream = FileInputStream(srcFile)

            outStream.write(NumberUtil.int2Byte(MessageTypes.FILE))
            outStream.write(NumberUtil.int2Byte(fileLength.toInt()))

            //申明具体每一文件的字节数组
            val b = ByteArray(FILE_SLICE_SIZE)
            var n = fielInStream.read(b)
            while (n != -1) {
                //从指定每一份文件的范围，写入不同的文件
                val time = System.currentTimeMillis()
                outStream.write(b, 0, n)
                curLength += n
                listener?.onProgress(((curLength*100)/fileLength).toInt(), curLength, fileLength)
                LogUtil.d("sent n=$n, time=${System.currentTimeMillis() - time}")
                n = fielInStream.read(b)
            }
            listener?.onSuccess()
            //关闭输入流
            fielInStream.close()
            outStream.flush()
            outStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}