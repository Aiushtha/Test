package com.lxz.kotlin.tools.http

import java.io.IOException

/**
 * Created by linxingzhu on 2018/1/9.
 */
class HttpException : IOException {
    private var code: Int = 0
    private var msg: String

    constructor(code: Int, message: String) {
        this.code = code
        this.msg = message
    }

    constructor(e: Throwable) {
        code = -1
        msg = e.message!!
    }

    fun getCode(): Int {
        return code
    }

    fun setCode(code: Int): HttpException {
        this.code = code
        return this
    }


    fun getMsg(): String{
        return msg
    }


    override fun toString(): String{
        return msg
    }
}