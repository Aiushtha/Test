package com.cn.lxz.kotlinandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.lxz.kotlin.tools.android.*
import com.lxz.kotlin.tools.android.idsOnClick
import com.lxz.kotlin.tools.android.toast
import org.lxz.utils.share.DataShare
import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class ShareActivity:AppCompatActivity(),View.OnClickListener {
    val KEY_NAME:String = "NAME"
    val KEY_AGE:String = "AGE"
    val KEY_SEX:String = "SEX"
    val KEY_NUM:String = "NUM"
    val KEY_WEIGHT:String= "WEIGHT"
    val KEY_USER:String= "USER"
    data class User(val name:String,val age:Int,val sex:Boolean)





    override fun onClick(v: View?) {
        when(v?.id){
          R.id.btn1 -> saveData()
          R.id.btn2 -> saveObject()
          R.id.btn3 -> saveObjectInKey()
        }
    }

    fun saveData() {
        KEY_NAME.saveString("lxz")
        KEY_AGE.saveInt(18)
        KEY_SEX.saveBoolean(false)
        KEY_NUM.saveLong(1L)
        KEY_WEIGHT.saveFloat(1.5f)
        toast("name:${KEY_NAME.getStoreString()} age:${KEY_AGE.getStoreInt()} sex:${KEY_SEX.getStoreBoolean()} num:${KEY_NUM.getStoreLong()} weight:${KEY_WEIGHT.getStoreFloat()}");
    }
    fun saveObject() {
        User("lxz",18,true).saveAsJsonBeanSelf()
        toast(User::class.java.getAsJsonBeanSelf().toString());
    }

   fun saveObjectInKey() {
        User("xxx", 16, false).saveAsJsonBeanInKey(KEY_USER)
        toast(KEY_USER.getAsJsonBean(User::class.java).toString());
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        idsOnClick(this,R.id.btn1,R.id.btn2,R.id.btn3)
        initSimpleShare(application);//初始化函数


    }

}