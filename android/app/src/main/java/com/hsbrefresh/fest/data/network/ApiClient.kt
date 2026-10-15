package com.hsbrefresh.fest.data.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var cachedApiService: HsbRefreshApiService? = null
    private var cachedBaseUrl: String? = null

    fun resetClient() {
        cachedApiService = null
        cachedBaseUrl = null
    }

    fun getApiService(context: Context): HsbRefreshApiService {
        val currentBaseUrl = ApiConfig.getBaseUrl(context)

        if (cachedApiService != null && cachedBaseUrl == currentBaseUrl) {
            return cachedApiService!!
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(HsbRefreshApiService::class.java)
        cachedApiService = service
        cachedBaseUrl = currentBaseUrl

        return service
    }
}
