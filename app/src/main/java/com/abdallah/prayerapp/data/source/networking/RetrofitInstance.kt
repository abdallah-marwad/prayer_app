package com.abdallah.prayerapp.data.source.networking

import com.abdallah.prayerapp.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitInstance {

    companion object {
        private  var retrofit: Retrofit? = null
        private  var apiServices: ApiServices? = null
        private fun client(): OkHttpClient {
            return OkHttpClient().newBuilder().callTimeout(25, TimeUnit.SECONDS)
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(12, TimeUnit.SECONDS).retryOnConnectionFailure(false)
                .build()
        }
        private fun getConnection(baseUrl : String): Retrofit {

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }

        fun getInstance(baseUrl : String): ApiServices {
            if (apiServices == null){
                apiServices =getConnection(baseUrl).create(ApiServices::class.java)
            }
            return apiServices!!
        }

    }
}