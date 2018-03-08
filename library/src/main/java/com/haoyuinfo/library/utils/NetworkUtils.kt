package com.haoyuinfo.library.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import android.telephony.TelephonyManager


object NetworkUtils {

    const val NETWORK_WIFI = 1    // wifi network
    const val NETWORK_4G = 4    // "4G" networks
    const val NETWORK_3G = 3    // "3G" networks
    const val NETWORK_2G = 2    // "2G" networks
    const val NETWORK_UNKNOWN = 5    // unknown network
    const val NETWORK_NO = -1   // no network

    /*打开网络设置界面 */
    fun openWirelessSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
    }

    private fun getActiveNetworkInfo(context: Context): NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    /*判断网络是否可用*/
    fun isAvailable(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && info.isAvailable
    }

    /*判断网络是否连接*/
    fun isConnected(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && info.isConnected
    }

    /*判断wifi是否连接状态 */
    fun isWifiConnected(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info?.type == ConnectivityManager.TYPE_WIFI
    }

    /*判断网络类型是否是移动网络*/
    fun isMobileConnected(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info?.type == ConnectivityManager.TYPE_MOBILE
    }

    /*获取当前的网络类型*/
    fun getNetWorkType(context: Context): Int {
        val info = getActiveNetworkInfo(context)
        if (info != null && info.isAvailable) {
            if (info.type == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_WIFI
            } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                when (info.subtype) {
                    TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_CDMA,
                    TelephonyManager.NETWORK_TYPE_EDGE,
                    TelephonyManager.NETWORK_TYPE_1xRTT,
                    TelephonyManager.NETWORK_TYPE_IDEN -> return NETWORK_2G
                    TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_UMTS,
                    TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_HSDPA,
                    TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_HSPA,
                    TelephonyManager.NETWORK_TYPE_EVDO_B,
                    TelephonyManager.NETWORK_TYPE_EHRPD,
                    TelephonyManager.NETWORK_TYPE_HSPAP -> return NETWORK_3G
                    TelephonyManager.NETWORK_TYPE_LTE -> return NETWORK_4G
                    else -> {
                        val subtypeName = info.subtypeName
                        if (subtypeName.equals("TD-SCDMA", true)
                                || subtypeName.equals("WCDMA", true)
                                || subtypeName.equals("CDMA2000", true)) {
                            return NETWORK_3G
                        } else {
                            return NETWORK_UNKNOWN
                        }
                    }
                }
            }
        }
        return NETWORK_UNKNOWN
    }
}