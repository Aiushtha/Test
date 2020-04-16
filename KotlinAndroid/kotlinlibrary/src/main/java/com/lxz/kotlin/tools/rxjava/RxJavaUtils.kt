package com.lxz.kotlin.tools.rxjava

import android.app.Activity
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


fun <T> Observable<T>.httpRunOn()= this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.threadRunOn()= this.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
