package com.haoyuinfo.app.utils

import com.haoyuinfo.app.base.BaseResult
import com.haoyuinfo.app.entity.MobileUser
import io.reactivex.Flowable
import retrofit2.http.*

interface AppService {
    @FormUrlEncoded
    @POST
    fun getTgt(@Url url: String, @Field("username") userName: String, @Field("password") password: String): Flowable<String>

    @FormUrlEncoded
    @POST
    fun getSt(@Url url: String, @Field("service") service: String): Flowable<String>

    @GET()
    fun login(@Url url: String): BaseResult<MobileUser>
}