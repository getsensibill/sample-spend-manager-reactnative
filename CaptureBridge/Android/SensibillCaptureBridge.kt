package com.mytestapp

import android.app.Activity
import android.content.Intent
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.getsensibill.capturestandalone.CaptureStandaloneActivity
import com.getsensibill.capturestandalone.models.BareCapturedReceiptData
import java.io.File


class SensibillCaptureBridge internal constructor(context: ReactApplicationContext) : ReactContextBaseJavaModule(context), ActivityEventListener {

    override fun getName(): String {
        return "SensibillCaptureBridge"
    }

    // add to CalendarModule.java
    @ReactMethod
    fun launchCapture() {
        Toast.makeText(reactApplicationContext, "Called Launch Capture", Toast.LENGTH_SHORT).show()
        Log.d("SensibillCaptureBridge", "Called launchCapture()")
        val intent = Intent(currentActivity, CaptureStandaloneActivity::class.java)
        currentActivity!!.startActivityForResult(intent, 2, null)
    }

    override fun onNewIntent(intent: Intent) {}

    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            activity.setResult(resultCode, data)

            data.getSerializableExtra(CaptureStandaloneActivity.EXTRA_CAPTURED_RECEIPTS)?.let {
                it.asTypedArray<BareCapturedReceiptData>()
                        .forEach { image ->
                            val params: WritableMap = Arguments.createMap()
                            params.putString(image.receiptFile.name, convertToBase64(image.receiptFile))

                            reactApplicationContext
                                    .getJSModule(RCTDeviceEventEmitter::class.java)
                                    .emit(EMITTER_NAME, convertToBase64(image.receiptFile))
                }
            }
        }
    }

    fun convertToBase64(attachment: File): String {
        return Base64.encodeToString(attachment.readBytes(), Base64.NO_WRAP)
    }

    init {
        context.addActivityEventListener(this)
    }

    companion object {
        private const val EMITTER_NAME = "sendImage"
    }
}