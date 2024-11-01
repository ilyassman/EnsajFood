package com.example.myapplication.util

import android.content.Context

class CartManager(context: Context) {
    private val prefs = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)

    fun addToCart(productId: String, quantity: Int) {
        prefs.edit().putInt(productId, quantity).apply()
    }

    fun getCartItems(): Map<String, Int> {
        val allEntries = prefs.all
        val cartItems = mutableMapOf<String, Int>()

        for ((key, value) in allEntries) {
            if (value is Int) {
                cartItems[key] = value
            }
        }

        return cartItems
    }
    fun clearCart() {
        val editor = prefs.edit()
        editor.clear()  // Supprime toutes les données dans les SharedPreferences
        editor.apply() // Applique les changements de manière asynchrone
    }
    fun removeFromCart(productId: String) {
        prefs.edit().remove(productId).apply()
    }

}