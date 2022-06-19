package com.shunyank.zerocard.android.activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import com.shunyank.zerocard.Greeting
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.shunyank.zerocard.android.R
import com.shunyank.zerocard.android.activities.auth.LoginActivity
import com.shunyank.zerocard.android.activities.card.AddCard
import com.shunyank.zerocard.android.network.AppWriteHelper
import com.shunyank.zerocard.android.utils.Constants
import com.shunyank.zerocard.android.utils.SharedPref
import io.appwrite.Query
import io.appwrite.services.Database
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {
    lateinit var addCardButton:Button
    lateinit var progressBar:ProgressBar
    lateinit var refresh:ImageButton
    lateinit var logoutButton:ImageButton
    lateinit var noCardLayout:ConstraintLayout
    lateinit var webViewLayout:ConstraintLayout
    lateinit var webView: WebView
     var documentJson :String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // get Account Details
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        }
        initView()
        val db = Database(AppWriteHelper.getInstance(applicationContext))
        val uuid :String = SharedPref(this).getUserId()

        loadData(db,uuid)

        refresh.setOnClickListener {
            loadData(db,uuid)
        }
        addCardButton.setOnClickListener {

            if(documentJson.isBlank()||documentJson.isEmpty())
            {
                startActivity(Intent(applicationContext, AddCard::class.java))
            }
            else{
                    startActivity(Intent(applicationContext, AddCard::class.java).putExtra(
                            "data",
                            documentJson))
                }
        }
        logoutButton.setOnClickListener {
            showLogOutDialog("Logout","Do you really want to logout?")
        }
    }

    private fun loadData(db: Database, uuid: String) {

        GlobalScope.launch {
            kotlin.runCatching {
                db.listDocuments(
                    Constants.CardCollectionId,
                    queries = listOf(Query.equal("user_uuid", uuid))
                )

            }.onSuccess {
                Log.e("documentListSize","${it.total}")
                if (it.total > 0) {
                    documentJson = Gson().toJson( it.documents.get(0).data)
                    addCardButton.text = "Edit Card"

                    runOnUiThread(Runnable {
                        noCardLayout.visibility = View.GONE
                        webViewLayout.visibility = View.VISIBLE
                        loadWebView(documentJson)

                    })

                } else {
                    runOnUiThread(Runnable {
                        noCardLayout.visibility = View.VISIBLE
                        webViewLayout.visibility = View.GONE


                        addCardButton.text = "Add Business Card"
                    })
                }

            }.onFailure {
                it.printStackTrace()

            }
        }
    }

    private fun loadWebView(documentJson: String?) {
        progressBar.visibility = View.VISIBLE
        webView.settings.setJavaScriptEnabled(true)
        var id =  JSONObject(documentJson).get("\$id")
        val businessCardUrl = "https://sampatsharma.com/zerocard/"

        Log.e("url",businessCardUrl)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(businessCardUrl)
                if (url!!.contains("https://sampatsharma.com")) {
                    view!!.loadUrl(url)
                } else {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(i)
                }
                return true
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE

                super.onPageFinished(view, url)
            }
        }
        webView.loadUrl(businessCardUrl)

    }

    private fun initView() {
        addCardButton = findViewById(R.id.add_business_card)
        noCardLayout = findViewById(R.id.no_card_main_layout)
        webViewLayout = findViewById(R.id.webviewLayout)
        webView = findViewById(R.id.webview)
        refresh = findViewById(R.id.refresh_button)
        progressBar = findViewById(R.id.progress_bar)
        logoutButton = findViewById(R.id.setting_button)
    }


    fun showLogOutDialog(title:String,msg:String){
        val builder: AlertDialog.Builder? = AddCard@this?.let {
            AlertDialog.Builder(it)
        }

        builder!!.setMessage(msg)
            .setTitle(title)

        builder.apply {
            setPositiveButton("yes") { dialog, id ->
                logOut()
            }
            setNegativeButton("Cancel") { dialog, id ->
            dialog.cancel()
            }

        }
        val dialog: AlertDialog? = builder.create()

        dialog!!.show()
    }

    private fun logOut() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        val shared = SharedPref(this)
        shared.saveIsUserLoggedIn(false)
        shared.saveUserEmail("")
        shared.saveUserName("")
        shared.saveUserId("")
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }

}
