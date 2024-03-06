package com.preachooda.assets.util

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthJwtInterceptor(
    private val obtainToken: () -> String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        Log.d("AuthJwtInterceptor", "obtainToken: ${obtainToken()}")
        proceed(
            request().newBuilder()
                .addHeader("Authorization", "Bearer ${obtainToken()}")
                .build()
        )
    }
}
