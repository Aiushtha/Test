package com.lxz.kotlin.tools.http

import okhttp3.OkHttpClient

/**
 * Created by linxingzhu on 2018/1/9.
 */
interface ClientBuilder {
    fun build(build: OkHttpClient.Builder)
}
