package com.lg.im.imapplication


import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import ani.bean.receive.ReceiveMessageBean
//import ani.tcp.discovery.TCPDiscovery
//import ani.exp.DiscoveryException
//import ani.tcp.discovery.lis.TCPDiscoveryListener
//import ani.tcp.transmitter.TCPTransmitter
//import ani.exp.TransmitterException
//import ani.udp.discovery.UDPDiscovery
//import ani.udp.discovery.lis.UDPDiscoveryListener
//import ani.udp.transmitter.UDPTransmitter
//import com.lg.im.imlibrary.bus.bean.EBusMessage
//import com.lg.im.imlibrary.utils.GlobalContext
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import java.io.File

class MainActivity : AppCompatActivity() {
    private val EXTRA_MESSAGE = "message"

//    private var udpDiscover: UDPDiscovery? = null//UDP连接
//    private var udpTranster: UDPTransmitter? = null//UDP发送
//
//    private var tcpDiscover: TCPDiscovery? = null//TCP连接
//    private var tcpTranster: TCPTransmitter? = null//TCP发送
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        GlobalContext.ctx = applicationContext
//
//        input_et.setOnEditorActionListener(object : OnEditorActionListener {
//            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
//                if (actionId == EditorInfo.IME_ACTION_SEND) {
//                    sendChatMessage()
//                    return true
//                }
//
//                return false
//            }
//        })
//        send_ibt.setOnClickListener {
//            sendChatMessage()
//        }
//        send_file_ibt.setOnClickListener {
//            sendChatFile()
//        }
//
//        EventBus.getDefault().register(this)
//
////        val file = File(Environment.getExternalStorageDirectory(), "Download/123.jpg")
////        val fileExit = file.exists()
////        appendChatMessage("fileExit=$fileExit")
////        FileUtil.splitFile(file.path, file.parentFile.path + "/test", 3)
////        FileUtil.mergeToFile(file.parentFile.path + "/test", file.parentFile.path + "/1111.jpg")
//
////        val file = File(Environment.getExternalStorageDirectory(), "Download/123.jpg")
////        val fileBytes = FileUtil.splitFileToBytes(file.path, 10)
////        FileUtil.mergeBytesToFile(fileBytes, file.parentFile.path + "/1111.jpg")
//
//        initUDP()
//        initTCP()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//
//        EventBus.getDefault().unregister(this)
//    }
//
//    private fun initUDP() {
//        udpDiscover = UDPDiscovery()
//        udpDiscover?.setDisoveryListener(object : UDPDiscoveryListener {
//
//            override fun onDiscoveryError(exception: Exception) {
//                appendChatMessage("* (!) udpDiscover error: " + exception.message)
//            }
//
//            override fun onIntentDiscovered(message: ReceiveMessageBean?) {
//                if (!TextUtils.isEmpty(message?.data)) {
//                    appendChatMessageFromSender(message!!.address?.hostName, message.data)
//                } else {
//                    appendChatMessage("* (!) Received Intent without message")
//                }
//
//
//            }
//
//            override fun onDiscoveryStarted() {
//                appendChatMessage("* (>) udpDiscover started")
//            }
//
//            override fun onDiscoveryStopped() {
//                appendChatMessage("* (<) udpDiscover stopped")
//            }
//        })
//        udpTranster = UDPTransmitter()
//    }
//
//    private fun initTCP() {
//        tcpDiscover = TCPDiscovery()
//        tcpDiscover?.setDisoveryListener(object : TCPDiscoveryListener {
//            override fun onProgress(progress: Int, cur: Long, total: Long) {
//
//            }
//
//            override fun onDiscoveryError(exception: Exception) {
//                appendChatMessage("* (!) tcpDiscover error: " + exception.message)
//            }
//
//            override fun onIntentDiscovered(message: ReceiveMessageBean?) {
//                if (!TextUtils.isEmpty(message?.data)) {
//                    appendChatMessageFromSender(message!!.address?.hostAddress, message.data)
//                } else {
//                    appendChatMessage("* (!) Received Intent without message")
//                }
//
//
//            }
//
//            override fun onDiscoveryStarted() {
//                appendChatMessage("* (>) tcpDiscover started")
//            }
//
//            override fun onDiscoveryStopped() {
//                appendChatMessage("* (<) tcpDiscover stopped")
//            }
//        })
//        tcpTranster = TCPTransmitter("172.30.230.37")
//    }
//
//    public override fun onResume() {
//        super.onResume()
//
//        try {
//            udpDiscover?.enable()
//        } catch (e: DiscoveryException) {
//            e.printStackTrace()
//            appendChatMessage("* (!) Could not start udpDiscover: " + e.message)
//        }
//
//        try {
//            tcpDiscover?.enable()
//        } catch (e: DiscoveryException) {
//            e.printStackTrace()
//            appendChatMessage("* (!) Could not start tcpDiscover: " + e.message)
//        }
//
//    }
//
//    public override fun onPause() {
//        super.onPause()
//        udpDiscover?.disable()
//        tcpDiscover?.disable()
//    }
//
//    fun sendChatMessage() {
//        val message = input_et.text.toString()
//
//        if (message.isEmpty()) {
//            return  // No message to send
//        }
//
//        input_et.setText("")
//
//        doAsync {
//            transmitMessage(message.toByteArray())
//        }
//    }
//
//    fun sendChatFile() {
//        doAsync {
////            val file = File(Environment.getExternalStorageDirectory(), "Download/123.jpg")
////            val fileBytes = FileUtil.splitFileToBytesWithSize(file)
//////            FileUtil.mergeBytesToFile(fileBytes, file.parentFile.path + "/1111.jpg")
////
////            uiThread {
////                appendChatMessage("send file=${fileBytes.size}")
////            }
////
////            fileBytes.forEach {
////                transmitIntentOnBackgroundThread(it)
////                Thread.sleep(8000)
////            }
//
//            val file = File(Environment.getExternalStorageDirectory(), "Download/123.jpg")
//            transmitFile(file.path)
//        }
//    }
//
//    private fun transmitMessage(data: ByteArray) {
//        try {
//            udpTranster?.transmit(data)
//        } catch (e: TransmitterException) {
//            e.printStackTrace()
//            appendChatMessage("Could not transmitMessage: " + e.message)
//        }
//    }
//
//    private fun transmitFile(path: String) {
//        try {
//            tcpTranster?.transmit(path)
//        } catch (e: TransmitterException) {
//            e.printStackTrace()
//            appendChatMessage("Could not transmitFile: " + e.message)
//        }
//
//    }
//
//    private fun appendChatMessage(message: String) {
//        runOnUiThread {
//            chat_tv.append(message + "\n")
//            chat_sv.fullScroll(ScrollView.FOCUS_DOWN)
//        }
//    }
//
//    private fun appendChatMessageFromSender(sender: String?, message: String?) {
//        appendChatMessage("<$sender> $message")
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun receiveEventBus(event: EBusMessage<String>){
//        appendChatMessage("${event.type}, ${event.data}")
//    }
}
