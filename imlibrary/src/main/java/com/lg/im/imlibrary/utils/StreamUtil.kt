package com.lg.im.imlibrary.utils

import java.io.*

/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 流处理工具
 *
 */
internal object StreamUtil {

    /**
     * 把流组合成一个字符串
     */
    fun streamToInt(inStream: InputStream?, len: Int): Int? {
        if(inStream == null) return null

        try {
            val b = ByteArray(len)
            val n = inStream.read(b)
            //先读后写
            if (n != -1) {//读
                return NumberUtil.byte2Int(b)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1
    }

    /**
     * 把流组合成一个字符串
     *
     */
    fun streamToString(inStream: InputStream?): String? {
        if(inStream == null) return null

        try {
            val byteList = ArrayList<Byte>()
            val b = ByteArray(FileUtil.FILE_SLICE_SIZE)

            var n = inStream.read(b)
            //先读后写
            while (n != -1) {//读
                byteList.addAll(b.take(n))
                n = inStream.read(b)
            }
            return String(byteList.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 把流组合成一个字符串
     */
    fun streamToString(inStream: InputStream?, len: Int): String? {
        if(inStream == null) return null

        try {
            val b = ByteArray(len)
            val n = inStream.read(b)
            if (n != -1) {//读
                return String(b)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 把流组合成一个文件
     *
     * @param srcBytes 分割文件目录
     * @param dstFile 目标合并文件绝对路径
     */
    fun streamToFile(inStream: InputStream?, dstFile: String,
                     lis: ((progress: Int, cur: Long, total: Long) -> Unit)? = null) {
        if(inStream == null) return

        val fileOutStream = BufferedOutputStream(FileOutputStream(File(dstFile)))
        try {
            val b = ByteArray(FileUtil.FILE_SLICE_SIZE)
            var curLength = 0L
            var n = inStream.read(b)
            //先读后写
            while (n != -1) {//读
                val time = System.currentTimeMillis()
                fileOutStream.write(b, 0, n)//写文件
                val writeTime = System.currentTimeMillis() - time
                n = inStream.read(b)
                curLength += n
                lis?.invoke(0, curLength, 0)

                val readTime = System.currentTimeMillis() - time - writeTime
                LogUtil.d("count=$n, writeTime=$writeTime, readTime=$readTime")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fileOutStream.flush()
                fileOutStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}