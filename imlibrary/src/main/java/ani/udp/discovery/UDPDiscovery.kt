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

import android.content.Intent
import ani.exp.DiscoveryException
import ani.internal.NetworkConfig
import ani.udp.discovery.lis.UDPDiscoveryListener

/**
 * UDPDiscovery class for receiving [Intent]s from the network.
 */
internal class UDPDiscovery
/**
 * Create a new [UDPDiscovery] instance that will listen to the given
 * multicast address and port for incoming [Intent]s.
 *
 * @param multicastAddress The multicast address to listen to, e.g. 225.4.5.6.
 * @param port The port to listen to.
 */
@JvmOverloads constructor(
    private val multicastAddress: String = NetworkConfig.UDP_DEFAULT_MULTICAST_ADDRESS,
    private val port: Int = NetworkConfig.UDP_DEFAULT_PORT
) {

    private var listenerUDP: UDPDiscoveryListener? = null
    private var threadUDP: UDPDiscoveryThread? = null

    /**
     * Set the [UDPDiscoveryListener] instance that will be notified about
     * incoming [Intent]s.
     *
     * @param listenerUDP The [UDPDiscoveryListener] that will be notified about
     * incoming [Intent]s.
     */
    fun setDisoveryListener(listenerUDP: UDPDiscoveryListener) {
        this.listenerUDP = listenerUDP
    }

    /**
     * Enables the [UDPDiscovery] so that it will monitor the network for
     * [Intent]s and notify the given [UDPDiscoveryListener] instance.
     *
     * This is a shortcut for:
     * `
     * discovery.setDiscoveryListener(listenerUDP);
     * discovery.enable();
    ` *
     *
     * @param listenerUDP The [UDPDiscoveryListener] that will be notified about
     * incoming [Intent]s.
     * @throws DiscoveryException if discovery could not be enabled.
     */
    @Throws(DiscoveryException::class)
    fun enable(listenerUDP: UDPDiscoveryListener) {
        setDisoveryListener(listenerUDP)
        enable()
    }

    /**
     * Enables the [UDPDiscovery] so that it will monitor the network for
     * [Intent]s and notify the set [UDPDiscoveryListener] instance.
     *
     * @throws DiscoveryException if discovery could not be enabled.
     * @throws IllegalStateException if no listenerUDP has been set
     * @throws IllegalAccessError if this [UDPDiscovery] is already enabled
     */
    @Throws(DiscoveryException::class)
    fun enable() {
        if (listenerUDP == null) {
            throw IllegalStateException("No listenerUDP set")
        }

        if (threadUDP == null) {
            threadUDP = createDiscoveryThread()
            threadUDP!!.start()
        } else {
            throw IllegalAccessError("UDPDiscovery already started")
        }
    }

    protected fun createDiscoveryThread(): UDPDiscoveryThread {
        return UDPDiscoveryThread(multicastAddress, port, listenerUDP!!)
    }

    /**
     * Disables the [UDPDiscovery].
     *
     * @throws IllegalAccessError if this [UDPDiscovery] is not running
     */
    fun disable() {
        if (threadUDP != null) {
            threadUDP!!.stopDiscovery()
            threadUDP = null
        }
    }
}
