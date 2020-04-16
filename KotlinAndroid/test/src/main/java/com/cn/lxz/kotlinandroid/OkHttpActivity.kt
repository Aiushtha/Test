package com.cn.lxz.kotlinandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.lxz.kotlin.tools.android.toast
import com.lxz.kotlin.tools.lang.asJsonFromat
import com.lxz.kotlin.tools.android.idsOnClick
import com.lxz.kotlin.tools.http.*
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_okhttp.*
import okhttp3.OkHttpClient
import org.lxz.utils.log.D

/**
 * Created by linxingzhu on 2017/11/28.
 */
class OkHttpActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_submit -> request();
        }
    }

    fun request() {
//        var factory: AsHttpFactory = AsHttpFactory.Build()
//                .setHttpLog(AsHttpFactory.LogListen { log ->
//                    tv_msg.post {
//                        tv_msg.append("==========网络请求日志=======\n")
//                        tv_msg.append(log.message.toString().asJsonFromat());
//                        tv_msg.append("==========请求结果==========\n")
//                    }
//                })
//                .build();
//
//        initOkHttpClient(factory.okHttpClient)


//        "https://api.douban.com/v2/movie/top250?start1&count=2"
//                .toRxObserable(Data::class.java)
//                .subscribeBy(
//                        onNext = {
//                            tv_msg.toast("请求成功: data.title= ${it.title}+data.count=${it.count}")
//                        },
//                        onError = {
//                            tv_msg.toast("请求失败" + it)
//                        }
//                )

        OkHttpClient()
                .http("https://api.douban.com/v2/movie/top250?start1&count=2")
                .setHead(mapOf("1" to "2"))
                .setBody(mapOf( "1" to "2"))
                .setMultBodyBuild{}
                .toObservable<String>(String.javaClass)
                .subscribeBy {

                }




//        OkHttpClient().http<ShareActivity.User>(url = "")
//                .asJsonFromat()


//
//        "https://api.douban.com/v2/movie/top250"
//        .bodyForm(
//                type = Data::class.java
//
//        ).subscribeBy(
//                onNext = {
//                    tv_msg.setText(it.asJsonFromat())
//                },
//                onError = {
//                    tv_msg.toast("请求失败")
//                }
//        )
//        "https://api.douban.com/v2/movie/top250"
//                .bodyForm(
//                        type = Data::class.java
//
//                ).subscribeBy(
//                onNext = {
//                    tv_msg.setText(it.asJsonFromat())
//                },
//                onError = {
//                    tv_msg.toast("请求失败")
//                }
//        )
////
//
//        "https://api.douban.com/v2/movie/top250"
//                .multipartForm<Data>(
//
//                        type = Data::class.java,
//
//                        body =  {
//                        }
//                ).subscribeBy(
//                onNext = {
//                    tv_msg.toast("请求成功: data.title= ${it.title}")
//                },
//                onError = {
//                    tv_msg.toast("请求失败" + it)
//                }
//        )
//        "https://api.douban.com/v2/movie/top250"
//                .simpleMultipartForm<Data>(
//                       type = Data::class.java,
//                       head =  mapOf(),
//                       body =  mapOf("key" to 24,"name" to "zhangsan","age" to 25)
//                ).subscribeBy(
//                onNext = {
//                    tv_msg.toast("请求成功: data.title= ${it.title}")
//                },
//                onError = {
//                    tv_msg.toast("请求失败" + it)
//                }
//        )

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_okhttp)
        idsOnClick(this, R.id.btn_submit)
        D.init(this)

    }


    data class Data(
            val count: Int, //20
            val start: Int, //0
            val total: Int, //250
            val subjects: List<Subject>,
            val title: String //豆瓣电影Top250
    )

    data class Subject(
            val rating: Rating,
            val genres: List<String>,
            val title: String, //肖申克的救赎
            val casts: List<Cast>,
            val collect_count: Int, //1160226
            val original_title: String, //The Shawshank Redemption
            val subtype: String, //movie
            val directors: List<Director>,
            val year: String, //1994
            val images: Images,
            val alt: String, //https://movie.douban.com/subject/1292052/
            val id: String //1292052
    )

    data class Images(
            val small: String, //https://img3.doubanio.com/view/photo/s_ratio_poster/public/p480747492.jpg
            val large: String, //https://img3.doubanio.com/view/photo/s_ratio_poster/public/p480747492.jpg
            val medium: String //https://img3.doubanio.com/view/photo/s_ratio_poster/public/p480747492.jpg
    )

    data class Director(
            val alt: String, //https://movie.douban.com/celebrity/1047973/
            val avatars: Avatars,
            val name: String, //弗兰克·德拉邦特
            val id: String //1047973
    )

    data class Avatars(
            val small: String, //https://img3.doubanio.com/view/celebrity/s_ratio_celebrity/public/p230.jpg
            val large: String, //https://img3.doubanio.com/view/celebrity/s_ratio_celebrity/public/p230.jpg
            val medium: String //https://img3.doubanio.com/view/celebrity/s_ratio_celebrity/public/p230.jpg
    )

    data class Rating(
            val max: Int, //10
            val average: Double, //9.6
            val stars: String, //50
            val min: Int //0
    )

    data class Cast(
            val alt: String, //https://movie.douban.com/celebrity/1054521/
            val avatars: Avatars,
            val name: String, //蒂姆·罗宾斯
            val id: String //1054521
    )

}