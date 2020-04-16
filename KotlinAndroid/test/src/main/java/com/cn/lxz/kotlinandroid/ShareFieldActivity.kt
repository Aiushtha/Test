package com.cn.lxz.kotlinandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import org.lxz.utils.log.D
import org.lxz.utils.share.DataShare
import kotlin.reflect.KProperty

class ShareFieldActivity : AppCompatActivity() {

    var text: String by ShareSave("初始值");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var mEditText = EditText(this)
        setContentView(mEditText)
        mEditText.textListen { text=it }
        mEditText.setText(text)

    }

}

class ShareSave(private val defaultValue: String) {
    operator fun getValue(thisRef: Any, prop: KProperty<*>): String {
        return DataShare.getString("&{thisRef::class.java.name}/${prop.name}",defaultValue)
    }
    operator fun setValue(thisRef:Any,prop:KProperty<*>,value:String)
    {
        DataShare.put("&{thisRef::class.java.name}/${prop.name}", value)
    }
}

//class ShareSave(private val key: String,private val defaultValue: String) {
//    operator fun getValue(thisRef: Any?, prop: KProperty<*>): String {
//        return DataShare.getString(key,defaultValue)
//    }
//    operator fun setValue(thisRef:Any?,prop:KProperty<*>,value:String)
//    {
//        DataShare.put(key, value)
//    }
//}

inline fun TextView.textListen(crossinline textChange:(str:String)->Unit){
    this.addTextChangedListener(object:TextWatcher{
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) = textChange.invoke(s.toString())
    })
}
