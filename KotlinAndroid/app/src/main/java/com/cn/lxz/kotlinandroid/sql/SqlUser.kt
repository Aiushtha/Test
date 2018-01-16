package com.cn.lxz.kotlinandroid.sql

import android.os.Parcelable
import io.requery.*
import java.util.*

/**
 * Created by linxingzhu on 2018/1/11.
 */
@Entity
interface SqlUser : Parcelable, Persistable {
    @get:Key
    @get:Generated
    val id:Int
    var name:String
    var birthday: Date
    @get:OneToMany(mappedBy = "owner")
    val phoneNumbers: MutableSet<SqlPhone>
}