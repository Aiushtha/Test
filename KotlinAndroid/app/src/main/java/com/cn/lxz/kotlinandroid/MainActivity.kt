package com.cn.lxz.kotlinandroid

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

@SuppressLint("Registered")
/**
 * Created by linxingzhu on 2017/11/13.
 */
class MainActivity :android.app.ListActivity(), AdapterView.OnItemClickListener {

        enum class ItemAction(var msg: String, var cla: Class<*>) {
            A("点击事件", ClickEventActivity::class.java),
            B("retrofit网络请求与配置", HttpActivity::class.java),
            C("Kotlin OkHttp", OkHttpActivity::class.java),
            D("数据库", SqlActivity::class.java),
            E("存储", ShareActivity::class.java),
            F("权限",PermissionsActivity::class.java);
            override fun toString(): String { return msg }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, ItemAction.values())
            listAdapter = adapter
            listView.onItemClickListener = this

        }

        override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
            startActivity(Intent(this, ItemAction.values()[i].cla))
        }


}