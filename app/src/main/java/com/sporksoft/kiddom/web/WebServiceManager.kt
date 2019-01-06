package com.sporksoft.kiddom.web

import com.sporksoft.kiddom.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object WebServiceManager {
    const val BASE_URL = "https://www.headlightlabs.com"
    private var webService: WebService? = null

    private fun buildMoshi(): Moshi {
        return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    private fun buildOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        builder.readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            builder.addInterceptor(interceptor)
        }

        return builder.build()
    }

    fun instanceOf(): WebService? {
        if (webService == null) {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(buildOkHttpClient())
                    .addConverterFactory(MoshiConverterFactory.create(buildMoshi()))
                    .build()

            webService = retrofit.create(WebService::class.java)
        }

        return webService
    }

    fun releaseWebService() {
        webService = null
    }
}