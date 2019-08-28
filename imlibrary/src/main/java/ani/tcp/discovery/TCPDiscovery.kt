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

import android.content.Intent
import ani.exp.DiscoveryException

import ani.internal.NetworkConfig
import ani.tcp.discovery.lis.TCPDiscoveryListener

/**
 * TCPDiscovery class for receiving [Intent]s from the network.
 */
internal class TCPDiscovery
/**
 * Create a new [TCPDiscovery] instance that will listen to the given
 * address and port for incoming [Intent]s.
 *
 * @param port The port to listen to.
 */
@JvmOverloads constructor(private val port: Int = NetworkConfig.TCP_DEFAULT_PORT) {

    private var listenerTCP: TCPDiscoveryListener? = null
    private var threadTCP: TCPDiscoveryThread? = null

    /**
     * Set the [TCPDiscoveryListener] instance that will be notified about
     * incoming [Intent]s.
     *
     * @param listenerTCP The [TCPDiscoveryListener] that will be notified about
     * incoming [Intent]s.
     */
    fun setDisoveryListener(listenerTCP: TCPDiscoveryListener) {
        this.listenerTCP = listenerTCP
    }

    /**
     * Enables the [TCPDiscovery] so that it will monitor the network for
     * [Intent]s and notify the given [TCPDiscoveryListener] instance.
     *
     * This is a shortcut for:
     * `
     * discovery.setDiscoveryListener(listenerTCP);
     * discovery.enable();
    ` *
     *
     * @param listenerTCP The [TCPDiscoveryListener] that will be notified about
     * incoming [Intent]s.
     * @throws DiscoveryException if discovery could not be enabled.
     */
    @Throws(DiscoveryException::class)
    fun enable(listenerTCP: TCPDiscoveryListener) {
        setDisoveryListener(listenerTCP)
        enable()
    }

    /**
     * Enables the [TCPDiscovery] so that it will monitor the network for
     * [Intent]s and notify the set [TCPDiscoveryListener] instance.
     *
     * @throws DiscoveryException if discovery could not be enabled.
     * @throws IllegalStateException if no listenerTCP has been set
     * @throws IllegalAccessError if this [TCPDiscovery] is already enabled
     */
    @Throws(DiscoveryException::class)
    fun enable() {
        if (listenerTCP == null) {
            throw IllegalStateException("No listenerTCP set")
        }

        if (threadTCP == null) {
            threadTCP = createDiscoveryThread()
            threadTCP!!.start()
        } else {
            throw IllegalAccessError("TCPDiscovery already started")
        }
    }

    protected fun createDiscoveryThread(): TCPDiscoveryThread {
        return TCPDiscoveryThread(port, listenerTCP!!)
    }

    /**
     * Disables the [TCPDiscovery].
     *
     * @throws IllegalAccessError if this [TCPDiscovery] is not running
     */
    fun disable() {
        if (threadTCP != null) {
            threadTCP!!.stopDiscovery()
            threadTCP = null
        }
    }
}
