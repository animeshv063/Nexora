package com.example.shopping.presentation.utils

import android.content.Context
import com.example.shopping.domain.models.OrderDataModel
import org.json.JSONArray
import org.json.JSONObject

object UserOrdersStorage {

    private const val PREFS_NAME = "user_orders_prefs"
    private const val KEY_ORDERS_PREFIX = "orders_"

    fun saveOrderLocally(context: Context, uid: String, order: OrderDataModel) {
        try {
            val existing = getLocalOrders(context, uid).toMutableList()
            val index = existing.indexOfFirst { it.orderId == order.orderId }
            if (index >= 0) {
                existing[index] = order
            } else {
                existing.add(0, order)
            }
            saveOrdersList(context, uid, existing)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLocalOrders(context: Context, uid: String): List<OrderDataModel> {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonStr = prefs.getString(KEY_ORDERS_PREFIX + uid, null) ?: return emptyList()
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<OrderDataModel>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    OrderDataModel(
                        orderId = obj.optString("orderId", ""),
                        productId = obj.optString("productId", ""),
                        productName = obj.optString("productName", ""),
                        quantity = obj.optInt("quantity", 1),
                        address = obj.optString("address", ""),
                        paymentMethod = obj.optString("paymentMethod", "COD"),
                        orderDate = obj.optLong("orderDate", System.currentTimeMillis()),
                        status = obj.optString("status", "Order Placed")
                    )
                )
            }
            list.sortedByDescending { it.orderDate }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun updateOrderStatusLocally(context: Context, uid: String, orderId: String, newStatus: String) {
        val existing = getLocalOrders(context, uid).toMutableList()
        val index = existing.indexOfFirst { it.orderId == orderId }
        if (index >= 0) {
            val updated = existing[index].copy(status = newStatus)
            existing[index] = updated
            saveOrdersList(context, uid, existing)
        }
    }

    fun clearOrdersLocally(context: Context, uid: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_ORDERS_PREFIX + uid).apply()
    }

    private fun saveOrdersList(context: Context, uid: String, list: List<OrderDataModel>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        list.forEach { order ->
            val obj = JSONObject().apply {
                put("orderId", order.orderId)
                put("productId", order.productId)
                put("productName", order.productName)
                put("quantity", order.quantity)
                put("address", order.address)
                put("paymentMethod", order.paymentMethod)
                put("orderDate", order.orderDate)
                put("status", order.status)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_ORDERS_PREFIX + uid, jsonArray.toString()).apply()
    }
}
