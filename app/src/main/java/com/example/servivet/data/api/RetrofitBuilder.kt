package com.example.servivet.data.api

import android.provider.Settings.Secure
import com.example.servivet.utils.Constants
import com.example.servivet.utils.Constants.SECURE_HEADER
import com.example.servivet.utils.PreferenceEntity.TOKEN
import com.orhanobut.hawk.Hawk
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object  RetrofitBuilder
{
    private const val DEVELOPMENT_BASE_URL="http://13.235.137.221:3476/mobileApi/"


    private fun getRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(DEVELOPMENT_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build() //Doesn't require the adapter
}

    private val httpClient: OkHttpClient =
        OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(Interceptor { chain ->
                val ongoing: Request.Builder = chain.request().newBuilder()
                ongoing.addHeader("Accept", "application/json")
                ongoing.addHeader("Content-Type", "application/json")
                ongoing.addHeader("accept-language", "en")
                //add header
                Hawk.get<String>(TOKEN, null)?.let {
                    ongoing.addHeader("Authorization", it)
                }
                if(SECURE_HEADER.isNotEmpty()){
                    ongoing.addHeader("requestfor", SECURE_HEADER)
                  //  SECURE_HEADER = " "
                }
                chain.proceed(ongoing.build())
            }).addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()


    val apiService: ApiService = getRetrofit().create(ApiService::class.java)

}
