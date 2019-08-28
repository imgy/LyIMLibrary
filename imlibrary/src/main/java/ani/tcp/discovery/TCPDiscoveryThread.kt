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

package ani.tcp.discovery

import ani.bean.receive.ReceiveMessageBean
import ani.cate.MessageTypes
import ani.tcp.discovery.lis.TCPDiscoveryListener
import com.lg.im.imlibrary.utils.FileUtil
import com.lg.im.imlibrary.utils.LogUtil
import com.lg.im.imlibrary.utils.StreamUtil
import java.io.BufferedInputStream
import java.io.File

import java.io.IOException
import java.io.InputStream
import java.net.*

/**
 * Internal class for handling the network connection of the [TCPDiscovery] class
 * on a background thread.
 */
internal class TCPDiscoveryThread
/**
 * Create a new background thread that handles incoming Intents on the given
 * multicast address and port.
 *
 *
 * Do not instantiate this class yourself. Use the [TCPDiscovery] class
 * instead.
 *
 * @param port
 * @param listenerTCP
 */
/* package-private */(
    private val port: Int,
    private val listenerTCP: TCPDiscoveryListener
) : Thread() {
    private var serverSocket: ServerSocket? = null

    @Volatile
    private var running: Boolean = false

    override fun run() {
        running = true

        listenerTCP.onDiscoveryStarted()

        try {
            serverSocket = createSocket()
            receiveMessage()
        } catch (exception: IOException) {
            if (running) {
                listenerTCP.onDiscoveryError(exception)
            }
        } finally {
            closeSocket()
        }

        listenerTCP.onDiscoveryStopped()
    }

    @Throws(IOException::class)
    protected fun createSocket(): ServerSocket {
        val socket = ServerSocket(port)
        return socket
    }

    private fun closeSocket() {
        if (serverSocket != null) {
            serverSocket!!.close()
        }
    }

    fun stopDiscovery() {
        running = false

        closeSocket()
    }

    @Throws(IOException::class)
    protected fun receiveMessage() {
        while (running) {
            try {
                //监听连接 ，如果无连接就会处于阻塞状态，一直在这等着
                val clicksSocket = serverSocket?.accept()
                listenerTCP.onIntentDiscovered(processStream(clicksSocket))
            } catch (e: Exception) {
                e.printStackTrace()
                listenerTCP.onDiscoveryError(e)
            }

        }
    }

    private fun processStream(clicksSocket: Socket?): ReceiveMessageBean? {
        clicksSocket!!.soTimeout = 15000
        val inStream = BufferedInputStream(clicksSocket.getInputStream())//获取输入流

        val type = StreamUtil.streamToInt(inStream, 4)//类型

        when(type) {
            MessageTypes.ASK, MessageTypes.RECEIVE -> {
                val msg = StreamUtil.streamToString(inStream)
                LogUtil.d("receiveMessage type=$type msg=$msg")

                msg?.let {
                    return@let ReceiveMessageBean(type = type, data = msg)
                }
            }
            MessageTypes.FILE -> {
                val msgLen = StreamUtil.streamToInt(inStream, 4)//有内容多少长度
                msgLen?.let {
                    val msg = StreamUtil.streamToString(inStream)
                    LogUtil.d("receiveMessage type=$type msg=$msg")

                    msg?.apply {
                        val time = System.currentTimeMillis()

                        val dstFile =
                            File(FileUtil.getDownloadCacheDirectory(), "${System.nanoTime()}.jpg").path
                        StreamUtil.streamToFile(inStream, dstFile) { progress, cur, total ->
                            listenerTCP.onProgress(progress, cur, total)
                        }

                        val timeMsg = "msg=$msg, $dstFile , size=${dstFile.length}, time=${System.currentTimeMillis() - time}"
                        LogUtil.d("processStream timeMsg=$timeMsg")

                        return ReceiveMessageBean(type = MessageTypes.FILE,
                            data = msg,
                            file = dstFile)
                    }
                }
            }
        }

        inStream.close()
        clicksSocket.close()

        return null
    }
}
