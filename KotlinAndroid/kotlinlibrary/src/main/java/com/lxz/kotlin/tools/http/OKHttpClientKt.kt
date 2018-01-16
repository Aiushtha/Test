package com.lxz.kotlin.tools.http

import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import java.lang.reflect.Type

/**
 * Created by linxingzhu on 2018/1/11.
 */

class Http(val httpClient:OkHttpClient,val url: String?){
    var headMap:Map<Any?,Any?>?=null
    var bodyMap:Map<Any?,Any?>?=null
    var multBodyBuild: ((MultipartBody.Builder) -> Unit?)? = null

    fun setHead(map:Map<Any?,Any?>):Http{
        headMap=map
        return this
    }
    fun setBody(map:Map<Any?,Any?>):Http{
        bodyMap=map
        return this
    }

    fun setMultBodyBuild(build: (MultipartBody.Builder) -> Unit?):Http{
        multBodyBuild=build
        return this
    }



    fun <T> toObservable(type:Type):Observable<T> = toObservable(type,null)


    fun <T> toObservable(type:Type,jsonPath:String?):Observable<T>{
        return Observable.create<T> { e ->
            val bodyBuild: MultipartBody.Builder
            bodyBuild = MultipartBody.Builder()

            var requestBuild = Request.Builder().url(url)

            headMap?.iterator()?.forEach { (key,value) -> bodyBuild }
            bodyMap?.iterator()?.forEach { (key, value) -> requestBuild.addHeader(key.toString(),value.toString());}
            multBodyBuild?.invoke(bodyBuild)

            var body = bodyBuild.build();
            var request = requestBuild.method("POST", body).build();

            var json: String = httpClient?.newCall(request)?.execute()?.body()?.string()!!;
            var obj = Gson().fromJson<T>(JPathGson.readJsonPath(json,jsonPath), type);
            e.onNext(obj)
            e.onComplete()

        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun OkHttpClient.http(url: String)=Http(httpClient = this, url = url)