package com.example.servivet.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

object Preferences {

        private  val TAG = "tag"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
    }

        fun setStringPreference(context: Context, key: String?, value: String) {
            val settings: SharedPreferences = getSharedPreferences(context)
            val editor = settings.edit()
            editor.putString(key, value)
            editor.apply()
        }

    fun setBooleanPreference(context: Context, key: String?, value: Boolean) {
        val settings: SharedPreferences = getSharedPreferences(context)
        val editor = settings.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun setIntegerPreference(context: Context, key: String?, value: Int) {
        val settings: SharedPreferences = getSharedPreferences(context)
        val editor = settings.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getStringPreference(context: Context, key: String?): String? {
        val prefs: SharedPreferences = getSharedPreferences(context)
        return prefs.getString(key, "")
    }

    fun getBooleanPreference(context: Context, key: String?): Boolean {
        val prefs: SharedPreferences = getSharedPreferences(context)
        return prefs.getBoolean(key, false)
    }

    fun getIntegerPreference(context: Context, key: String?): Int {
        val prefs: SharedPreferences = getSharedPreferences(context)
        return prefs.getInt(key, -1)
    }

    fun getAllPreference(context: Context): String {
        val settings: SharedPreferences = getSharedPreferences(context)
        val editor = settings.all
        var text = ""
        try {
            for ((key, value1) in editor) {
                val value = value1!!
// do stuff
                text += "\t$key = $value\t"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return text
    }

    fun removePreference(context: Context, key: String?) {
        val settings: SharedPreferences = getSharedPreferences(context)
        val editor = settings.edit()
        editor.remove(key)
        editor.apply()
    }

    fun removeAllPreference(context: Context) {
        val settings: SharedPreferences = getSharedPreferences(context)
        val editor = settings.edit()
        editor.clear()
        editor.apply()
    }

    fun setPreference(context: Context?, key: String?, `val`: Any?) {
        val settings: SharedPreferences? =
            context?.let { getSharedPreferences(it) }
        val editor = settings!!.edit()
        val gson = Gson()
        val json = gson.toJson(`val`)
        editor.putString(key, json)
        editor.apply()
    }

        fun getPreference(context: Context?, key: String?): String? {
            val prefs: SharedPreferences? =
                context?.let { getSharedPreferences(it) }
            return prefs!!.getString(key, "")
        }

}