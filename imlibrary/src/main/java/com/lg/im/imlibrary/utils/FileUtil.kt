package com.lg.im.imlibrary.utils

import android.os.Environment
import java.io.*

/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 文件类
 *
 */
internal object FileUtil {

    const val FILE_SLICE_SIZE = 10 * 1024

    fun getDownloadCacheDirectory(): File {
        return Environment.getDownloadCacheDirectory()
    }

    fun getExternalStorageDirectory(): File {
        return Environment.getExternalStorageDirectory()
    }

    /**
     * 文件分割成byte
     *
     * @param srcFilePath 源文件Path
     * @param count       分割个数
     */
    fun splitFileToBytesWithSize(srcFile: File): MutableList<ByteArray> {
        val count = srcFile.length()/FILE_SLICE_SIZE + if(srcFile.length()%FILE_SLICE_SIZE > 0) 1 else 0
        return splitFileToBytes(srcFile.path, count.toInt())
    }

    /**
     * 文件分割成byte
     *
     * @param srcFilePath 源文件Path
     * @param count       分割个数
     */
    fun splitFileToBytesWithSize(srcFile: File, size: Int): MutableList<ByteArray> {
        val count = srcFile.length()/size + if(srcFile.length()%size > 0) 1 else 0
        return splitFileToBytes(srcFile.path, count.toInt())
    }

