package com.legendx.batteryschedule.helpers

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object DataManage {
    private lateinit var sharedPref: SharedPreferences
    private const val PREFS_NAME = "BatterySchedule"
    private const val PREFS_MODE = MODE_PRIVATE

    fun initialize(context: Context){
       sharedPref = context.getSharedPreferences(PREFS_NAME, PREFS_MODE)
    }

    fun saveData(key: String, value: String): Boolean{
        return try{
            sharedPref.edit().putString(key, value).apply()
            true
        }catch (e: Exception){
            false
        }
    }

    fun getData(key: String): String?{
        return sharedPref.getString(key, null)
    }

    fun removeData(key: String): Boolean{
        return try{
            println(key)
            sharedPref.edit().remove(key).apply()
            true
        }catch (e: Exception){
            false
        }
    }


}