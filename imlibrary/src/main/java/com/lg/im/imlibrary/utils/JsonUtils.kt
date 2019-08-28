package com.zqkh.commlibrary.utilslibrary.utils

import android.text.TextUtils
import com.google.gson.Gson
import org.json.JSONArray
import java.lang.reflect.Type


/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * json序列化/反序列化工具
 *
 */
object JsonUtils {

//    fun <T> toJsonString(t: T): String {
//        val mapper = ObjectMapper()
//        val json = mapper.writeValueAsString(t)
//        return json
//    }

//    inline fun <reified T : Any> cloneObject(t: T): T {
//        val str = toJsonString(t)
//        return fromJson(str)
//    }

    inline fun <reified T : Any> cloneObject(t: T?): T? {
        if (t == null) {
            return null
        }

        val str = toJsonString(t)

        return fromJson(str)
    }

    fun <T> toJsonString(t: T?): String? {
        if (t == null) {
            return null
        }

        return Gson().toJson(t)
    }

    inline fun <reified T : Any> fromJson(json: String?): T? {
        if (TextUtils.isEmpty(json)) {
            return null
        }
        return Gson().fromJson(json, T::class.java)
    }

    inline fun <T> fromJson(json: String?, cls: Class<T>): T? {
        if (TextUtils.isEmpty(json)) {
            return null
        }
        return Gson().fromJson(json, cls)
    }

    inline fun <T> fromJson(json: String?, type: Type): T? {
        if (TextUtils.isEmpty(json)) {
            return null
        }
        return Gson().fromJson(json, type)
    }

//    inline fun <reified T: Any> fromJson(str: String): T {
//        val mapper = ObjectMapper()
//        val t = mapper.readValue(str, T::class.java)
//        return t
//    }
//
//    fun <T> toListObject(str: String, cls: Class<T>): List<T>? {
//        var list: List<T>? = null
//        val mapper = ObjectMapper()
//        val t = TypeFactory.defaultInstance()
//        try {
//            list = mapper.readValue<List<T>>(str, t.constructCollectionType(ArrayList::class.java, cls))
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        return list
//    }

//    inline fun <reified T> Gson.fromJson(json: String)
//            = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

    fun <T> toListObject(str: String?, type: Type): List<T>? {
//        val type = object : TypeToken<List<T>>() {}.type
        if (str.isNullOrEmpty()) {
            return null
        }

        return Gson().fromJson(str, type)
    }
    fun <T> listToJsonArray(lst: List<T>?): JSONArray? {
        if (lst != null) {
            val ja = JSONArray()
            lst.forEach {
                ja.put(it)
            }

            return ja
        }

        return null
    }

    fun <K, V> toMapObject(str: String?, type: Type): Map<K, V>? {
//        val type = object : TypeToken<Map<K, V>>() {}.type
        if (str.isNullOrEmpty()) {
            return null
        }

        return Gson().fromJson(str, type)
    }
}