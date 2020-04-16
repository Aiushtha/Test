package com.lxz.kotlin.tools.http

import okhttp3.Response

/**
 * Created by linxingzhu on 2018/1/9.
 */
interface AsHttpInterceptor {
    /**
     * 对返回的请求进行过滤
     * @param response 对网络相应和返回的json进行过滤
     * @param json 返回的json数据
     * */
    @Throws(Exception::class)
    fun interceptor(response: Response, json: String): String
    /**
     * 对异常进行处理
     * @param e 异常
     * */
    fun handlerThrowable(e: Throwable): HttpException
    /**
     * 对http code进行过滤
     * @param code
     * */
    fun httpCodeMessage(code: Int): String
}
