package com.truyencv.app.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class MotTruyenDefaultParamsInterceptor(
    private val deviceId: String = "antigravity",
    private val os: String = "android",
    private val appVersion: String = "1.0.6"
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url

        val urlBuilder = originalHttpUrl.newBuilder()
        if (originalHttpUrl.queryParameter("deviceid") == null) {
            urlBuilder.addQueryParameter("deviceid", deviceId)
        }
        if (originalHttpUrl.queryParameter("os") == null) {
            urlBuilder.addQueryParameter("os", os)
        }
        if (originalHttpUrl.queryParameter("app_version") == null) {
            urlBuilder.addQueryParameter("app_version", appVersion)
        }

        val request = original.newBuilder()
            .url(urlBuilder.build())
            .build()

        return chain.proceed(request)
    }
}
