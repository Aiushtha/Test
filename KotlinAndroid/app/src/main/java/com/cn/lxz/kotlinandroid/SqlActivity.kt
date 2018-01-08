package com.cn.lxz.kotlinandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.reactivex.rxkotlin.toMaybe
import io.reactivex.rxkotlin.toSingle
import kotlinx.android.synthetic.main.activity_sql.*

/**
 * Created by linxingzhu on 2017/11/13.
 */
 class SqlActivity:AppCompatActivity(),View.OnClickListener{
    override fun onClick(view: View?) {
        when(view?.id){
//          R.id.btn_add->into();
//          R.id.btn_search->search();
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sql)
        val adapter = SqlAdapter();
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
//        idsOnClick(this,R.id.btn_add,R.id.btn_search)
    }


    class  SqlAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getItemCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }



}