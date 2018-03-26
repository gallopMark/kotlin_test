package com.haoyuinfo.app.utils

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.haoyuinfo.app.base.BaseResult
import com.haoyuinfo.app.base.CompatApplication
import com.haoyuinfo.app.entity.MobileUser
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


/**
 * 创建日期：2018/3/19.
 * 描述:Retofit网络请求工具类
 * 作者:xiaoma
 */
class RetrofitUtils private constructor() {
    private val retrofit: Retrofit
    private val service: AppService
    private val BASE_URL = "http://neancts.gdei.edu.cn/app/"

    init {
        val mOkHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(20, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(20, TimeUnit.SECONDS)//设置连接超时时间
                .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(CompatApplication.instance())))
                .build()
        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        service = retrofit.create(AppService::class.java)
    }

    companion object {
        @Volatile
        private var mInstance: RetrofitUtils? = null

        fun getInstance(): RetrofitUtils {
            if (mInstance == null) {
                synchronized(RetrofitUtils::class.java) {
                    if (mInstance == null) {
                        mInstance = RetrofitUtils()
                    }
                }
            }
            return mInstance!!
        }
    }

    fun login(username: String, password: String): Flowable<BaseResult<MobileUser>>? {
        return Flowable.just(Constants.LOGIN_URL).flatMap {
            service.getTgt(it, username, password)
        }.flatMap {
            service.getSt(it, Constants.SERVICE)
        }.map {
            service.login("${Constants.SERVICE}?ticket=$it")
        }
    }
}