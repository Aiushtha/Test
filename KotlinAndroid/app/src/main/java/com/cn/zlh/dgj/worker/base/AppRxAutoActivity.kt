//package com.cn.zlh.dgj.worker.base
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.os.Bundle
//import android.util.AttributeSet
//import android.view.View
//import android.view.ViewGroup
//import android.widget.LinearLayout
//import com.cn.zlh.dgj.worker.R
//import com.community.custom.zwashservice.base.rx.RxActivity
//import com.zhy.autolayout.AutoFrameLayout
//import com.zhy.autolayout.AutoLinearLayout
//import com.zhy.autolayout.AutoRelativeLayout
//import kotlinx.android.synthetic.main.activity_template.*
//import kotlinx.android.synthetic.main.app_super_content.*
//import org.lxz.utils.base.LayoutId
//import org.lxz.utils.base.TitleName
//
//
///**
// * Created by linxingzhu on 2018/2/28.
// */
////@LayoutId(R.layout.support_simple_spinner_dropdown_item)
//abstract class AppRxAutoActivity : RxActivity(),View.OnClickListener {
//
//    private val LAYOUT_LINEARLAYOUT = "LinearLayout"
//    private val LAYOUT_FRAMELAYOUT = "FrameLayout"
//    private val LAYOUT_RELATIVELAYOUT = "RelativeLayout"
//
//
//    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
//        var view: View? = null
//        if (name == LAYOUT_FRAMELAYOUT) {
//            view = AutoFrameLayout(context, attrs)
//        }
//
//        if (name == LAYOUT_LINEARLAYOUT) {
//            view = AutoLinearLayout(context, attrs)
//        }
//
//        if (name == LAYOUT_RELATIVELAYOUT) {
//            view = AutoRelativeLayout(context, attrs) as View?
//        }
//
//        return if (view != null) view else super.onCreateView(name, context, attrs)
//
//    }
//
//
//    @SuppressLint("MissingSuperCall")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //使用模板
//        super.setContentView(R.layout.activity_template)
//        action_bar_toolbar?.run {setSupportActionBar(action_bar_toolbar)}
//
//
//        val layoutId = this.javaClass.getAnnotation(LayoutId::class.java)
//        layoutId?.run {if(layoutId.value!=0){setContentView(layoutId.value)}}
//
//        val titleName = this.javaClass.getAnnotation(TitleName::class.java)
//        titleName?.run {if(titleName.value!=null)setTitle(titleName.value)}
//
//        afterOnCreate(savedInstanceState)
//    }
//
//    abstract fun afterOnCreate(savedInstanceState: Bundle?)
//
//
//    override fun setContentView(view: View) {
//        app_super_container?.removeAllViews()
//        app_super_container?.addView(view, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
//
//    }
//
//    override fun setContentView(layout: Int) {
//        app_super_container?.removeAllViews()
//        app_super_container?.addView(getLayoutInflater().inflate(layout, null),
//                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
//
//    }
//
//
//    override fun setTitle(name: Int) {
//        setTitle(resources.getString(name))
//    }
//
//    protected fun setTitle(name: String) {
//        action_bar_title?.setText(name)
//    }
//
//    protected fun setActionText(name: String){
//        action_bar_text?.run {  action_bar_text.setText(name)
//            action_bar_text.setTextColor(-0xc4544d)
//        }
//    }
//
//
//}