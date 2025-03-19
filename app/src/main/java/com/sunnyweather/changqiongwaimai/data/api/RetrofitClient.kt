package com.sunnyweather.changqiongwaimai.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://47.105.36.245:8080/user/" // 替换成你的API基地址

    //添加拦截器便于调试
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    //添加请求头
    private val headerInterceptor = Interceptor {chain ->
        val originalRequest : Request = chain.request()
        val newRequest = originalRequest.newBuilder()
            .addHeader("authentication","eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjEwMDAwMDE3Mzk3MTIwNzEsInVzZXJJZCI6NH0.tcq3QOhS7yEK6GtSf9XUKhQSVGD0O9fCzFrw42yeMp0")
            .build()
        chain.proceed(newRequest)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()


    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}