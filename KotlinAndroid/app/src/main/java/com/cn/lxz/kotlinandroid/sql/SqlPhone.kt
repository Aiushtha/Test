package com.cn.lxz.kotlinandroid.sql

import android.os.Parcelable
import io.requery.*

@Entity
interface SqlPhone : Parcelable, Persistable {
    @get:Key
    @get:Generated
    val id: Int

    var phoneNumber: String

    @get:ManyToOne
    var owner: SqlUser
}
