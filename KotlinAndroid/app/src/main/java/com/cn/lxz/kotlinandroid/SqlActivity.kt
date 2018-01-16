package com.cn.lxz.kotlinandroid

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.cn.lxz.kotlinandroid.sql.SqlPhoneEntity
import com.cn.lxz.kotlinandroid.sql.SqlUser
import com.cn.lxz.kotlinandroid.sql.SqlUserEntity
import com.lxz.kotlin.tools.android.idsOnClick
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.meta.EntityModelBuilder
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import kotlinx.android.synthetic.main.activity_sql.*
import kotlinx.android.synthetic.main.dialog_user_insert.*
import kotlinx.android.synthetic.main.item_sql.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by linxingzhu on 2017/11/13.
 */
class SqlActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var data: KotlinReactiveEntityStore<Persistable>
    lateinit var adapter: MyAdapter;


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_insert -> insert()
            R.id.btn_detele -> delete()
            R.id.btn_update -> update()
            R.id.btn_select -> select()
        }

    }

    fun select() {
        /***
        操作符	等同	描述	示例
        ==	.eq.	检查两个操作数的值相等与否，如果是，则条件变为真。	(A == B) i不为 true.
        /=	.ne.	检查，两个操作数的值相等与否，如果值不相等，则条件变为真。	(A != B) 为 true.
        >	.gt.	检查，左操作数的值大于右操作数的值，如果是的话那么条件为真。	(A > B)不为true.
        <	.lt.	检查，左操作数的值是否小于右操作数的值，如果是的话那么条件为真。	(A < B) 是 true.
        >=	.ge.	检查，左边的操作数的值是否大于或等于右操作数的值，如果是，则条件变为真。	(A >= B) 不为 true.
        <=	.le.	检查，左边的操作数的值是否小于或等于右操作数的值，如果是，则条件变为真。	(A <= B)
         **/

        data.select(SqlUser::class)
                .where(SqlUserEntity.NAME.like("b%")).and(SqlUserEntity.ID.gt(0))
                .orderBy(SqlUserEntity.ID.desc())
                .limit(5)
                .get()
                .toList().toObservable()
                .subscribe({

                });




//        var type = SqlUserEntity::javaClass
//        data.select(SqlUser::class.java).get(
//
//        data.raw(SqlUser::class.java,"","")
//        data.raw(SqlUserEntity.CREATOR,"").toObservable().subscribe(
//                {
//                    tv_msg.setText(""+it.count())
//                }
//        )
    }

    fun update() {
//        data.update().where()
    }

    fun delete() {
        data.delete(SqlUser::class)
                .where((SqlUserEntity.NAME.like("%lxz%")))
                .get()
                .single()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    refresh()
                })

    }

    fun insert() {
        InsertDialog(this, null).show(supportFragmentManager, "insert")
    }

    fun refresh() {
        data.select(SqlUser::class)
                .get()
                .toObservable()
                .toList()
                .subscribe(
                        Consumer<List<SqlUser>> {
                            adapter.list.clear()
                            adapter.list.addAll(it)
                            adapter.notifyDataSetChanged()
                        }

                )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sql)
        idsOnClick(this, R.id.btn_insert, R.id.btn_detele, R.id.btn_update, R.id.btn_select)
        initSql()
        adapter = MyAdapter();
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun initSql() {
        val DEFAULT = EntityModelBuilder("default")
                .addType(SqlUserEntity.`$TYPE`)
                .addType(SqlPhoneEntity.`$TYPE`)
                .build()
        val source = DatabaseSource(this, DEFAULT, 3)
        data = KotlinReactiveEntityStore<Persistable>(KotlinEntityDataStore(source.configuration))
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    inner class MyAdapter : RecyclerView.Adapter<MyHolder>() {

        var list: ArrayList<SqlUser> = ArrayList()


        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            var item = list.get(position)
            with(holder.itemView) {
                tv_id.text = item.id.toString()
                tv_name.text = item.name.toString()
                tv_birthday.text = item.birthday.toString()

                iv_remove.setOnClickListener({
                    data.delete(SqlUser::class)
                            .where(SqlUserEntity.ID.eq(item.id))
                            .get().single()
                            .toObservable().subscribe({ refresh() })
                })
                iv_edit.setOnClickListener {
                    InsertDialog(this@SqlActivity, list.get(position)).show(supportFragmentManager, "edit")
                }
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyHolder {
            var layout = layoutInflater.inflate(R.layout.item_sql, null);
            //添加下划线
            layout.iv_remove.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG)
            layout.iv_edit.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG)
            layout.tv_phone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG)
            return MyHolder(layout);
        }

    }


    @SuppressLint("ValidFragment")
    companion object {
        class InsertDialog(var activity: SqlActivity, var sqlUser: SqlUser?) : DialogFragment() {

            override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.dialog_user_insert, container)

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onViewCreated(layout: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                sqlUser?.id.toString().run {tv_id.text=this}

                tv_birthday.setOnClickListener {
                    with(DatePickerDialog(activity)) {
                        var calendar = Calendar.getInstance()
                        calendar.time = sqlUser?.birthday?:Date()
                        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        setOnDateSetListener { view, year, month, dayOfMonth ->
                            layout.tv_birthday.text = "${year}-${(month + 1)}-${dayOfMonth}"
                        }
                        show()
                    }
                }
                btn_cancel.setOnClickListener { dismissAllowingStateLoss() }
                sqlUser?.run {
                    var user : SqlUser= sqlUser!!;
                    with(view) {
                        tv_id.text = user.id.toString()
                        tv_name.setText(user.name)
                        tv_birthday.text = user.birthday.toString()


                        btn_ok.setOnClickListener {
                            dismissAllowingStateLoss()
                            user.name = tv_name.text.toString()
                            tv_name.setSelection(tv_name.text.toString().length)
                            user.birthday = SimpleDateFormat("yyyy-MM-dd").parse(tv_birthday.text.toString())
                            activity.data.update(user)
                                    .subscribeBy {
                                        activity.refresh()
                                    }
                        }
                        return
                    }
                }

                tv_birthday.setText( SimpleDateFormat("yyyy-MM-dd").format(Date()))

                btn_ok.setOnClickListener {
                    dismissAllowingStateLoss()
                    var user=SqlUserEntity()
                    user.name = tv_name.text.toString()
                    user.birthday = SimpleDateFormat("yyyy-MM-dd").parse(tv_birthday.text.toString())
                    var phone = SqlPhoneEntity();
                    phone.phoneNumber=et_phone.text.toString()
                    user.phoneNumbers.add(phone)
                    activity.data.insert(user)
                            .subscribeBy {
                                activity.refresh()
                            }

                }

            }
        }
    }


}