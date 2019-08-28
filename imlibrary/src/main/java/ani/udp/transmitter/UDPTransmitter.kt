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

package ani.udp.transmitter

import android.content.Intent
import ani.exp.TransmitterException
import ani.internal.NetworkConfig

import ani.udp.discovery.UDPDiscovery
import ani.udp.discovery.UDPDiscoveryThread.Companion.MAX_BYTE_SIZE

import java.io.IOException
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.net.SocketException
import java.net.UnknownHostException

/**
 * UDPTransmitter class for sending [Intent]s through network.
 */
internal class UDPTransmitter
/**
 * Creates a new [UDPTransmitter] instance that will sent [Intent]s to
 * the given multicast address and port.
 *
 * @param multicastAddress The destination multicast address, e.g. 225.4.5.6.
 * @param port             The destination network port.
 */
@JvmOverloads constructor(
    private val multicastAddress: String = NetworkConfig.UDP_DEFAULT_MULTICAST_ADDRESS,
    private val port: Int = NetworkConfig.UDP_DEFAULT_PORT
) {
    /**
     * Sends an [Intent] through the network to any listening [UDPDiscovery]
     * instance.
     *
     * @param data The data to send.
     * @throws TransmitterException if intent could not be transmitted.
     */
    @Throws(TransmitterException::class)
    fun transmit(data: ByteArray) {
        var socket: MulticastSocket? = null

        try {
            socket = createSocket()
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
            socket?.close()
        }
    }

    @Throws(IOException::class)
    protected fun createSocket(): MulticastSocket {
        return MulticastSocket()
    }

    /**
     * Actual (private) implementation that serializes the [Intent] and sends
     * it as [DatagramPacket]. Used to separate the implementation from the
     * error handling code.
     */
    @Throws(IOException::class)
    private fun transmit(socket: MulticastSocket, data: ByteArray) {

        if(data.size > MAX_BYTE_SIZE) throw TransmitterException("data is bigger than 65507", IOException())

        val packet = DatagramPacket(
            data,
            data.size,
            InetAddress.getByName(multicastAddress),
            port
        )

        socket.send(packet)
    }
}
