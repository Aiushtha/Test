package com.cn.lxz.kotlinandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.layout.*
import com.lxz.kotlin.tools.android.idsOnClick

class ClickEventActivity : AppCompatActivity(),View.OnClickListener {
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn1 -> text1.append("btn1");
            R.id.btn2 -> text1.append("btn2");
            R.id.btn3 -> text1.append("btn3");
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout);
        idsOnClick(this, R.id.btn1, R.id.btn2, R.id.btn3)


    }


}
