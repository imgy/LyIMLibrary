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

package ani.tcp.discovery.lis

import android.content.Intent
import ani.bean.receive.ReceiveMessageBean

import java.net.InetAddress

/**
 * A [TCPDiscoveryListener] receives notifications from a [TCPDiscovery].
 * Notifications indicate lifecycle related events as well as successfully received
 * [Intent]s.
 */
internal interface TCPDiscoveryListener {
    /**
     * The [TCPDiscovery] has been started and is now waiting for incoming
     * [Intent]s.
     */
    fun onDiscoveryStarted()

    /**
     * The [TCPDiscovery] has been stopped.
     */
    fun onDiscoveryStopped()

    /**
     * An unrecoverable error occured. The [TCPDiscovery] is going to be stopped.
     *
     * @param exception Actual exception that occured in the background thread
     */
    fun onDiscoveryError(exception: Exception)

    /**
     * Called when the [TCPDiscovery] has successfully received an [Intent].
     *
     * @param address The IP address of the sender of the [Intent].
     * @param intent The received [Intent].
     */
    fun onIntentDiscovered(message: ReceiveMessageBean?)

    /**
     * 进度
     */
    fun onProgress(progress: Int, cur: Long, total: Long)
}
