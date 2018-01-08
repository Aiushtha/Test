package com.lxz.kotlin.tools.http

import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okio.Buffer
import okio.BufferedSink
import okio.Okio
import okio.Source
import org.lxz.utils.http.AsHttp.createCustomRequestBody
import org.lxz.utils.log.D
import java.io.File
import java.io.IOException

/**
 * 默认OKHttpClient客户端
 * */
var mOkHttpClient: OkHttpClient = OkHttpClient()
fun initOkHttpClient(client: OkHttpClient) {
    mOkHttpClient = client
}

/**
 * 获得默认的OKHttpClient
 * */
fun getOKHttpClient(): OkHttpClient = if (mOkHttpClient == null) OkHttpClient() else mOkHttpClient


/**
 *将url转成httpRequest
 */
fun String.toOkHttpRequest(): Request = Request.Builder().url(this).build()

/**
 *将url转成Response
 * */
fun String.toOkHttpUri(): Response = getOKHttpClient().newCall(toOkHttpRequest()).execute()
fun String.toRxObserable(): Observable<String> = Observable.defer { Observable.just(toOkHttpUri().body()!!.string()) }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> String.toRxObserable(cla: Class<T>): Observable<T> =
        Observable.create<T> { e ->
            e.onNext(Gson().fromJson(toOkHttpUri().body()!!.string(), cla))
            e.onComplete()
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


private val onBody: (Any) -> Unit = {}
private val onHead: (Any) -> Unit = {}


/**
 * 表单提交
 * */
fun <T> String.simpleBodyForm(
        client: OkHttpClient,
        type: Class<T>,
        head: Map<Any?, Any?>,
        body: Map<Any?, Any?>): Observable<T>
        = bodyForm((if (client == null) getOKHttpClient() else client)
        , type, {
    if (head != null) {
        var iterator = head.iterator();
        while (iterator.hasNext()) {
            var keyValue = iterator.next();
            it.addHeader(keyValue.key.toString(), keyValue.value.toString())
        }
    }
}, {
    if (body != null) {
        var iterator = body.iterator()
        while (iterator.hasNext()) {
            var keyValue = iterator.next()

            it.add(keyValue.key.toString(), keyValue.value.toString())
        }
    }
})

/**
 * 表单提交
 * @param type 传入的POJO-javabean类型
 * @param head http-head
 * @param body formbody
 * */
fun <T> String.bodyForm(
        type: Class<T>,
        head: (Request.Builder) -> Unit = onHead,
        body: (FormBody.Builder) -> Unit = onBody): Observable<T>
        = createFrom(getOKHttpClient(), type, this, head, body)

/**
 * 表单提交
 * @param client 传入客户端
 * @param type 传入的POJO-javabean类型
 * @param head http-head
 * @param body formbody
 * */
fun <T> String.bodyForm(
        client: OkHttpClient,
        type: Class<T>,
        head: (Request.Builder) -> Unit = onHead,
        body: (FormBody.Builder) -> Unit = onBody): Observable<T>
        = createFrom((if (client == null) getOKHttpClient() else client), type, this, head, body)

/**
 * 创建一个表单
 * @param client okhttpclient
 * @param type 传入的类型
 * @param url 传入的url地址
 * @param head http-head
 * @param body frombody
 *
 * */
private fun <T> createFrom(client: OkHttpClient, type: Class<T>, url: String, head: (Request.Builder) -> Unit, body: (FormBody.Builder) -> Unit): Observable<T> {
    return Observable.create<T> { e ->
        val bodyBuild: FormBody.Builder
        val body: FormBody;
        bodyBuild = FormBody.Builder()
        onBody.invoke(bodyBuild)
        body = bodyBuild.build()
        var requestBuild = Request.Builder().url(url)
        head.invoke(requestBuild)
        var request: Request = requestBuild.method("POST", body).build()
        var json: String = client!!.newCall(request).execute().body()!!.string();
        var obj = Gson().fromJson<T>(json, type);
        e.onNext(obj)
        e.onComplete()
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

/**
 * 请求表单提交
 * @param type POJO-javaBeans
 * @param head http-head
 * @param body http-body
 * */
fun <T> String.simpleMultipartForm(type: Class<T>, head: Map<Any?, Any?>, body: Map<Any?, Any?>): Observable<T>
        = simpleMultipartForm(getOKHttpClient(),type,head,body)

/**
 * 请求表单提交
 * @param type POJO-javaBeans
 * @param head http-head
 * @param body http-body
 * */
fun <T> String.simpleMultipartForm(client: OkHttpClient, type: Class<T>, head: Map<Any?, Any?>, body: Map<Any?, Any?>): Observable<T>
        = multipartBodyForm(client, type, this, {
    if (head != null) {
        var iterator = head.iterator();
        while (iterator.hasNext()) {
            var keyValue = iterator.next();
            it.addHeader(keyValue.key.toString(), keyValue.value.toString())
        }
    }
}, {
    if (body != null) {
        var iterator = body.iterator()
        while (iterator.hasNext()) {
            var keyValue = iterator.next();
            it.addFormDataPart(keyValue.key.toString(), keyValue.value.toString())
        }
    }
})


fun <T> String.multipartForm(
        client: OkHttpClient,
        type: Class<T>,
        head: (Request.Builder) -> Unit = onHead,
        body: (MultipartBody.Builder) -> Unit = onBody): Observable<T>
        = multipartBodyForm((if (getOKHttpClient() == null) OkHttpClient() else getOKHttpClient()), type, this, head, body)

private fun <T> multipartBodyForm(client: OkHttpClient, type: Class<T>, url: String, head: (Request.Builder) -> Unit, body: (MultipartBody.Builder) -> Unit): Observable<T> {
    return Observable.create<T> { e ->
        val bodyBuild: MultipartBody.Builder
        bodyBuild = MultipartBody.Builder()
        body.invoke(bodyBuild)
        val body = bodyBuild.build();
        var requestBuild = Request.Builder().url(url)
        head.invoke(requestBuild)
        var request: Request = requestBuild.method("POST", body).build()
//        e.onNext()
        e.onComplete()
        D.show(body.toString())
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

/**text/html;HTML格式*/
val MEDIATYPE_TEXT_HTML = MediaType.parse("text/html")
/**text/plain;纯文本格式*/
val MEDIATYPE_TEXT_PLAIN = MediaType.parse("text/plain")
/**text/xml;XML格式*/
val MEDIATYPE_TEXT_XML = MediaType.parse("text/xml")
/**image/gif;gif图片格式*/
val MEDIATYPE_TEXT_GIF = MediaType.parse("image/gif")
/**image/jpeg;jpg图片格式*/
val MEDIATYPE_TEXT_JPEG = MediaType.parse("image/jpeg")
/**image/png;png图片格式*/
val MEDIATYPE_TEXT_PNG = MediaType.parse("image/png")
/**application/xhtml+xml;XHTML格式*/
val MEDIATYPE_XHTML = MediaType.parse("application/xhtml+xml")
/**application/xml;XML数据格式*/
val MEDIATYPE_XML = MediaType.parse("application/xml")
/**application/atom+xml;Atom XML聚合格式*/
val MEDIATYPE_ATOM = MediaType.parse("application/atom+xml")
/**application/json;JSON数据格式*/
val MEDIATYPE_JSON = MediaType.parse("application/json")
/*application/pdf;pdf格式*/
val MEDIATYPE_PDF = MediaType.parse("application/pdf")
/**application/msword;Word文档格式*/
val MEDIATYPE_MSWORD = MediaType.parse("application/msword")
/**application/octet-stream ： 二进制流数据（如常见的文件下载）*/
val MEDIATYPE_STREAM = MediaType.parse("application/octet-stream")
/**application/x-www-form-urlencoded ： <form encType=””>中默认的encType，form表单数据被编码为key/value格式发送到服务器（表单默认的提交数据的格式）*/
val MEDIATYPE_FROM = MediaType.parse("application/x-www-form-urlencoded")

/**
 * 文件上传 下载 进度
 * */
interface FileProgressListener {
    /**
     * @param 文件长度
     * @param 文件进度
     * @param 是否完成
     * */
    fun onProgress(length: Long, progress: Long, isComple: Boolean): Unit
}

/**
 * 添加部分文件
 * @param type 媒体类型
 * @param name 文件名
 * @param file 文件
 * @param listen 文件进度监听
 */
fun MultipartBody.Builder.addFileDataPart(type: MediaType, name: String, file: File, listen: (length: Long, progress: Long, isComple: Boolean) -> Unit) {
    var body = RequestBody.create(type, file);
    this.addFormDataPart(name, file.name, createCustomRequestBody(body, file, listen))
}

///**
// * 添加部分文件
// * @param type 媒体类型
// * @param name 文件名
// *
// */
//fun MultipartBody.Builder.addFileDataPart(type: MediaType, name: String, filename: String, file: File, listen: (length: Long, progress: Long, isComple: Boolean) -> Unit) {
//    var body = RequestBody.create(type, file);
//    this.addFormDataPart(name, filename, createCustomRequestBody(body, file, listen))
//}

/**
 * 创建自定义的请求结构体
 * @param body 请求的body
 * @param file 请求的文件
 * @param listener 文件处理进度
 * */
private fun createCustomRequestBody(body: RequestBody, file: File, listener: FileProgressListener): RequestBody {
    return object : RequestBody() {

        override fun contentType(): MediaType? {
            return body.contentType()
        }

        override fun contentLength(): Long {
            return file.length()
        }

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            val source: Source
            try {
                source = Okio.source(file)
                //                    sink.writeAll(source);
                val buf = Buffer()
                var remaining: Long = contentLength()
                var readCount: Long = 0
                while ((readCount == source.read(buf, 4098))) {
                    sink.write(buf, readCount)
                    remaining -= readCount;
                    listener.onProgress(contentLength(), remaining, (remaining == 0L))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}