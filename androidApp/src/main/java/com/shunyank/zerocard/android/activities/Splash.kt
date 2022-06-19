package com.shunyank.zerocard.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.shunyank.zerocard.android.activities.MainActivity
import com.shunyank.zerocard.android.activities.auth.LoginActivity
import com.shunyank.zerocard.android.network.AppWriteHelper
import com.shunyank.zerocard.android.utils.SharedPref
import io.appwrite.services.Account
import io.appwrite.services.Database
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    private var isUserLoggedIn= false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)




        // Check if user Logged in

         isUserLoggedIn =   getSharedPreferences(SharedPref.UserSharePrefName, MODE_PRIVATE).getBoolean("is_logged_in",false)
        Log.e("isLoggedin","$isUserLoggedIn")

        val client =  AppWriteHelper.getInstance(this)

        val account = Account(client)

        // Create Session to use read and write access

        val timer = object: CountDownTimer(1500, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //
            }

            override fun onFinish() {
                GlobalScope.launch {

                    kotlin.runCatching {

                        account.getSessions()

//               account.createAnonymousSession()
                    }
                        .onSuccess {
                            if(it.sessions.size>0){


                                // if true then we will start mainActivity
                               startActivity()

                            }else{
                                // if there is no session please create one
                                createSession(account)
                            }

                            Log.e("Account Response", "-> ${it.toString()}")

                        }
                        .onFailure {
                            createSession(account)
                            Log.e("Account Response exec", "-> ${it.localizedMessage}")
                        }

                }
            }
        }
        timer.start()
    }

    suspend fun createSession(account:Account){
        kotlin.runCatching {
            account.createAnonymousSession()
        }.onSuccess {
           startActivity()
        }
    }



    private fun startActivity(){
        // if true then we will start mainActivity
        if(isUserLoggedIn)
        {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }else
        {
            startActivity(Intent(applicationContext, LoginActivity::class.java))

        }
        finish()
    }
}