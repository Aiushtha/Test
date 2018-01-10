package com.lxz.kotlin.tools.http

import android.app.Application
import com.google.gson.Gson
import okhttp3.Response
import org.lxz.utils.ashttp.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.HashMap

/**
 * Created by linxingzhu on 2018/1/9.
 */
open class DefaultInterceptor @JvmOverloads constructor(internal var app: Application, httpCodeRawFileRes: Int = R.raw.exception_filter) : AsHttpInterceptor {

    private var errorTable: ErrorCodeTable? = null
    var mistake: MutableMap<Class<*>, String> = HashMap()

    inner class ErrorCodeTable {
        var htppCodeTable: Map<String, String>? = null
        var throwsTable: Map<String, String>? = null
        var defaultHttpCodeMessage: String? = null
        var defaultThrowsMessage: String? = null
    }

    init {
        val `in` = app.applicationContext.resources.openRawResource(httpCodeRawFileRes)
        try {
            val errorCodeMsg = read(`in`, "utf-8")
            errorTable = Gson().fromJson(errorCodeMsg, ErrorCodeTable::class.java)
            val it = errorTable!!.throwsTable!!.entries.iterator()
            while (it.hasNext()) {
                val keyvalue = it.next()
                val claPath = keyvalue.key
                val msg = keyvalue.value
                val cla = Class.forName(claPath)
                mistake.put(cla, msg)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException(e)
        }

    }


    override fun httpCodeMessage(code: Int): String {
        val msg = errorTable!!.htppCodeTable!![code.toString()]
        return msg ?: errorTable!!.defaultHttpCodeMessage!!
    }


    @Throws(Exception::class)
    override fun interceptor(response: Response, json: String): String {
        return json
    }

    override fun handlerThrowable(e: Throwable): HttpException {
        if (e is HttpException) return e
        val errorMsg = mistake[e.javaClass]
        return HttpException(0, errorMsg ?: errorTable!!.defaultThrowsMessage!!)
    }

    companion object {


        @Throws(IOException::class)
        fun read(`in`: InputStream, encode: String?): String {

            val sb = StringBuffer()
            var br: BufferedReader? = null
            if (encode == null) {
                br = BufferedReader(InputStreamReader(`in`))
            } else {
                br = BufferedReader(InputStreamReader(`in`, encode))
            }
            var line: String?=null
            fun readline():String?{
                line=br!!.readLine()
                return line;
            }
            while((readline())!=null)
            {
                sb.append(line)
                sb.append("\n")
            }
            br.close()
            return sb.toString().trim { it <= ' ' }
        }
    }


}