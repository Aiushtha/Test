package com.lxz.kotlin.tools.http

import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import okio.Buffer
import org.lxz.utils.log.L
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.lang.reflect.Type
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * Created by linxingzhu on 2018/1/9.
 */
class AsHttpFactory internal constructor() {

    var requestHashMap: HashMap<String, HttpLog> = HashMap<String, HttpLog>()
    var url = "http://127.0.0.1"
    var jPath: String? = null
    var okHttpClient: OkHttpClient? = null
    lateinit var retrofit: Retrofit
    var connectTimeout: Int = 0
    var readTimeout: Int = 0
    var showLog = true
    val tag = "LogHttpInfo"
    var netCacheInterceptor: Interceptor? = null
    var asHttpInterceptor: AsHttpInterceptor? = null
    var interceptorList: MutableList<Interceptor>? = null
    var netInterceptorList: MutableList<Interceptor>? = null
    var heads: MutableMap<String, String>? = null
    var parameters: MutableMap<String, String>? = null
    var logListen: LogListen? = null
    var clientBuilder: ClientBuilder? = null


    inner class HttpLog(var number: String) {
        var response: Response? = null
        var request: Request? = null
        var code: Int = 0
        var throwable: Throwable? = null
        var data: String?=null


        private lateinit var message: String



        fun handlerLog() {
            if (showLog) {
                try {
                    message = getMessage()
                    L.json(message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            logListen!!.log(this)

        }


        fun getMessage(): String {
            var print: LinkedHashMap<String, Any> = LinkedHashMap()

            try {
                var mediaType: MediaType? = response?.body()?.contentType()
                val requestBody = if (response != null) response?.request()?.body() else request?.body()
                val buffer = Buffer()
                requestBody?.writeTo(buffer)
                val charset = Charset.forName("UTF-8")
                val paramsStr = buffer.readString(charset)
                var decParamsStr: String? = null
                try {
                    decParamsStr = URLDecoder.decode(paramsStr, "UTF-8")
                } catch (e: Exception) {
                }
                var postParams: MutableMap<String, String>? = null
//                try {
//                    val arr = decParamsStr!!.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//                    postParams = HashMap()
//                    for (str in arr) {
//                        val keyValue = str.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//                        val key = keyValue[0]
//                        val value = keyValue[1]
//                        postParams.put(key, value)
//                    }
//                } catch (e: Exception) {
//                }

                val url:String = if (response != null) response?.request()?.url().toString() else request?.url().toString()
                print.put("id", number)
                print.put("url", url)
                print.put("method", if (response != null) response?.request()?.method()?:"" else request?.method()?:"")
                print.put("mediaType", mediaType?:"null")
                print.put("head", if (response != null) response?.request()?.headers()?:"" else request?.headers()?:"")
                if (url?.indexOf("?") != -1) {
                    val getParams = HashMap<String, String>()
                    try {
                        val urlPamArr = url?.split("\\?".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1].split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        for (str in urlPamArr) {
                            val keyValue = str.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            val key = keyValue[0]
                            val value = URLDecoder.decode(keyValue[1], "UTF-8")
                            getParams.put(key, value)
                        }
                        print.put("query", getParams)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                print.put("params", paramsStr?:"")
                print.put("paramsDecode", postParams?:"")

                if (request?.body() is MultipartBody) {
                    print.put("isMultipartBody", true)
                    val multipartBody = request!!.body() as MultipartBody?
                    print.put("multipartBodyboundary", multipartBody!!.boundary())
                    print.put("multipartContentLength", multipartBody.contentLength())
                    print.put("multipartContentLength", multipartBody.type())
                    print.put("multipartBody", multipartBody.parts())
                    val listInfo = ArrayList<Map<*, *>>()
                    for (part in multipartBody.parts()) {
                        val map = HashMap<Any, Any>()
                        map.put("contentLength", part.body().contentLength())
                        map.put("contentType", part!!.body()!!.contentType()!!)
                        listInfo.add(map)
                    }
                    print.put("multiparContentInfo", listInfo)

                }
                if (request?.body() is FormBody) {
                    print.put("isFromBody", true)
                    val oldFormBody = request!!.body() as FormBody?

                    val fromInfos = ArrayList<Map<*, *>>()
                    for (i in 0 until oldFormBody!!.size()) {
                        val fromBodyMap = HashMap<String, Any>()
                        fromBodyMap.put("contentLength", oldFormBody.contentLength())
                        fromBodyMap.put("contentType", oldFormBody!!.contentType()!!)
                        fromBodyMap.put("value", oldFormBody.value(i))
                        fromInfos.add(fromBodyMap)
                    }
                    print.put("frombody", fromInfos)
                }


                print.put("code", code)
                if(data!=null)print.put("data", data!!?:"")
                    try {
                        val dataDecode = ascii2native(data)
                        print.put("dataDecode", dataDecode!!)
                        print.put("dataJsonDecode", Gson().fromJson(dataDecode, Map::class.java))
                    } catch (e: Exception) {
                    }

                print.put("error", throwable!!.javaClass.simpleName + " " + throwable!!.message)


            } catch (e: IOException) {

            }
//            message = Gson().toJson(print);
            return  Gson().toJson(print)


        }
    }

    class Build {
        val asHttpFactory = AsHttpFactory()

        /**
         * 设置主链接的url
         */
        fun setUrl(url: String): Build {
            asHttpFactory.url = url
            return this
        }

        /**
         * jsonPath解析
         */
        fun setJsonPath(jsonPath: String?): Build {
            asHttpFactory.jPath = jsonPath
            return this
        }

        /**
         * 客户端逻辑
         */
        fun setClientLogic(asHttpInterceptor: AsHttpInterceptor): Build {
            asHttpFactory.asHttpInterceptor = asHttpInterceptor
            return this
        }

        /**
         * 客户端逻辑
         */
        fun setShowLog(showLog: Boolean): Build {
            asHttpFactory.showLog = showLog
            return this
        }

        /**
         * 客户端逻辑
         */
        fun setReadTimeout(seconds: Int): Build {
            asHttpFactory.readTimeout = seconds
            return this
        }

        /**
         * 连接超时
         */
        fun setConnectTimeout(seconds: Int): Build {
            asHttpFactory.connectTimeout = seconds
            return this
        }

        /**
         * 添加拦截器
         */
        fun addInterceptor(interceptor: Interceptor): Build {
            if (asHttpFactory.interceptorList == null)
                asHttpFactory.interceptorList = ArrayList()
            asHttpFactory.interceptorList!!.add(interceptor)
            return this
        }

        /**
         * 添加拦截器
         */
        fun addNetInterceptor(interceptor: Interceptor): Build {
            if (asHttpFactory.netInterceptorList == null)
                asHttpFactory.netInterceptorList = ArrayList()
            asHttpFactory.netInterceptorList!!.add(interceptor)
            return this
        }

        fun addParameter(key: String, value: String): Build {
            if (asHttpFactory.parameters == null) asHttpFactory.parameters = HashMap()
            asHttpFactory.parameters!!.put(key, value)
            return this
        }

        fun addHeader(key: String, value: String): Build {
            if (asHttpFactory.heads == null) asHttpFactory.heads = HashMap()
            asHttpFactory.heads!!.put(key, value)
            return this
        }


        fun setHttpLog(logListen: LogListen): Build {
            asHttpFactory.logListen = logListen
            return this
        }
        fun setHttpLog(listen: (httpLog: HttpLog) -> Unit ):Build{
              asHttpFactory.logListen = object: LogListen {
                  override fun log(httpLog: HttpLog) {
                      listen.invoke(httpLog)
                  }
              };
            return this;
        };


        fun setClientBuild(clientBuilder: ClientBuilder): Build {
            asHttpFactory.clientBuilder = clientBuilder
            return this
        }
        fun setClientBuild(clientBuilder: (OkHttpClient.Builder)->Unit): Build {
            asHttpFactory.clientBuilder = object:ClientBuilder{
                override fun build(build: OkHttpClient.Builder) {
                    clientBuilder.invoke(build)
                }

            }
            return this
        }

        fun build(): AsHttpFactory {
            return asHttpFactory.build()
        }


    }


    fun build(): AsHttpFactory {
        netCacheInterceptor = createInterceptor()
        okHttpClient = createOkHttpClient()
        retrofit = createRetrofitInstance()
        return this
    }


    fun createRetrofitInstance(): Retrofit {
        retrofit = Retrofit.Builder()
                .client(okHttpClient)

                .addConverterFactory(ResponseConvertFactory(Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build()
        return retrofit!!;
    }

    fun createOkHttpClient(): OkHttpClient {
        val build = OkHttpClient.Builder()


        if (interceptorList != null && !interceptorList!!.isEmpty()) {
            for (inter in interceptorList!!) {
                build.addInterceptor(inter)
            }
        }
        if (netInterceptorList != null && !netInterceptorList!!.isEmpty()) {
            for (inter in netInterceptorList!!) {
                build.addNetworkInterceptor(inter)
            }
        }
        if (clientBuilder != null) {
            clientBuilder!!.build(build)
        }
        build.addInterceptor(netCacheInterceptor)
                .connectTimeout(connectTimeout.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout.toLong(), TimeUnit.MILLISECONDS)


        return build.build()
    }





    fun createInterceptor(): Interceptor {


        netCacheInterceptor = Interceptor { chain ->
            var response: Response?=null
            var httpLog: HttpLog? = null;
            try {
                val request = chain.request()


                val httpUrl: HttpUrl
                val httpUrlBuild = request.url().newBuilder()
                if (parameters != null) {
                    val it = parameters!!.entries.iterator()
                    while (it.hasNext()) {
                        val keyValue = it.next()
                        httpUrlBuild.addQueryParameter(keyValue.key, keyValue.value)
                    }

                }
                httpUrl = httpUrlBuild.build()

                val build = request.newBuilder()
                val buildHeads: Headers
                if (heads != null) {
                    buildHeads = Headers.of(heads!!)
                    build.headers(buildHeads)
                }
                response = chain.proceed(build.url(httpUrl).build())

                httpLog = HttpLog(response!!.body()!!.source().buffer().sha1().hex())
                httpLog.request = request

                httpLog.response = response
                httpLog.code = response.code()
                requestHashMap.put(httpLog.number, HttpLog(httpLog.number))

                if (response.code() != 200||response.code()!=0) {
                    requestHashMap.remove(httpLog.number)
                     throw HttpException(httpLog.code, "http code")
                }else
                {
                    response;
                }

            } catch (e: Exception) {
                e.printStackTrace()
                requestHashMap.remove(httpLog!!.number)
                httpLog.throwable = e
                if (asHttpInterceptor != null) {
                    val httpException = asHttpInterceptor!!.handlerThrowable(e)
                    httpLog.throwable = httpException
                    httpLog.handlerLog()
                    throw httpException
                } else {
                    httpLog.handlerLog();
                    throw e
                }

            }


        }

        return netCacheInterceptor!!;
    }


    inner class ResponseConvertFactory(gson: Gson?) : Converter.Factory() {

        @JvmOverloads
        fun create(gson: Gson = Gson()): ResponseConvertFactory {
            return ResponseConvertFactory(gson)
        }

        init {
            if (gson == null) throw NullPointerException("gson == null")
        }

        override fun responseBodyConverter(type: Type, annotations: Array<Annotation>,
                                           retrofit: Retrofit): Converter<ResponseBody, *> {
            return Converter<ResponseBody, Any> { value ->
                var sha1 = value.source().buffer().sha1().hex()
                var httpLog: HttpLog? = null
                if (value != null) {
                    httpLog = requestHashMap[sha1]
                }
                try {
                    val str = value.string()
                    httpLog!!.data = str
                    if (asHttpInterceptor != null) {
                        val jsonResult = asHttpInterceptor!!.interceptor(httpLog!!.response!!, str)
                        return@Converter JPathGson.fromJson<Any>(jsonResult, jPath, type)
                    } else {
                        return@Converter JPathGson.fromJson<Any>(str, jPath, type)
                    }
                } catch (e: Exception) {
                    if (httpLog != null) {
                        httpLog.throwable = e
                    }
                    if (asHttpInterceptor != null) {
                        throw asHttpInterceptor!!.handlerThrowable(e)
                    } else {
                        throw RuntimeException(e)
                    }
                } finally {
                    if (httpLog != null) {
                        requestHashMap.remove(httpLog.number)
                        httpLog.handlerLog()
                    }
                }
            }
        }

    }

    private fun isText(mediaType: MediaType): Boolean {
        if (mediaType.type() != null && mediaType.type() == "text") {
            return true
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype() == "json" ||
                    mediaType.subtype() == "xml" ||
                    mediaType.subtype() == "html" ||
                    mediaType.subtype() == "webviewhtml")
                return true
        }
        return false
    }

    private fun bodyToString(request: Request): String {
        try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body()!!.writeTo(buffer)
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "something error when show requestBody."
        }

    }


    fun ascii2native(asciicode: String?): String? {
        var asciis = asciicode!!.split("\\\\u".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var nativeValue = asciis[0]
        try {
            for (i in 1 until asciis.size) {
                val code = asciis[i]
                nativeValue += Integer.parseInt(code.substring(0, 4), 16).toChar()
                if (code.length > 4) {
                    nativeValue += code.substring(4, code.length)
                }
            }
        } catch (e: NumberFormatException) {
            return asciicode
        }

        return nativeValue
    }




}
