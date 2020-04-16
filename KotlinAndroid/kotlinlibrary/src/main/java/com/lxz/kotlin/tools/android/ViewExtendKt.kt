package com.lxz.kotlin.tools.android

import android.app.Activity
import android.view.View
import android.widget.Toast

/**
 * Created by linxingzhu on 2017/11/4.
 */



fun View.toast(str: String)= Toast.makeText(this.context, str, Toast.LENGTH_SHORT).show()



