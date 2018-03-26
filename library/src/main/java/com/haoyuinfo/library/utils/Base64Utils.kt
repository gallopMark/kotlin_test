package com.haoyuinfo.library.utils

import android.util.Base64


object Base64Utils {
    /**
     * Base64加密
     * @param str
     * @return
     */
    fun encode(str: String): String {
        val encodeBytes = Base64.encode(str.toByteArray(), Base64.DEFAULT)
        return String(encodeBytes)
    }

    /**
     * Base64解密
     * @param str
     * @return
     */
    fun decode(str: String): String {
        val decodeBytes = Base64.decode(str.toByteArray(), Base64.DEFAULT)
        return String(decodeBytes)
    }
}