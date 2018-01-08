package com.lxz.kotlin.tools.lang

/**
 * 计算函数耗时时间
 */
fun calculateTime(f:()->Unit):Unit{
    var oldTime=System.currentTimeMillis()
    f();
    var newTime=System.currentTimeMillis();
    println("fun->${newTime-oldTime}")

}