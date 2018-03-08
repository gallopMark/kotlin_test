package com.haoyuinfo.library.utils

import android.content.Context
import android.graphics.*
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.io.File

object GlideUtils {
    fun loadCircleImage(context: Context, url: String?, emptyImg: Int, erroImg: Int, iv: ImageView) {
        //原生 API
        Glide.with(context).load(url).placeholder(emptyImg).error(erroImg).dontAnimate()
                .centerCrop().transform(GlideCircleTransform(context)).into(iv)
    }

    fun loadCircleImage(context: Context, file: File, emptyImg: Int, erroImg: Int, iv: ImageView) {
        //原生 API
        Glide.with(context).load(file).placeholder(emptyImg).error(erroImg).dontAnimate()
                .centerCrop().transform(GlideCircleTransform(context)).into(iv)
    }

    fun loadImage(context: Context, url: String?, emptyImg: Int, erroImg: Int, iv: ImageView) {
        //原生 API
        Glide.with(context).load(url).placeholder(emptyImg).error(erroImg).centerCrop().dontAnimate().into(iv)
    }

    fun loadGifImage(context: Context, url: String?, emptyImg: Int, erroImg: Int, iv: ImageView) {
        Glide.with(context).load(url).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(emptyImg).error(erroImg).into(iv)
    }

    fun loadImage(context: Context, file: File, imageView: ImageView) {
        Glide.with(context)
                .load(file)
                .into(imageView)
    }

    fun loadImage(context: Context, resourceId: Int, imageView: ImageView) {
        Glide.with(context)
                .load(resourceId)
                .into(imageView)
    }

    private class GlideCircleTransform(context: Context) : BitmapTransformation(context) {
        override fun getId(): String {
            return javaClass.name
        }

        override fun transform(pool: BitmapPool?, toTransform: Bitmap?, outWidth: Int, outHeight: Int): Bitmap? {
            return circleCrop(pool, toTransform)
        }

        private fun circleCrop(pool: BitmapPool?, source: Bitmap?): Bitmap? {
            if (source == null) return null
            val size = Math.min(source.width, source.height)
            val x = (source.width - size) / 2
            val y = (source.height - size) / 2
            var squared: Bitmap? = null
            var result: Bitmap? = null
            try {
                squared = Bitmap.createBitmap(source, x, y, size, size)
                result = pool?.get(size, size, Bitmap.Config.ARGB_8888)
                if (result == null) {
                    result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
                }
                val canvas = Canvas(result)
                val paint = Paint()
                paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                paint.isAntiAlias = true
                val r = size / 2f
                canvas.drawCircle(r, r, r, paint)
                return result
            } catch (e: OutOfMemoryError) {
                squared?.recycle()
                result?.recycle()
                return null
            }
        }
    }
}