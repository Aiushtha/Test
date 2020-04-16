package com.cn.lxz.kotlinandroid

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.cn.lxz.kotlinandroid.sql.*
import com.lxz.kotlin.tools.android.idsOnClick
import com.lxz.kotlin.tools.android.toast
import io.reactivex.Observable
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
import kotlinx.android.synthetic.main.item_phone.view.*
import kotlinx.android.synthetic.main.item_sql.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

/**
 * Created by linxingzhu on 2017/11/13.
 */
class SqlActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var data: KotlinReactiveEntityStore<Persistable>
    lateinit var adapter: MyAdapter;

    fun initSql() {
        val DEFAULT = EntityModelBuilder("default")
                .addType(SqlUserEntity.`$TYPE`)
                .addType(SqlPhoneEntity.`$TYPE`)
                .build()
        val source = DatabaseSource(this, DEFAULT, 3)
        data = KotlinReactiveEntityStore<Persistable>(KotlinEntityDataStore(source.configuration))
    }

    fun initData(){
        data.count(SqlUserEntity::class).get().single().subscribe { integer ->
            when(integer) {
                0 -> Observable.fromCallable(CreateSqlUser(data)).flatMap {o->o}.observeOn(Schedulers.computation()).subscribe({ refresh() })
                else -> refresh()
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sql)
        idsOnClick(this, R.id.btn_insert, R.id.btn_detele, R.id.btn_update, R.id.btn_select)
        initSql()
        adapter = MyAdapter();
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        initData()

    }

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
        var etId = EditText(this);
        var etName = EditText(this);
        var linearLayout = LinearLayout(this)
        linearLayout.setOrientation(LinearLayout.VERTICAL)
        etId.setInputType(InputType.TYPE_CLASS_NUMBER)
        etId.hint="input id"
        linearLayout.addView(etId)
        etName.hint="input name"
        linearLayout.addView(etName)
        AlertDialog.Builder(this)
                .setTitle("select id or name")
                .setView(linearLayout)
                .setPositiveButton("确定", DialogInterface.OnClickListener{dialog,width->
                    data.select(SqlUser::class)
                            .where(SqlUserEntity.NAME.like("${etName.text.toString().toString()}")).or(
                             SqlUserEntity.ID.eq(
                                     etId.text.toString().let{try{it.toInt()}catch(e:Throwable){-1}}.toInt())
                             )
                            .orderBy(SqlUserEntity.ID.desc())
                            .limit(100)
                            .get()
                            .toList()
                            .toObservable()
                            .toList()
                            .toObservable()
                            .subscribe({
                                adapter.list.clear()
                                adapter.list.addAll(it)
                                adapter.notifyDataSetChanged()
                            })

                })
                .setNegativeButton("取消", null).show();

    }

    fun update(){
        var etId = EditText(this);
        var etName = EditText(this);
        var linearLayout = LinearLayout(this)
        linearLayout.setOrientation(LinearLayout.VERTICAL)
        etId.setInputType(InputType.TYPE_CLASS_NUMBER)
        etId.hint="input id"
        linearLayout.addView(etId)
        etName.hint="input name"
        linearLayout.addView(etName)
        AlertDialog.Builder(this)
                .setTitle("update name by id")
                .setView(linearLayout)
                .setPositiveButton("确定", DialogInterface.OnClickListener{dialog,width->
                    data.update(SqlUser::class)
                            .set(SqlUserEntity.NAME,etName.text.toString())
                            .where(SqlUserEntity.ID.eq(etId.text.toString().toInt()))
                            .get()
                            .single()
                            .toObservable()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                refresh()
                            })

                })
                .setNegativeButton("取消", null).show();
    }

    fun delete() {
        var et = EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.hint="input id"
        AlertDialog.Builder(this)
                 .setTitle("input id and delete by id")
                 .setView(et)
                 .setPositiveButton("确定", DialogInterface.OnClickListener{dialog,width->
                     data.delete(SqlUser::class)
                             .where(SqlUserEntity.ID.eq(et.text.toString().toInt()))
                             .get()
                             .single()
                             .toObservable()
                             .subscribeOn(Schedulers.io())
                             .observeOn(AndroidSchedulers.mainThread())
                             .subscribe({ refresh() })

                 })
                .setNegativeButton("取消", null).show();
    }

    fun insert() {
        InsertDialog(this, null).show(supportFragmentManager, "insert")
    }

    fun refresh() {
        data.select(SqlUser::class).get().toObservable().toList().subscribe(
                        Consumer<List<SqlUser>> {
                            adapter.list.clear()
                            adapter.list.addAll(it)
                            adapter.notifyDataSetChanged()
                        }

                )
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
                tv_id.text = item.id?.toString()
                tv_name.text = item.name?.toString()
                item.birthday?.run {tv_birthday.text = this.toString()}

                iv_remove.setOnClickListener({
                    data.delete(SqlUser::class)
                            .where(SqlUserEntity.ID.eq(item.id))
                            .get().single()
                            .toObservable().subscribe({ refresh() })
                })
                iv_edit.setOnClickListener {
                    InsertDialog(this@SqlActivity, list.get(position)).show(supportFragmentManager, "edit")
                }
                tv_phone.setOnClickListener {
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

                if (sqlUser == null) sqlUser = SqlUserEntity().apply {
                    birthday=Date()
                    tv_birthday.setText(SimpleDateFormat("yyyy-MM-dd").format(Date()))
                }


                sqlUser?.id?.toString().run { tv_id.text = this }



                tv_birthday.setOnClickListener {
                    with(DatePickerDialog(activity)) {
                        var calendar = Calendar.getInstance()
                        calendar.time = sqlUser?.birthday ?: Date()
                        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        setOnDateSetListener { view, year, month, dayOfMonth ->
                            layout.tv_birthday.text = "${year}-${(month + 1)}-${dayOfMonth}"
                        }
                        show()
                    }
                }
                btn_cancel.setOnClickListener { dismissAllowingStateLoss() }
                var user: SqlUser = sqlUser!!;
                Log.d("sql", ">>${user.id} ${user.name} phone size:${user.phoneNumbers.size}")
                var phoneAdapter = PhonesAdapter(layout.context, user)
                lv_phones.adapter = phoneAdapter



                with(view) {
                    tv_id.text = user.id.toString()
                    tv_name.setText(user.name)
                    user?.birthday?.apply{tv_birthday.setText( SimpleDateFormat("yyyy-MM-dd").format(this))}



                    btn_ok.setOnClickListener {
                        dismissAllowingStateLoss()
                        user.name = tv_name.text.toString()
                        tv_name.setSelection(tv_name.text.toString().length)

                        user.phoneNumbers.clear()
                        phoneAdapter.phones.forEach { phone ->
                            if (!TextUtils.isEmpty(phone.phoneNumber)) {
                                user.phoneNumbers.add(phone)
                            }
                            Log.d("sql", "add phone ${phone} ")
                        }
                        Log.d("sql","user id:"+user.id)
                        when (user.id) {
                            0 -> activity.data.insert(user)
                            else -> activity.data.update(user)
                        }.subscribeBy {
                            activity.refresh()
                        }
                    }


                }


//                btn_ok.setOnClickListener {
//                    dismissAllowingStateLoss()
//                    var user = SqlUserEntity()
//                    user.name = tv_name.text.toString()
//                    user.birthday = SimpleDateFormat("yyyy-MM-dd").parse(tv_birthday.text.toString())
//                    when(user.id)
//                    {
//                        null->activity.data.insert(user)
//                        else->activity.data.update(user)
//                    }.subscribeBy {
//                        activity.refresh()
//                    }
//
//
//                }

            }
        }
    }


    class PhonesAdapter(var ctx: Context, var user: SqlUser) : BaseAdapter() {

        var phones: ArrayList<SqlPhone> = ArrayList();

        init {
            user.phoneNumbers.forEach { e ->
                phones.add(e)
                Log.d("sql", "for each-> ${e.id}:${e.phoneNumber}")
            }
            phones.sortWith(Comparator { a, b -> a.id - b.id })

            phones.add(SqlPhoneEntity())
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            class TempTextWatcher(var index: Int) : TextWatcher {
                override fun afterTextChanged(s: Editable?)= this.run { phones.get(index).phoneNumber = s.toString()}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
            return (convertView ?: LayoutInflater.from(ctx).inflate(R.layout.item_phone, null))
                    .apply {
                        val sqlPhone = phones.get(position)
                        when (et_phone.getTag()) {
                            null -> {
                                var textWatcher = TempTextWatcher(position)
                                et_phone.setTag(textWatcher)
                                et_phone.addTextChangedListener(textWatcher)
                            }
                            else -> (et_phone.getTag() as TempTextWatcher).index = position
                        }
                        et_phone.setText(sqlPhone.phoneNumber ?: "")
                        tv_add.setAutoLinkMask(Linkify.ALL);
                        if (phones.size == position + 1) {
                            tv_add.setText("add")
                            tv_add.setOnClickListener {
                                if (phones.size > 5) {
                                    Toast.makeText(it.context, "max 6 item count", Toast.LENGTH_LONG).show()
                                    return@setOnClickListener
                                }
                                var sql = SqlPhoneEntity();
                                Log.d("sql", "add " + sql.phoneNumber)
                                phones.add(sql)
                                notifyDataSetChanged()
                            }
                        } else {
                            tv_add.setText("remove")
                            tv_add.setTag(position)
                            tv_add.setOnClickListener {
                                phones.removeAt(tv_add.getTag() as Int)
                                notifyDataSetChanged()
                            }
                        }

                    }
        }

        override fun getCount(): Int {
            return phones.size
        }

    }


}