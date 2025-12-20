package com.example.cafebook.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.cafebook.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 網路連接状态工具类
 * 支持实时监听網路状态变化
 */
object NetworkUtils {
    private var connectivityManager: ConnectivityManager? = null

    // 網路状态流，可以观察網路状态变化
    private val _networkState = MutableStateFlow(false)
    val networkState: StateFlow<Boolean> = _networkState

    /**
     * 初始化工具类
     */
    fun initialize(context: Context) {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        updateNetworkState(context)
        registerNetworkCallback(context)
    }

    /**
     * 检查是否連接網路
     */
    fun isNetworkConnected(context: Context): Boolean {
        return try {
            val cm = connectivityManager ?: context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val network = cm.activeNetwork
            val capabilities = cm.getNetworkCapabilities(network)
            capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 检查是否是 WiFi 連接
     */
    fun isWifiConnected(context: Context): Boolean {
        return try {
            val cm = connectivityManager ?: context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val network = cm.activeNetwork
            val capabilities = cm.getNetworkCapabilities(network)
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 检查是否是移动数据連接
     */
    fun isMobileDataConnected(context: Context): Boolean {
        return try {
            val cm = connectivityManager ?: context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val network = cm.activeNetwork
            val capabilities = cm.getNetworkCapabilities(network)
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取網路类型
     */
    fun getNetworkType(context: Context): String {
        return when {
            isWifiConnected(context) -> context.getString(R.string.network_type_wifi)
            isMobileDataConnected(context) -> context.getString(R.string.network_type_mobile)
            else -> context.getString(R.string.network_type_no_network)
        }
    }

    /**
     * 注册網路状态监听
     */
    private fun registerNetworkCallback(context: Context) {
        val cm = connectivityManager ?: return

        val networkRequest =
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .build()

        cm.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _networkState.value = true
                }

                override fun onLost(network: Network) {
                    _networkState.value = false
                }

                override fun onUnavailable() {
                    _networkState.value = false
                }
            },
        )
    }

    /**
     * 更新網路状态
     */
    private fun updateNetworkState(context: Context) {
        _networkState.value = isNetworkConnected(context)
    }
}
