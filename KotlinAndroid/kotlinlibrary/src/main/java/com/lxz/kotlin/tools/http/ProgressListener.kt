package com.lxz.kotlin.tools.http

/**
 * Created by linxingzhu on 2018/1/9.
 */
interface ProgressListener {
    fun onProgress(length: Long, progress: Long, isComple: Boolean)
}