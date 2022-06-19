package com.shunyank.zerocard.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
class SharedPref(val context: Context) {
    fun saveUserId(id:String){
       val sharedPref =  context.getSharedPreferences(UserSharePrefName,MODE_PRIVATE)
        sharedPref.edit().putString("user_id",id).commit()
    }
    fun getUserId():String{
        val userId =  context.getSharedPreferences(UserSharePrefName,MODE_PRIVATE).getString("user_id","")
        return userId!!
    }

    fun saveUserEmail(email:String){
        val sharedPref =  context.getSharedPreferences(UserSharePrefName,MODE_PRIVATE)
        sharedPref.edit().putString("user_email",email).commit()
    }
    fun getUserEmail():String{
        val userEmail =  context.getSharedPreferences(UserSharePrefName,MODE_PRIVATE).getString("user_email","")
        return userEmail!!
    }
    fun saveUserName(name:String){
        val sharedPref =  context.getSharedPreferences(UserSharePrefName,MODE_PRIVATE)
        sharedPref.edit().putString("user_name",name).commit()
    }
    fun getUserName():String{
        val userName =  context.getSharedPreferences(UserSharePrefName,MODE_PRIVATE).getString("user_name","")
        return userName!!
    }


    fun saveIsUserLoggedIn(loggedIn:Boolean){
        val sharedPref =  context.getSharedPreferences(UserSharePrefName,MODE_PRIVATE)
        sharedPref.edit().putBoolean("is_logged_in",loggedIn).commit()
    }

    companion object {
        const val UserSharePrefName = "UserPref"
    }
}