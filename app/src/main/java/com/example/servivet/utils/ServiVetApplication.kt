package com.example.servivet.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.orhanobut.hawk.Hawk

class ServiVetApplication:Application() {
    lateinit var instance: ServiVetApplication
    override fun onCreate() {
        super.onCreate()
        Hawk.init(applicationContext).build()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        instance = this
    }
}