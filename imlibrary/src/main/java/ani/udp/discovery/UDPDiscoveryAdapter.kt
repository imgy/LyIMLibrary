package ani.udp.discovery

import android.content.Intent
import ani.bean.receive.ReceiveMessageBean
import ani.udp.discovery.lis.UDPDiscoveryListener

import java.net.InetAddress

/**
 * This adapter class provides empty implementations of the methods from
 * [UDPDiscoveryListener].
 *
 * Any custom listener that cares only about a subset of the methods of this listener
 * can simply subclass this adapter class instead of implementing the interface
 * directly.
 */
internal abstract class UDPDiscoveryAdapter : UDPDiscoveryListener {
    /**
     * Called when the [UDPDiscovery] has successfully received an [Intent].
     *
     * @param address The IP address of the sender of the [Intent].
     * @param intent The received [Intent].
     */
    abstract override fun onIntentDiscovered(message: ReceiveMessageBean?)

    /**
     * The [UDPDiscovery] has been started and is now waiting for incoming
     * [Intent]s.
     *
     * Empty default implementation.
     */
    override fun onDiscoveryStarted() {
        // Empty default implementation
    }

    /**
     * The [UDPDiscovery] has been stopped.
     *
     * Empty default implementation.
     */
    override fun onDiscoveryStopped() {
        // Empty default implementation
    }

    /**
     * An unrecoverable error occured. The [UDPDiscovery] is going to be stopped.
     *
     * Empty default implementation.
     *
     * @param exception Actual exception that occured in the background thread
     */
    override fun onDiscoveryError(exception: Exception) {
        // Empty default implementation
    }
}