    /**
     * 文件分割成byte
     *
     * @param srcFilePath 源文件Path
     * @param count       分割个数
     */
    fun splitFileToBytes(srcFilePath: String, count: Int): MutableList<ByteArray> {
        val dstList = ArrayList<ByteArray>()
        var raf: RandomAccessFile? = null
        try {
            //获取目标文件 预分配文件所占的空间 在磁盘中创建一个指定大小的文件   r 是只读
            raf = RandomAccessFile(File(srcFilePath), "r")
            val length = raf.length()//文件的总长度
            val maxSize = length / count//文件切片后的长度
            var offSet = 0L//初始化偏移量
            for (i in 0 until count - 1) { //最后一片单独处理
                val begin = offSet
                val end = (i + 1) * maxSize
                val tempList = ArrayList<Byte>()
                offSet = getWriteBytes(srcFilePath, tempList, i, begin, end)
                dstList.add(tempList.toByteArray())
            }
            if (length - offSet > 0) {
                val tempList = ArrayList<Byte>()
                getWriteBytes(srcFilePath, tempList, count - 1, offSet, length)
                dstList.add(tempList.toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                raf?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return dstList
        }
    }

    /**
     * 文件分割方法
     *
     * @param srcFilePath 源文件Path
     * @param dstFilePath 分割文件的目标目录
     * @param count       分割个数
     */
    fun splitFile(srcFilePath: String, dstFilePath: String, count: Int) {
        var raf: RandomAccessFile? = null
        try {
            //获取目标文件 预分配文件所占的空间 在磁盘中创建一个指定大小的文件   r 是只读
            raf = RandomAccessFile(File(srcFilePath), "r")
            val length = raf.length()//文件的总长度
            val maxSize = length / count//文件切片后的长度
            var offSet = 0L//初始化偏移量
            for (i in 0 until count - 1) { //最后一片单独处理
                val begin = offSet
                val end = (i + 1) * maxSize
                //                offSet = writeFile(file, begin, end, i);
                offSet = getWrite(srcFilePath, dstFilePath, i, begin, end)
            }
            if (length - offSet > 0) {
                getWrite(srcFilePath, dstFilePath, count - 1, offSet, length)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                raf?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 指定文件每一份的边界，写入不同byte中
     *
     * @param srcFilePath 源文件
     * @param dstList     目标数组
     * @param index       源文件的顺序标识
     * @param begin       开始指针的位置
     * @param end         结束指针的位置
     * @return long
     */
    private fun getWriteBytes(
        srcFilePath: String, dstList: MutableList<Byte>, index: Int, begin: Long,
        end: Long
    ): Long {
        var endPointer = 0L
        try {
            //申明文件切割后的文件磁盘
            val inFile = RandomAccessFile(File(srcFilePath), "r")

            //申明具体每一文件的字节数组
            val b = ByteArray(FILE_SLICE_SIZE)
            //从指定位置读取文件字节流
            inFile.seek(begin)
            //判断文件流读取的边界
            while (inFile.filePointer <= end) {
                //从指定每一份文件的范围，写入不同的文件
                val n = inFile.read(b)
                if (n != -1) dstList.addAll(b.take(n)) else break
            }
            //定义当前读取文件的指针
            endPointer = inFile.filePointer
            //关闭输入流
            inFile.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return endPointer
    }

    /**
     * 指定文件每一份的边界，写入不同文件中
     *
     * @param srcFilePath 源文件
     * @param dstFilePath 目标目录
     * @param index       源文件的顺序标识
     * @param begin       开始指针的位置
     * @param end         结束指针的位置
     * @return long
     */
    private fun getWrite(
        srcFilePath: String, dstFilePath: String, index: Int, begin: Long,
        end: Long
    ): Long {
        val srcFile = File(srcFilePath)
        var endPointer = 0L
        try {
            //申明文件切割后的文件磁盘
            val inFile = RandomAccessFile(File(srcFilePath), "r")

            //目录不存在就创建
            val dstDir = File(dstFilePath)
            if (!dstDir.exists()) dstDir.mkdir()

            //定义一个可读，可写的文件并且后缀名为.tmp的二进制文件
            val tempFile = File(
                dstDir, srcFile.name
                    .split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        + "_" + index + ".tmp"
            )
            if(tempFile.exists()) tempFile.delete()
            val outFile = RandomAccessFile(tempFile, "rw")

            //申明具体每一文件的字节数组
            val b = ByteArray(FILE_SLICE_SIZE)
            //从指定位置读取文件字节流
            inFile.seek(begin)
            //判断文件流读取的边界
            while (inFile.filePointer <= end) {
                //从指定每一份文件的范围，写入不同的文件
                val n = inFile.read(b)
                if (n != -1) outFile.write(b, 0, n) else break
            }
            //定义当前读取文件的指针
            endPointer = inFile.filePointer
            //关闭输入流
            inFile.close()
            //关闭输出流
            outFile.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return endPointer
    }

    /**
     * 把byte组合成一个文件
     *
     * @param srcBytes 分割文件目录
     * @param dstFile 目标合并文件绝对路径
     */
    fun mergeBytesToFile(srcBytes: MutableList<ByteArray>, dstFile: String) {
        if (srcBytes.isNotEmpty()) {
            var raf: RandomAccessFile? = null
            try {
                //申明随机读取文件RandomAccessFile
                val dstFile = File(dstFile)
                if (dstFile.exists()) dstFile.delete()

                raf = RandomAccessFile(dstFile, "rw")
                //开始合并文件，对应切片的二进制文件

                for (value in srcBytes) {
                    if(value.isNotEmpty()) raf.write(value)//写文件
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    raf?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * @param srcFile 分割文件目录
     * @param dstFile 目标合并文件绝对路径
     */
    fun mergeToFile(srcFile: String, dstFile: String) {
        val file = File(srcFile)
        if (file.exists() && file.listFiles()!!.isNotEmpty()) {
            var raf: RandomAccessFile? = null
            try {
                //申明随机读取文件RandomAccessFile
                val dstFile = File(dstFile)
                if (dstFile.exists()) dstFile.delete()

                raf = RandomAccessFile(dstFile, "rw")
                //开始合并文件，对应切片的二进制文件
                val files = file.listFiles()

                for (value in files!!) {
                    //读取切片文件
                    val reader = RandomAccessFile(value, "r")
                    val b = ByteArray(FILE_SLICE_SIZE)
                    var n = reader.read(b)
                    //先读后写
                    while (n != -1) {//读
                        raf.write(b, 0, n)//写
                        n = reader.read(b)
                    }
                    reader.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    raf?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }
}
