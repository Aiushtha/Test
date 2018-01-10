package com.lxz.kotlin.tools.http

/**
 * Created by linxingzhu on 2018/1/9.
 */
open interface LogListen {
    fun log(httpLog: AsHttpFactory.HttpLog)
}
