package com.example.donorproject

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val api: ApiService by lazy {
        Retrofit.Builder().baseUrl("https://platform.fatsecret.com/").addConverterFactory(
            GsonConverterFactory.create()).build().create(ApiService::class.java)
    }
}