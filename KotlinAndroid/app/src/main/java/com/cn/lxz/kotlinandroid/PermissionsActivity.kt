package com.cn.lxz.kotlinandroid

import android.Manifest
import android.graphics.Camera
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.lxz.kotlin.tools.android.toast
import com.lxz.kotlin.tools.http.bodyForm
import com.lxz.kotlin.tools.http.initOkHttpClient
import com.lxz.kotlin.tools.http.simpleMultipartForm
import com.lxz.kotlin.tools.lang.asJsonFromat
import com.lxz.kotlin.tools.android.idsOnClick
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_okhttp.*
import okhttp3.OkHttpClient
import org.lxz.utils.http.AsHttpFactory
import android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.SurfaceView
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions





/**
 * Created by linxingzhu on 2017/11/28.
 */
class PermissionsActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_camera -> request();
        }
    }


    fun request(){
        val rxPermissions = RxPermissions(this)
        rxPermissions.setLogging(true)
        rxPermissions.requestEach(Manifest.permission.CAMERA)
                .subscribeBy (
                        onNext = {
                            if(it.granted)
                            {
                                toast("it granted");
                            }else if(it.shouldShowRequestPermissionRationale)
                            {
                                 toast("shouldShowRequestPermissionRationale");
                            }else
                            {
                                 toast("can enable the camera")
                            }
                        }
                )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)
        idsOnClick(this,R.id.btn_camera)
//        val rxPermissions = RxPermissions(this)
//        rxPermissions.setLogging(true)
//
//        RxView.clicks(findViewById(R.id.btn_camera))
//                // Ask for permissions when button is clicked
//                .compose(rxPermissions.ensureEach(Manifest.permission.CAMERA))
//                .subscribeBy (
//                        onNext = {
//                            if(it.granted)
//                            {
//                                toast("it granted");
//                            }else if(it.shouldShowRequestPermissionRationale)
//                            {
//                                 toast("shouldShowRequestPermissionRationale");
//                            }else
//                            {
//                                 toast("can enable the camera")
//                            }
//                        }
//                )
    }



}