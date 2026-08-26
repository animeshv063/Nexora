package com.example.shopping.presentation.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object PaymentEventManager {
    private val _paymentResultFlow = MutableSharedFlow<PaymentEvent>(extraBufferCapacity = 1)
    val paymentResultFlow = _paymentResultFlow.asSharedFlow()

    fun emitEvent(event: PaymentEvent) {
        _paymentResultFlow.tryEmit(event)
    }

    sealed class PaymentEvent {
        data class Success(val paymentId: String) : PaymentEvent()
        data class Error(val code: Int, val message: String) : PaymentEvent()
    }
}
