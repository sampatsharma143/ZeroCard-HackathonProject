package com.shunyank.zerocard.android.activities.card

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.romellfudi.fudinfc.gear.NfcAct
import com.romellfudi.fudinfc.util.sync.NfcReadUtilityImpl
import com.shunyank.zerocard.android.R




class NfcReaderActivity : AppCompatActivity(){
    private var package_name = "com.android.chrome";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_reader)
        Log.e("intent action",intent.action.toString())
        Log.e("intent data",intent.dataString.toString())
        if(intent.dataString==null){
            Toast.makeText(this ,"unable to fetch link",Toast.LENGTH_SHORT ).show()
            finish()
        }

            val builder = CustomTabsIntent.Builder()

            // to set the toolbar color use CustomTabColorSchemeParams
            // since CustomTabsIntent.Builder().setToolBarColor() is deprecated

            val params = CustomTabColorSchemeParams.Builder()
            params.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            builder.setDefaultColorSchemeParams(params.build())

            // shows the title of web-page in toolbar
            builder.setShowTitle(true)

            // setShareState(CustomTabsIntent.SHARE_STATE_ON) will add a menu to share the web-page
            builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)

            // To modify the close button, use
            // builder.setCloseButtonIcon(bitmap)

            // to set weather instant apps is enabled for the custom tab or not, use
            builder.setInstantAppsEnabled(true)

            //  To use animations use -
            //  builder.setStartAnimations(this, android.R.anim.start_in_anim, android.R.anim.start_out_anim)
            //  builder.setExitAnimations(this, android.R.anim.exit_in_anim, android.R.anim.exit_out_anim)
            val customBuilder = builder.build()
            customBuilder.intent.setPackage(package_name)
            customBuilder.launchUrl(this, Uri.parse(intent.dataString.toString()))
            finish()

    }
    fun Context.isPackageInstalled(packageName: String): Boolean {
        // check if chrome is installed or not
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun onPause() {
        super.onPause()
    }
//
    public override fun onNewIntent(paramIntent: Intent) {
        super.onNewIntent(paramIntent)

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
                // Process the messages array.
                if(messages.size>0){
                    for(data in messages){
                        val value = data.toByteArray().toString()
                        Log.e("value",value)
                    }
                }

            }
    }
//        if(Intent.ACTION_VIEW ==paramIntent.action){
//            val appLinkData: Uri? = intent?.data
//            Log.e("got Url",appLinkData.toString())
//        }
//
//        val items: SparseArray<String?>? = NfcReadUtilityImpl().readFromTagWithSparseArray(intent)
//        if(items!=null){
//            for (i in 0 until items.size()) {
//                // items.valueAt(i)
//                Log.e("item", ""+items.valueAt(i))
//                if(items.valueAt(i).toString().startsWith("https://sampatsharma.com")){
//
//                    runOnUiThread(Runnable {
//                        Toast.makeText(baseContext,items.valueAt(i).toString(),Toast.LENGTH_SHORT).show()
//                    })
//
//                }
//            }
//        }else{
//            Log.e("item"," not found\"")
//        }

    }
}