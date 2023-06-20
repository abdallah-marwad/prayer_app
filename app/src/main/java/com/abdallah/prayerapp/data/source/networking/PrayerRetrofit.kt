package com.abdallah.prayerapp.data.source.networking

import com.abdallah.prayerapp.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PrayerRetrofit {

    companion object {
        private  var retrofit: Retrofit? = null
        private  var prayerApi: PrayerApi? = null
        private fun getConnection(): Retrofit {
            if (retrofit == null) {
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }

        fun getInstance(): PrayerApi {
            if (prayerApi == null){
                getConnection().create(PrayerApi::class.java)
            }
            return prayerApi!!
        }

    }
}