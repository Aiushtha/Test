package com.lxz.kotlin.tools.lang

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * 常用字符串开发工具包
 */

/**
 * 将json字符串按缩进格式输出
 */
fun String.asJsonFromat(): String {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val jsonPar = JsonParser()
    val jsonEl = jsonPar.parse(this)
    return gson.toJson(jsonEl)

}
/**
 * 将非空对象转化成json格式
 * */
fun Any?.asJson():String{
    return Gson().toJson(this)
}
fun Any?.asJsonFromat():String{
    if(this is String) {
        return asJsonFromat();
    }else
    {
        return asJson().asJsonFromat();
    }
}

/**
 * 扩展函数 追加字符串转md5方法 32位
 */
fun String.asMd5In32():String{
    try {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象

        val digest:ByteArray = instance.digest(this.toByteArray())//对字符串加密，返回字节数组
        var sb : StringBuffer = StringBuffer()
        for (b in digest) {
            var i :Int = b.toInt() and 0xff//获取低八位有效值
            var hexString = Integer.toHexString(i)//将整数转化为16进制
            if (hexString.length < 2) {
                hexString = "0" + hexString//如果是一位的话，补0
            }
            sb.append(hexString)
        }
        return sb.toString();
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return this;
}
/**
 * 扩展函数 追加字符串转md5方法 16位
 */
fun String.asMd5in16():String=asMd5In32().substring(8, 24);


