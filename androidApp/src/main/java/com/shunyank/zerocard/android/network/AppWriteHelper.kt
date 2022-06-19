package com.shunyank.zerocard.android.network

import android.content.Context
import io.appwrite.Client

class AppWriteHelper {
        companion object {
            @Volatile private var INSTANCE: Client? = null
            fun getInstance(context: Context): Client =
                INSTANCE ?: Client(context)
                    .setEndpoint("https://superapp.megamind.app/v1")
                    .setProject("62ad9d00262b2158da33")
        }

}