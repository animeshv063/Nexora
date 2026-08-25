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
        enableEdgeToEdge()
        setContent {
            ShoppingAppTheme {
                App()
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Toast.makeText(this, "Payment Successful! ID: $razorpayPaymentId 🎉", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed or Cancelled: $response", Toast.LENGTH_LONG).show()
    }
}
