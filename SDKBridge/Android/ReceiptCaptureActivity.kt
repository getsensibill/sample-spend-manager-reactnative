package com.mytestapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.getsensibill.captureflow.coordinator.CaptureFlowCoordinator
import com.getsensibill.captureflow.coordinator.CaptureFlowState
import java.util.*

/**
 * Used to launch the Receipt Capture Flow.
 * Creates a capture flow coordinator, launches it when user clicks the button.
 * A listener is used to track state changes, and outputs that to the layout.
 */
class ReceiptCaptureActivity : AppCompatActivity() {

    private lateinit var progressText: TextView
    private lateinit var launchCapture: TextView

    // Creates a capture flow Coordinator
    private val captureFlow = CaptureFlowCoordinator(this)

    // Capture flow listener, used to listen to the state changing
    private val captureFlowListener: CaptureFlowCoordinator.CaptureFlowListener =
            object : CaptureFlowCoordinator.CaptureFlowListener {
                @SuppressLint("SetTextI18n")
                override fun onCaptureFlowUpdate(newState: CaptureFlowState, externalAccountTransactionId: String?) {
                    val text = when (newState) {
                        is CaptureFlowState.IMAGES_CAPTURED -> "Images are captured"
                        is CaptureFlowState.FLOW_CANCELLED -> "Capture flow cancelled"
                        is CaptureFlowState.Error -> "Error occurred: ${newState.exception.message}"
                        is CaptureFlowState.Transacting -> {
                            val transaction = with(newState.transaction) {
                                "status:$status\nlocalId:$localId\ntxnId:$transactionId\n" +
                                        "externalTxnId:$externalTransactionId\nreceiptId:$receiptId"
                            }
                            "Transacting\n$transaction\n(savedExtTxnId:$externalAccountTransactionId)"
                        }
                    }
                    progressText.appendOnNewLine("${Date()}: $text")
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_capture)

        progressText = findViewById(R.id.progressText)
        launchCapture = findViewById(R.id.launchCapture)

        launchCapture.setOnClickListener {
            // launches the capture flow
            captureFlow.launchCaptureFlow(captureFlowListener, externalAccountTransactionId = "testTxnId1")
        }
    }

    /**
     * Helper function for appending new lines for the example
     */
    @SuppressLint("SetTextI18n")
    private fun TextView.appendOnNewLine(newText: CharSequence) {
        runOnUiThread { text = "$text\n$newText" }
    }
}
