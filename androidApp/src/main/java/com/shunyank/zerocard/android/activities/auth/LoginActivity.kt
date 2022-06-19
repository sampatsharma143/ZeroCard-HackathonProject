package com.shunyank.zerocard.android.activities.auth

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.shunyank.zerocard.android.R
import com.shunyank.zerocard.android.activities.MainActivity
import com.shunyank.zerocard.android.network.AppWriteHelper
import com.shunyank.zerocard.android.utils.Constants
import com.shunyank.zerocard.android.utils.SharedPref
import io.appwrite.Query
import io.appwrite.models.DocumentList
import io.appwrite.services.Account
import io.appwrite.services.Database
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Document

class LoginActivity : AppCompatActivity() {
    lateinit var loginButton : CardView
    lateinit var backButton : ImageButton
    lateinit var sharedPref:SharedPref

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
         sharedPref = SharedPref(this)

        FirebaseApp.initializeApp(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient.signOut()

        firebaseAuth = FirebaseAuth.getInstance()
        loginButton.setOnClickListener(View.OnClickListener {

            signInGoogle()
        })

        backButton.setOnClickListener {
            super.onBackPressed()
        }
    }
   fun initView() {
        loginButton = findViewById(R.id.login_cardview)
       backButton = findViewById(R.id.back_button)
    }

    private fun signInGoogle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            loginButton.setCardBackgroundColor(getColor(R.color.grey60))
            loginButton.isClickable = false
        }
        val signInIntent: Intent = mGoogleSignInClient.signInIntent

        startActivityForResult(signInIntent, Req_Code)
    }

    // onActivityResult() function : this is where
    // we provide the task and data for the Google Account
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException) {
            enableLoginButton()
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // this is where we update the UI after Google signin takes place
    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {


                sharedPref.saveUserId(account.id!!)
                sharedPref.saveUserName(account.displayName!!)
                sharedPref.saveUserEmail(account.email!!)

                // create user in AppWrite
                createAppWriteUser(account.id!!,account.displayName!!,account.email!!)


            }
            enableLoginButton()
        }
    }

    private fun createAppWriteUser(id: String, displayName: String, email: String) {

        // check if user exist or not
        val client = AppWriteHelper.getInstance(this)
        val db = Database(client)


       GlobalScope.launch {
           kotlin.runCatching {
               db.listDocuments(Constants.UsersCollectionId,queries = listOf(Query.equal("uuid",id )),)

           }.onSuccess {

               if(it.total<=0){
                   kotlin.runCatching {
                       db.createDocument(Constants.UsersCollectionId,"unique()", data = mapOf("name" to displayName,"uuid" to id,"email" to email))

                   }.onSuccess {
                       showToastMessage("Logged In Successfully")

                       sharedPref.saveIsUserLoggedIn(true)
                       startMainActivity()
                   }.onFailure {
                       it.printStackTrace()
                       showToastMessage("Something Went Wrong")

                   }

               }else{
                   sharedPref.saveIsUserLoggedIn(true)

                   showToastMessage("Logged In Successfully")

                   startMainActivity()
               }

           }.onFailure {
               it.printStackTrace()
               showToastMessage("Something Went Wrong")

           }

       }



    }
    fun showToastMessage(msg:String){
        runOnUiThread {   Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT).show() }


    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun enableLoginButton(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            loginButton.setCardBackgroundColor(getColor(R.color.colorPrimaryDark))
            loginButton.isClickable = true
        }
    }
}