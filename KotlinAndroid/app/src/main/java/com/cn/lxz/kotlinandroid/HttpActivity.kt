package com.cn.lxz.kotlinandroid

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.gson.Gson
import io.reactivex.Observable

import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_http.*
import okhttp3.Response
import org.lxz.utils.http.*
import retrofit2.http.GET
import retrofit2.http.Query
import com.lxz.kotlin.tools.lang.asJsonFromat
import com.lxz.kotlin.tools.android.idsOnClick
import com.lxz.kotlin.tools.android.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import okhttp3.Cache
import java.io.File

/**
 * Created by linxingzhu on 2017/11/7.
 */
class HttpActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View?) {
       when(v?.id){
           R.id.btn_submit->request();
       }

    }
    private lateinit var api: Api
    fun request(){
        tv_msg.text="";
        api.request("上海")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy (
                        onNext = {
                            tv_msg.toast("请求成功")
                        },
                        onError =  {
                            tv_msg.toast("请求失败"+it)
                        }
                );
    }

    data class ErrorStatus(val status: Int,val message: String)
    private fun iniApi(){
        //http://www.sojson.com/open/api/weather/json.shtml?city=北京
        var factory = AsHttpFactory.Build()
                .setUrl("http://www.sojson.com")
                .setJsonPath(null)
                .setConnectTimeout(4000)
                .setReadTimeout(4000)
                /**从jsonPath根节点开始解析 $.data 表示从数据的data路径开始解析*/
//              .setJsonPath("$.")
                .setShowLog(true)
                .setHttpLog(AsHttpFactory.LogListen { log ->
                    tv_msg.post {
                        tv_msg.append("==========网络请求日志=======\n")
                        tv_msg.append(log.message.toString().asJsonFromat());
                        tv_msg.append("==========请求结果==========\n")
                    }
                })
                .setClientBuild(AsHttpFactory.ClientBuilder {
                    build ->
                        //cache的作用是指定缓存文件地址、大小
                        build.cache(Cache(File( application.getExternalCacheDir(),"test_cache"),10 * 1024 * 1024));

                })
//                //有网无网全局缓存策略 见http://www.jianshu.com/p/5cd7e95c2f29
//                .addInterceptor(Interceptor {
//                    chain ->
//                     var request=chain.request();
//                     var maxAge = 10;
//                       if (isNetworkConnected(application)) {
//                        var response:Response = chain.proceed(request);
//                        // read from cache for 0 s  有网络不会使用缓存数据
//                        var cacheControl = request.cacheControl().toString();
//                           response.newBuilder()
//                                .removeHeader("Pragma")
//                                .removeHeader("Cache-Control")
//                                .header("Cache-Control", "public, max-age=" + maxAge)
//                                .build();
//                    } else {
//                        //无网络时强制使用缓存数据
//                        request.newBuilder()
//                                .cacheControl(CacheControl.FORCE_CACHE)
//                                .build();
//                           var response:Response = chain.proceed(request);
//                           var maxStale = 60 * 60 * 24 * 3;
//                          response.newBuilder()
//                                   .removeHeader("Pragma")
//                                   .removeHeader("Cache-Control")
//                                   .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
//                                   .build();
//
//
//                    }
//
//                })
                .setClientLogic(object:DefaultInterceptor(application){
                    //httpcode过滤器
                    override fun httpCodeMessage(code: Int): String {
                        return super.httpCodeMessage(code)
                    }

                    //异常过滤器
                    override fun handlerThrowable(e: Throwable?): HttpException {
                        return super.handlerThrowable(e)
                    }



                    var gson: Gson =Gson();
                    //请求结果过滤 定义status>200抛出请求异常
                    override fun interceptor(response: Response?, json: String?): String {
                        var errorBean = gson.fromJson(json, ErrorStatus::class.java)
                        if(errorBean.status!=200)throw HttpException(errorBean.status,errorBean.message);
                        return super.interceptor(response, json)
                    }
                })
                .build();
        api = factory.retrofit.create(Api::class.java);
    }



@SuppressLint("MissingPermission")
    fun isNetworkConnected(context:Context):Boolean{
           var mConnectivityManager:ConnectivityManager = context
                   .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;
           var mNetworkInfo = mConnectivityManager.activeNetworkInfo;
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        return false;
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_http);
        idsOnClick(this,R.id.btn_submit)
        iniApi();
    }

    interface Api {
        @GET("/open/api/weather/json.shtml")
        fun request(@Query("city") city:String): Observable<WeatherBean>
    }


    data class WeatherBean(
            val date: String, //20171107
            val message: String, //Success !
            val status: Int, //200
            val city: String, //北京
            val count: Int, //2
            val data: Data
    )

    data class Data(
            val shidu: String, //85%
            val pm25: Double, //167.0
            val pm10: Double, //185.0
            val quality: String, //重度污染
            val wendu: String, //6
            val ganmao: String, //老年人及心脏、呼吸系统疾病患者人群应停留在室内，停止户外运动，一般人群减少户外运动
            val yesterday: Yesterday,
            val forecast: List<Forecast>
    )

    data class Yesterday(
            val date: String, //06日星期一
            val sunrise: String, //06:47
            val high: String, //高温 15.0℃
            val low: String, //低温 4.0℃
            val sunset: String, //17:08
            val aqi: Double, //208.0
            val fx: String, //北风
            val fl: String, //<3级
            val type: String, //多云
            val notice: String //绵绵的云朵，形状千变万化
    )

    data class Forecast(
            val date: String, //07日星期二
            val sunrise: String, //06:48
            val high: String, //高温 18.0℃
            val low: String, //低温 4.0℃
            val sunset: String, //17:07
            val aqi: Double, //138.0
            val fx: String, //西北风
            val fl: String, //3-4级
            val type: String, //晴
            val notice: String //晴空万里，去沐浴阳光吧
    )



}