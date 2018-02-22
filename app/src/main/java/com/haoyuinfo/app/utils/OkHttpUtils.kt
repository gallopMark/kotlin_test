package com.haoyuinfo.app.utils

import android.content.Context
import android.util.Log
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.Gson
import com.google.gson.internal.`$Gson$Types`
import com.haoyuinfo.app.base.CompatApplication
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okio.*
import java.io.File
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.URLConnection
import java.util.*
import java.util.concurrent.TimeUnit


class OkHttpUtils private constructor() {
    private val mOkHttpClient: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(20, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(20, TimeUnit.SECONDS)//设置连接超时时间
            .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(CompatApplication.instance())))
            .build()
    private val mGson: Gson = Gson()

    /*同步get请求，返回json字符串*/
    @Throws(Exception::class)
    fun getAsJson(context: Context, url: String): String? {
        var response = getResponse(context, url)
        val json = response.body()?.string()
        if (json != null && json.contains(Constants.NOSESSION)) {
            login(context)
            response.close()
            response = getResponse(context, url)
            return response.body()?.string()
        }
        return json
    }

    @Throws(Exception::class)
    fun <T> getAsClass(context: Context, url: String): T {
        var response = getResponse(context, url)
        var json = response.body()?.string()
        if (json != null && json.contains(Constants.NOSESSION)) {
            login(context)
            response.close()
            response = getResponse(context, url)
            json = response.body()?.string()
        }
        return mGson.fromJson(json, getRawType())
    }

    /*异步get请求，返回Disposable*/
    fun <T> getAsync(context: Context, url: String, callback: ResultCallback<T>?): Disposable {
        val request = Request.Builder().url(url).tag(context).addHeader("Accept-Encoding", "gzip").build()
        return deliveryResult(context, request, callback)
    }

    /*同步post请求*/
    @Throws(Exception::class)
    fun postAsJson(context: Context, url: String, params: Map<String, String>): String? {
        var response = postResonse(context, url, params)
        val json = response.body()?.string()
        if (json != null && json.contains(Constants.NOSESSION)) {
            login(context)
            response.close()
            response = postResonse(context, url, params)
            return response.body()?.string()
        }
        return json
    }

    fun <T> postAsClass(context: Context, url: String, params: Map<String, String>): T {
        var response = postResonse(context, url, params)
        var json = response.body()?.string()
        if (json != null && json.contains(Constants.NOSESSION)) {
            login(context)
            response.close()
            response = postResonse(context, url, params)
            json = response.body()?.string()
        }
        return mGson.fromJson(json, getRawType())
    }

    /*异步post请求*/
    fun <T> postAsync(context: Context, url: String, params: Map<String, String>, callback: ResultCallback<T>?): Disposable {
        val request = buildPostRequest(context, url, params)
        return deliveryResult(context, request, callback)
    }

    /*同步文件上传*/
    @Throws(Exception::class)
    fun postAsJson(context: Context, url: String, file: File, listener: ProgressListener): String? {
        return postAsJson(context, url, arrayOf(file), emptyMap(), listener)
    }

    @Throws(Exception::class)
    fun postAsJson(context: Context, url: String, file: File, params: Map<String, String>, listener: ProgressListener): String? {
        return postAsJson(context, url, arrayOf(file), params, listener)
    }

    @Throws(Exception::class)
    fun postAsJson(context: Context, url: String, files: Array<File>, params: Map<String, String>, listener: ProgressListener): String? {
        var response = buildFileResponse(context, url, files, listener, params)
        var json = response.body()?.string()
        if (json != null && json.contains(Constants.NOSESSION)) {
            login(context)
            response.close()
            response = buildFileResponse(context, url, files, listener, params)
            json = response.body()?.string()
        }
        return json
    }

    fun <T> postAsync(context: Context, url: String, file: File, listener: ProgressListener, callback: ResultCallback<T>?): Disposable {
        return postAsync(context, url, arrayOf(file), emptyMap(), listener, callback)
    }

    fun <T> postAsync(context: Context, url: String, file: File, params: Map<String, String>, listener: ProgressListener, callback: ResultCallback<T>?): Disposable {
        return postAsync(context, url, arrayOf(file), params, listener, callback)
    }

    fun <T> postAsync(context: Context, url: String, files: Array<File>, params: Map<String, String>, listener: ProgressListener, callback: ResultCallback<T>?): Disposable {
        val request = buildMultipartFormRequest(context, url, files, listener, params)
        return deliveryResult(context, request, callback)
    }

    @Throws(Exception::class)
    private fun login(context: Context) {
        val map = HashMap<String, String>().apply {
            put("username", PreferenceUtils.getAccount(context))
            put("password", PreferenceUtils.getPassWord(context))
        }
        var response = postResonse(context, Constants.LOGIN_URL, map)
        val url = response.body()?.string()
        url?.let {
            map.clear()
            map["service"] = Constants.SERVICE
            val st = post(context, it, map)
            get(context, "${Constants.SERVICE}?ticket=$st")
        }
    }

    @Throws(Exception::class)
    private fun postResonse(context: Context, url: String, params: Map<String, String>): Response {
        val request = buildPostRequest(context, url, params)
        return mOkHttpClient.newCall(request).execute()
    }

    private fun buildPostRequest(context: Context, url: String, params: Map<String, String>): Request {
        val formEncodingBuilder = FormBody.Builder()
        for (param in params) {
            formEncodingBuilder.add(param.key, param.value)
        }
        val requestBody = formEncodingBuilder.build()
        return Request.Builder().url(url).tag(context).addHeader("Accept-Encoding", "gzip").post(requestBody).build()
    }

    @Throws(Exception::class)
    private fun buildFileResponse(context: Context, url: String, files: Array<File>, progressListener: ProgressListener, params: Map<String, String>?): Response {
        val request = buildMultipartFormRequest(context, url, files, progressListener, params)
        return mOkHttpClient.newCall(request).execute()
    }

    private fun buildMultipartFormRequest(context: Context, url: String, files: Array<File>?, listener: ProgressListener, params: Map<String, String>?): Request {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        params?.let {
            for (param in it) {
                builder.addFormDataPart(param.key, param.value)
            }
        }
        files?.let {
            var fileBody: RequestBody
            for (i in files.indices) {
                val file = files[i]
                val fileName = file.name
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file)
                builder.addFormDataPart("file", file.name, ProgressRequestBody(fileBody, file, listener))
            }
        }
        val requestBody = builder.build()
        return Request.Builder().url(url).tag(context).addHeader("Accept-Encoding", "gzip").post(requestBody).build()
    }

    private fun guessMimeType(path: String): String {
        val fileNameMap = URLConnection.getFileNameMap()
        val contentTypeFor = fileNameMap.getContentTypeFor(path)
        return contentTypeFor ?: "application/octet-stream"
    }

    @Throws(Exception::class)
    private fun getResponse(context: Context, url: String): Response {
        val request = Request.Builder().url(url).tag(context).addHeader("Accept-Encoding", "gzip").build()
        return mOkHttpClient.newCall(request).execute()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> deliveryResult(context: Context, request: Request, callback: ResultCallback<T>?): Disposable {
        callback?.onBefore(request)
        return Flowable.fromCallable {
            var response = mOkHttpClient.newCall(request).execute()
            var json = response.body()?.string()
            if (json != null && json.contains(Constants.NOSESSION)) {
                login(context)
                response.close()
                response = mOkHttpClient.newCall(request).execute()
                json = response.body()?.string()
            }
            Log.e("json", json)
            json
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ json ->
            callback?.let {
                if (it.mType == String::class.java) it.onResponse(json as T)
                else it.onResponse(mGson.fromJson(json, it.mType))
            }
        }, {
            it.printStackTrace()
            callback?.onError(request, it)
        })
    }

    abstract class ResultCallback<in T> {
        var mType: Type? = null

        init {
            try {
                val superclass = javaClass.genericSuperclass
                val parameterized = superclass as ParameterizedType
                mType = `$Gson$Types`.canonicalize(parameterized.actualTypeArguments[0])
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        open fun onBefore(request: Request) {}
        abstract fun onError(request: Request, e: Throwable)
        abstract fun onResponse(response: T?)
    }

    interface ProgressListener {
        fun onProgress(totalBytes: Long, remainingBytes: Long, done: Boolean, file: File)
    }

    class ProgressRequestBody(private val requestBody: RequestBody, private val file: File, private val listener: ProgressListener?) : RequestBody() {
        private var bufferedSink: BufferedSink? = null

        override fun contentType(): MediaType? {
            return requestBody.contentType()
        }

        @Throws(IOException::class)
        override fun contentLength(): Long {
            return requestBody.contentLength()
        }

        override fun writeTo(sink: BufferedSink) {
            if (bufferedSink == null) {
                bufferedSink = Okio.buffer(sink(sink)) //包装
            }
            bufferedSink?.let {
                requestBody.writeTo(it)
                it.flush()
            }
        }

        private fun sink(sink: Sink): Sink {
            return object : ForwardingSink(sink) {
                //当前写入字节数
                private var bytesWritten = 0L
                //总字节长度，避免多次调用contentLength()方法
                private var contentLength = 0L

                @Throws(IOException::class)
                override fun write(source: Buffer, byteCount: Long) {
                    super.write(source, byteCount)
                    if (contentLength == 0L) {
                        //获得contentLength的值，后续不再调用
                        contentLength = contentLength()
                    }
                    //增加当前写入的字节数
                    bytesWritten += byteCount
                    listener?.onProgress(contentLength, contentLength - bytesWritten, bytesWritten == contentLength, file)
                }
            }
        }
    }

    /**************对外公布的方法*************/
    companion object {
        @Volatile
        private var mInstance: OkHttpUtils? = null
            get() {
                if (field == null) {
                    synchronized(OkHttpUtils::class.java) {
                        if (field == null) {
                            field = OkHttpUtils()
                        }
                    }
                }
                return field
            }

        fun getResponse(context: Context, url: String): Response? = mInstance?.getResponse(context, url)
        fun get(context: Context, url: String): String? = mInstance?.getAsJson(context, url)

        fun <T> getAsync(context: Context, url: String, callback: ResultCallback<T>): Disposable? = mInstance?.getAsync(context, url, callback)

        fun postResponse(context: Context, url: String, params: Map<String, String>): Response? = mInstance?.postResonse(context, url, params)

        fun post(context: Context, url: String, params: Map<String, String>): String? = mInstance?.postAsJson(context, url, params)

        fun <T> postAsync(context: Context, url: String, params: Map<String, String>, callback: ResultCallback<T>): Disposable? = mInstance?.postAsync(context, url, params, callback)

        fun post(context: Context, url: String, file: File, listener: ProgressListener): String? = mInstance?.postAsJson(context, url, file, listener)

        fun post(context: Context, url: String, file: File, params: Map<String, String>, listener: ProgressListener): String? = mInstance?.postAsJson(context, url, file, params, listener)

        fun post(context: Context, url: String, files: Array<File>, params: Map<String, String>, listener: ProgressListener): String? = mInstance?.postAsJson(context, url, files, params, listener)

        fun <T> postAsync(context: Context, url: String, file: File, listener: ProgressListener, callback: ResultCallback<T>): Disposable? = mInstance?.postAsync(context, url, file, listener, callback)

        fun <T> postAsync(context: Context, url: String, file: File, params: Map<String, String>, listener: ProgressListener, callback: ResultCallback<T>): Disposable? = mInstance?.postAsync(context, url, file, params, listener, callback)

        fun <T> postAsync(context: Context, url: String, files: Array<File>, params: Map<String, String>, listener: ProgressListener, callback: ResultCallback<T>): Disposable? = mInstance?.postAsync(context, url, files, params, listener, callback)
    }

    private fun getRawType(): Type? {
        try {
            val superclass = javaClass.genericSuperclass
            val parameterized = superclass as ParameterizedType
            return `$Gson$Types`.canonicalize(parameterized.actualTypeArguments[0])
        } catch (e: Exception) {
            return null
        }
    }
}