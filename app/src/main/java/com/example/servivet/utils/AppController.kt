package com.example.servivet.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleObserver
import com.orhanobut.hawk.Hawk

class AppController:Application(),LifecycleObserver{
    lateinit var instance: AppController
    override fun onCreate() {
        super.onCreate()
        Hawk.init(applicationContext).build()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        instance = this
    }
}