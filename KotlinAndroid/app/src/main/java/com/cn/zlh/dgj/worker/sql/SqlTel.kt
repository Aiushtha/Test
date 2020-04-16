package com.cn.zlh.dgj.worker.sql

import android.os.Parcelable
import io.requery.Entity
import io.requery.Generated
import io.requery.Key
import io.requery.Persistable

/**
 * Created by linxingzhu on 2018/2/24.
 */
@Entity
interface SqlTel : Parcelable, Persistable {
    @get:Key
    @get:Generated
    val id: Int



    var num_id: Int


    var isBranch: Int  //0表示不是 1表示部门
    var name: String
    var tel: String
    var content: String
    var headUrl: String

    //部门名称
    var branchName: String
    var level: Int 
    var mobile_show: Int 

    //是否包含下级 格式[1,2,3]
    var ids: String


    var branch_id: Int 
}