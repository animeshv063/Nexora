package com.example.shopping

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.shopping.presentation.navigation.App
import com.example.shopping.ui.theme.ShoppingAppTheme
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.razorpay.Checkout.preload(applicationContext)
        enableEdgeToEdge()
        setContent {
            ShoppingAppTheme {
                App()
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        val txnId = razorpayPaymentId ?: "TXN_${System.currentTimeMillis()}"
        Toast.makeText(this, "Payment Successful! ID: $txnId 🎉", Toast.LENGTH_LONG).show()
        com.example.shopping.presentation.utils.PaymentEventManager.emitEvent(
            com.example.shopping.presentation.utils.PaymentEventManager.PaymentEvent.Success(txnId)
        )
    }

    override fun onPaymentError(code: Int, response: String?) {
        val errMsg = response ?: "Payment cancelled or no payment method available"
        com.example.shopping.presentation.utils.PaymentEventManager.emitEvent(
            com.example.shopping.presentation.utils.PaymentEventManager.PaymentEvent.Error(code, errMsg)
        )
    }
}
