package com.lg.im.imapplication


import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.text.TextUtils
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import ani.bean.receive.ReceiveMessageBean
import ani.exp.TransmitterException
import ani.ext.IMCenter
import ani.internal.OnReceiveMessageListener
import com.lg.im.imlibrary.bus.bean.EBusMessage
import com.lg.im.imlibrary.bus.cate.EBusTypes.NETWORK_CONNECTED
import com.lg.im.imlibrary.bus.cate.EBusTypes.NETWORK_NOT_CONNECTED
import com.lg.im.imlibrary.utils.GlobalContext
import com.lg.im.imlibrary.utils.NetUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import java.io.File
import java.net.URLEncoder
import kotlin.random.Random

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalContext.ctx = application

        input_et.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendChatMessage()
                    return true
                }

                return false
            }
        })
        send_ibt.setOnClickListener {
            sendChatMessage()
        }
        send_file_ibt.setOnClickListener {
            sendChatFile()
        }

        EventBus.getDefault().register(this)

//        val file = File(Environment.getExternalStorageDirectory(), "Download/123.jpg")
//        val fileExit = file.exists()
//        appendChatMessage("fileExit=$fileExit")
//        FileUtil.splitFile(file.path, file.parentFile.path + "/test", 3)
//        FileUtil.mergeToFile(file.parentFile.path + "/test", file.parentFile.path + "/1111.jpg")

//        val file = File(Environment.getExternalStorageDirectory(), "Download/123.jpg")
//        val fileBytes = FileUtil.splitFileToBytes(file.path, 10)
//        FileUtil.mergeBytesToFile(fileBytes, file.parentFile.path + "/1111.jpg")

        IMCenter.getIMCenter().register("abc", object : OnReceiveMessageListener{
            override fun onMessage(msg: ReceiveMessageBean) {
                if (!TextUtils.isEmpty(msg.data)) {
                    appendChatMessageFromSender(msg.address?.hostName, msg.data?.substring(0, 30))
                } else {
                    appendChatMessage("* (!) Received Intent without message")
                }
            }

            override fun onError(err: String) {
                appendChatMessage("* (!) udpDiscover error: $err")
            }
        })

        val color = "#${Integer.toHexString(Random(System.nanoTime()).nextInt(255))}" +
                "${Integer.toHexString(Random(System.nanoTime()).nextInt(150))}" +
                "${Integer.toHexString(Random(System.nanoTime()).nextInt(255))}"
        val ip = "<font color= $color>" + NetUtil.getLocalIpAddress() +"</font>"

        chat_tv2.text = Html.fromHtml(ip)
        for (i in 0..10) {
            IMCenter.getIMCenter().syncDataCenter().putSyncData(
                URLEncoder.encode("key_${NetUtil.getLocalIpAddress()}_$i"),
                System.currentTimeMillis(),
                "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                        "${System.nanoTime()}," +
                    "jflkkjdsalkfjlaksjf")
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        IMCenter.getIMCenter().unRegister()
        EventBus.getDefault().unregister(this)
    }

    fun sendChatMessage() {
        val message = input_et.text.toString()

        if (message.isEmpty()) {
            return  // No message to send
        }

        input_et.setText("")

        doAsync {
            transmitMessage(message.toByteArray())
        }
    }

    fun sendChatFile() {
        doAsync {
//            val file = File(Environment.getExternalStorageDirectory(), "Download/123.jpg")
//            val fileBytes = FileUtil.splitFileToBytesWithSize(file)
////            FileUtil.mergeBytesToFile(fileBytes, file.parentFile.path + "/1111.jpg")
//
//            uiThread {
//                appendChatMessage("send file=${fileBytes.size}")
//            }
//
//            fileBytes.forEach {
//                transmitIntentOnBackgroundThread(it)
//                Thread.sleep(8000)
//            }

            val file = File(Environment.getExternalStorageDirectory(), "Download/123.jpg")
            transmitFile(file.path)
        }
    }

    private fun transmitMessage(data: ByteArray) {
        try {
            IMCenter.getIMCenter().msgTransmitter().transmitUDPMessage(data)
        } catch (e: TransmitterException) {
            e.printStackTrace()
            appendChatMessage("Could not transmitMessage: " + e.message)
        }
    }

    private fun transmitFile(path: String) {
        try {
            IMCenter.getIMCenter().msgTransmitter().transmitFile("aa", path)
        } catch (e: TransmitterException) {
            e.printStackTrace()
            appendChatMessage("Could not transmitFile: " + e.message)
        }

    }

    private fun appendChatMessage(message: String) {
        runOnUiThread {
            chat_tv.text = message + "\n" + chat_tv.text
//            chat_sv.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun appendChatMessageFromSender(sender: String?, message: String?) {
        appendChatMessage("<$sender> $message")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveEventBus(event: EBusMessage<Int>){
        when {
            event.type == NETWORK_NOT_CONNECTED ->
                appendChatMessage("${event.type}, ${event.data}, 网络断开连接")
            event.type == NETWORK_CONNECTED ->
                appendChatMessage("${event.type}, ${event.data}, 网络连接上")
            else -> appendChatMessage("${event.type}, ${event.data}")
        }

    }
}
