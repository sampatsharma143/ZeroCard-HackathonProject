package com.shunyank.zerocard.android.activities.card

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.romellfudi.fudinfc.gear.NfcAct
import com.romellfudi.fudinfc.gear.interfaces.OpCallback
import com.romellfudi.fudinfc.util.sync.NfcReadUtilityImpl

import com.shunyank.zerocard.android.R
import com.shunyank.zerocard.android.activities.nfc.ShowQrCodeActivity
import com.shunyank.zerocard.android.network.AppWriteHelper
import com.shunyank.zerocard.android.utils.Constants
import com.shunyank.zerocard.android.utils.SharedPref
import io.appwrite.services.Database
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddCard :NfcAct() {
    var isUpdate:Boolean = false

    var mOpCallback: OpCallback? = null
    private lateinit var saveButton:Button
    private lateinit var backButton:ImageButton
    private lateinit var edtBName:EditText
    private lateinit var edtBEmail:EditText
    private lateinit var edtBInsta:EditText
    private lateinit var edtBYoutube:EditText
    private lateinit var edtBOwner:EditText
    private lateinit var edtBWebsite:EditText
    private lateinit var edtBPhone:EditText
    private lateinit var edtBAddres:EditText
    var bName:String = ""
    var bEmail:String = ""
    var bPhone:String = ""
    var bInsta:String = ""
    var bYoutube:String = ""
    var bOwnerName:String = ""
    var bWebsite:String = ""
    var bAddress:String = ""
    var businessCardUrl:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        initUi()
        val db = Database(AppWriteHelper.getInstance(applicationContext))
        val uuid :String = SharedPref(this).getUserId()


        val data:String? = intent.getStringExtra("data")
        if(data!=null){

            Log.e("data",data)
            val jsonData:JSONObject = JSONObject(data)
            // Populate Data

            populateData(jsonData)
            val id = jsonData.get("\$id")
            businessCardUrl = "https://sampatsharma.com/zerocard"
            saveButton.text = "Update"

            isUpdate = true
            findViewById<Button>(R.id.write_nfc).visibility = View.VISIBLE

        }else{
            findViewById<Button>(R.id.write_nfc).visibility = View.GONE

            saveButton.text = "Save"
            isUpdate = false



        }
//        GlobalScope.launch {
//            kotlin.runCatching {
//                db.listDocuments(Constants.CardCollectionId,  queries = listOf(Query.equal("uuid",uuid )))
//
//            }.onSuccess {
//                if(it.total>0){
//
//
//                }else{
//
//
//                }
//
//            }.onFailure {
//
//            }
//        }
        findViewById<Button>(R.id.write_nfc).setOnClickListener {

           var writeIntent = Intent(this,ShowQrCodeActivity::class.java)
            writeIntent.putExtra("business_url",businessCardUrl)
            startActivity(writeIntent)
            Log.e("businessCardUrl",businessCardUrl)
        }
        saveButton.setOnClickListener {
            if(validateData()){

                bName = edtBName.text.trim().toString()
                bEmail = edtBEmail.text.trim().toString()
                bPhone = edtBPhone.text.trim().toString()
                bInsta = edtBInsta.text.trim().toString()
                bYoutube = edtBYoutube.text.trim().toString()
                bOwnerName = edtBOwner.text.trim().toString()
                bWebsite = edtBWebsite.text.trim().toString()
                bAddress = edtBAddres.text.trim().toString()
                if(isUpdate){

                    GlobalScope.launch {
                        kotlin.runCatching {
                            db.updateDocument(Constants.CardCollectionId, "4837294837aaa",
                                data = mapOf(
                                    "business_name" to bName,
                                    "business_email" to bEmail,
                                    "business_address" to bAddress,
                                    "business_url" to bWebsite,
                                    "phone_number" to bPhone,
                                    "user_uuid" to uuid,
                                    "instagram_link" to bInsta,
                                    "youtube_link" to bYoutube,
                                    "business_owner" to bOwnerName

                                    ))
                        }.onSuccess {
                            businessCardUrl = "https://sampatsharma.com/zerocard/"+it.id
                            runOnUiThread(Runnable {
                                findViewById<Button>(R.id.write_nfc).visibility = View.VISIBLE
                                showDialog("Write NFC Tag","Card Updated Successfully")
                            })

                        }.onFailure {
                            it.printStackTrace()
                            runOnUiThread(Runnable {
                                Toast.makeText(baseContext,"Something Went Wrong",Toast.LENGTH_SHORT).show()

                            })

                        }
                    }
                    // update Data
                }else{
                    //save Data to the server

                    GlobalScope.launch {
                        kotlin.runCatching {
                            db.createDocument(Constants.CardCollectionId, "4837294837aaa",
                                data = mapOf(
                                    "business_name" to bName,
                                    "business_email" to bEmail,
                                    "business_address" to bAddress,
                                    "business_url" to bWebsite,
                                    "phone_number" to bPhone,
                                    "user_uuid" to uuid,
                                    "instagram_link" to bInsta,
                                    "youtube_link" to bYoutube,
                                    "business_owner" to bOwnerName

                                    ))
                        }.onSuccess {
                            businessCardUrl = "https://sampatsharma.com/zerocard/"+it.id
                            runOnUiThread(Runnable {
                                findViewById<Button>(R.id.write_nfc).visibility = View.VISIBLE
                                showDialog("Write NFC Tag","Card Added Successfully/n Please Write Your NFC Tag")
                            })

                        }.onFailure {
                            it.printStackTrace()
                            runOnUiThread(Runnable {
                                Toast.makeText(baseContext,"Something Went Wrong",Toast.LENGTH_SHORT).show()

                            })

                        }
                    }
                }



            }
        }

        backButton.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun populateData(jsonData: JSONObject) {
        val businessEmail = jsonData.get("business_email").toString()

        val businessInsta = jsonData.get("instagram_link").toString()
        val businessYoutube = jsonData.get("youtube_link").toString()
        val businessOwner = jsonData.get("business_owner").toString()
        val businessWebsite = jsonData.get("business_url").toString()
        val businessAddress= jsonData.get("business_address").toString()

        edtBName.setText(jsonData.get("business_name").toString())

        if(!businessEmail.isBlank()){
            edtBEmail.setText( businessEmail)
        }
        edtBPhone.setText(jsonData.get("phone_number").toString())


        if(!businessInsta.isBlank()){
            edtBInsta.setText( businessInsta)
        }
        if(!businessYoutube.isBlank()){
            edtBYoutube.setText( businessYoutube)
        }
        if(!businessOwner.isBlank()){
            edtBOwner.setText( businessOwner)
        }

        if(!businessWebsite.isBlank()){
            edtBWebsite.setText( businessWebsite)
        }
        if(!businessAddress.isBlank()){
            edtBAddres.setText( businessAddress)
        }


    }

    private fun validateData(): Boolean {

        if(edtBName.text.trim().toString().isEmpty()||edtBPhone.text.trim().toString().isEmpty()){
            runOnUiThread(
                Runnable {
                    Toast.makeText(baseContext,"Please Enter Business Name And Phone",Toast.LENGTH_SHORT).show()
                }
            )
            return false
        }
        return true
    }

    private fun initUi() {
        backButton = findViewById(R.id.back_button)
        saveButton = findViewById(R.id.save_button)
        edtBName = findViewById(R.id.edt_bs_name)
        edtBEmail = findViewById(R.id.edt_bs_email)
        edtBWebsite = findViewById(R.id.edt_bs_web)
        edtBAddres = findViewById(R.id.edt_bs_address)
        edtBPhone = findViewById(R.id.edt_bs_phone)
        edtBOwner = findViewById(R.id.edt_bs_owner)
        edtBInsta = findViewById(R.id.edt_bs_insta)
        edtBYoutube = findViewById(R.id.edt_bs_youtube)

    }



    fun showDialog(title:String,msg:String){
        val builder: AlertDialog.Builder? = AddCard@this?.let {
            AlertDialog.Builder(it)
        }

        builder!!.setMessage(msg)
            .setTitle(title)

        builder.apply {
            setPositiveButton("Ok") { dialog, id ->
                dialog.cancel()
            }

        }
        val dialog: AlertDialog? = builder.create()

        dialog!!.show()
    }


}