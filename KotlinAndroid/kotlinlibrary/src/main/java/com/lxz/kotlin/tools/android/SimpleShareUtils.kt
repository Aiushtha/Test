package com.lxz.kotlin.tools.android

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import org.lxz.utils.share.DataShare




/**
 * 初始化简单存储
 * @param app androidApplication
 * */
fun initSimpleShare(app:Application)=DataShare.init(app)

/**
 * 获得简单存储的setting
 */
fun getSimpleShare(): SharedPreferences =DataShare.getInstance().androidShare.settings;

/**
 * 将任何非空对象以自身类型json格式保存-单例
 * */
fun Any?.saveAsJsonBeanSelf():Any?= {DataShare.saveJsonObject(this);this}

/**
 * 以类型返回该存储对象-单例
 */
fun <T> Class<T>.getAsJsonBeanSelf()=DataShare.getJsonObject(this)

/**
 * 将任何非空对象存储在以json格式存储在key上
 * @param key 指定的key值
 */
fun Any?.saveAsJsonBeanInKey(key:String?):Any?= key.saveAsJsonBean(this)
/**
 * 将非空字符串以指定key值保存
 * @param key 指定的key值
 * */
fun String?.saveInKey(key:String?)= DataShare.put(key,this)
/**
 * 将非空整数以指定key值保存
 * @param key 指定的key值
 * */
fun Int?.saveInKey(key:String?)= DataShare.put(key,this)

/**
 * 将非空Float以指定key值保存
 * @param key 指定的key值
 */
fun Float?.saveInKey(key:String?)= DataShare.put(key,this)

/**
 * 将非空Long以指定key值保存
 * @param key 指定的key值
 */
fun Long?.saveKey(key: String?)= DataShare.put(key,this)

/**
 * 将非空Boolean以指定key值保存
 * @param key 指定的key值
 */
fun Boolean?.saveInkey(key:String?)= DataShare.put(key,this)

/**从指定的非空key值返回保存的字符串*/
fun String?.getStoreString():String?= DataShare.getString(this)
/**从指定的非空key值返回保存的整数*/
fun String?.getStoreInt():Int= DataShare.getInt(this)
/**从指定的非空key值返回保存的Float*/
fun String?.getStoreFloat():Float= DataShare.getFloat(this)
/**从指定的非空key值返回保存的Long*/
fun String?.getStoreLong():Long= DataShare.getLong(this)
/**从指定的非空key值返回保存的Boolean*/
fun String?.getStoreBoolean():Boolean= DataShare.getBoolean(this)
/**已指定的key值保存值*/
fun String?.saveAsJsonBean(value:Any?):Any?=DataShare.saveJsonObject(this,value)
/**已指定key值以json转换为已指定的类型*/
fun <T> String?.getAsJsonBean(cla:Class<T>):Any?=Gson().fromJson<T>(DataShare.getString(this),cla);

/**
 * 已指定的key值保存字符串
 * @param key 指定的value值
 * */
fun String?.saveString(value:String?)=DataShare.put(this,value)
/**
 * 已指定的key值保存整数
 * @param key 指定的value值
 * */
fun String?.saveInt(value:Int?)=DataShare.put(this,value)
/**
 * 已指定的key值保存Float
 * @param key 指定的value值
 * */
fun String?.saveFloat(value:Float?)=DataShare.put(this,value)
/**
 * 已指定的key值保存布尔值
 * @param key 指定的value值
 * */
fun String?.saveBoolean(value:Boolean?)=DataShare.put(this,value)
/**
 * 已指定的key值保存Long
 * @param key 指定的value值
 * */
fun String?.saveLong(value:Long?)=DataShare.put(this,value)


