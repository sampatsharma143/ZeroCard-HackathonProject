package com.shunyank.zerocard.android.activities.nfc

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.encoder.QRCode
import com.romellfudi.fudinfc.gear.NfcAct
import com.romellfudi.fudinfc.gear.interfaces.OpCallback
import com.romellfudi.fudinfc.gear.interfaces.TaskCallback
import com.romellfudi.fudinfc.util.interfaces.NfcWriteUtility
import com.shunyank.zerocard.android.R

import java.io.File
import java.net.URL

class ShowQrCodeActivity : AppCompatActivity(){
    private var businessCardUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_nfc)
         businessCardUrl = intent.getStringExtra("business_url")
        Log.e("cardUrl",businessCardUrl!!)
        if(businessCardUrl==null){
            Toast.makeText(this ,"Business Card Url Not Found Closing Activity!",Toast.LENGTH_SHORT).show()
            finish()
        }
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            super.onBackPressed()
        }

        var url = URL(businessCardUrl)
        var qrImageView:ImageView = findViewById(R.id.qr_code)
        Glide.with(baseContext).load(getQrCodeBitmap(businessCardUrl!!)).into(qrImageView)
//        val file: File = QRCode.from(url)


    }

    fun getQrCodeBitmap(url: String): Bitmap {
        val size = 512 //pixels
        val qrCodeContent = "https://sampatsharma.com/zerocard"
        val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }

}