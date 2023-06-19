package com.abdallah.prayerapp.data.networking

import com.abdallah.prayerapp.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PrayerRetrofit {

    companion object {
        private  var retrofit: Retrofit? = null
        fun getInstance(): Retrofit {
            if (retrofit == null) {
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }

//        val api by lazy {
//            retrofit.create(NewsApi::class.java)
//        }

    }
}