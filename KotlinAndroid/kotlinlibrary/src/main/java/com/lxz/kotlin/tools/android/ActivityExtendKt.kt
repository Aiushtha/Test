package com.lxz.kotlin.tools.android

import android.app.Activity
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

/**
 * @author lxz
 * */

/**
 * Created by linxingzhu on 2017/11/4.
 */

private var clickIntervalTime=2L;

/**
 * 设置默认点击事件的间隔
 * */
fun setClickIntervalTime(time:Long){
    clickIntervalTime =time;
}

/***
 * 对一组控件id组指定onclick事件 默认时间间隔为2秒
 * @param click 点击事件
 * @param ids 输入点击空间编号 如R.id.btn1,R.id.btn2.....
 */
fun Activity.idsOnClick(click:View.OnClickListener,vararg ids: Int): View.OnClickListener {
    for (id in ids)
    {
       var view=findViewById<View>(id);
       RxView.clicks(findViewById<View>(id))
               .throttleFirst(clickIntervalTime,TimeUnit.SECONDS)
               .subscribe(Consumer<Any> { click.onClick(view) });
    }

    return click;
}
/***
 * 对一组控件id组指定onclick事件
 * @param click 点击事件
 * @param intervalTime 点击事件间隔时间
 * @param ids 输入点击空间编号 如R.id.btn1,R.id.btn2.....
 */
fun Activity.idsOnClick(click:View.OnClickListener,intervalTime:Long,timeUnit: TimeUnit,vararg ids: Int): View.OnClickListener {
    for (id in ids)
    {
        var view=findViewById<View>(id);
        RxView.clicks(findViewById<View>(id))
                .throttleFirst(intervalTime,timeUnit)
                .subscribe(Consumer<Any> { click.onClick(view) });
    }

    return click;
}

/***
 * 从view开始查找一组控件id  指定onclick事件
 * @param views 从指定的view开始
 * @param ids 输入点击空间编号 如R.id.btn1,R.id.btn2.....
 */
fun View.OnClickListener.viewOnClick(views:View,vararg ids: Int){
    for (id in ids)
    {
        var view=views.findViewById<View>(id);
        RxView.clicks(view)
                .throttleFirst(clickIntervalTime,TimeUnit.SECONDS)
                .subscribe(Consumer<Any> { this.onClick(view) });
    }
}

/***
 * 弹出一个Toast框
 * @param toast 传入字符串
 */
fun Activity.toast(str: String)= Toast.makeText(this, str, Toast.LENGTH_SHORT).show()

