package com.cn.zlh.dgj.worker.base

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.cn.zlh.dgj.worker.sql.Models
import com.zhy.autolayout.config.AutoLayoutConifg
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import org.lxz.utils.log.D
import org.lxz.utils.share.DataShare
import kotlin.properties.Delegates

class App : Application(){

    companion object {
        var instance: App by Delegates.notNull()
        lateinit var api: Api
        fun getDb() = instance.dataBase

    }


    init {
       instance=this
    }

    override fun onCreate() {
        super.onCreate()
        DataShare.init(this)
        D.init(this)
        //AutoLayout初始化
        AutoLayoutConifg.getInstance().useDeviceSize().init(this)
        httpFactory()
    }

    private fun httpFactory() {

    }

    val dataBase: KotlinReactiveEntityStore<Persistable> by lazy {
        val source = DatabaseSource(instance, Models.DEFAULT, 8)
        source.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS)
        KotlinReactiveEntityStore<Persistable>(KotlinEntityDataStore(source.configuration))
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)

    }
}