package com.example.myapplication.util

import android.content.Context
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

interface EmailCallback {
    fun onSuccess()
    fun onError(message: String)
}

class EmailService(private val context: Context) {

    private val url = "http://10.0.2.2/foodapp/ws/sendMail.php"

    fun sendEmail(to: String, code: String, callback: EmailCallback) {
        // Créer l'objet JSON avec les données de l'email
        val jsonBody = JSONObject().apply {
            put("to", to)
            put("verification_code", code)
        }

        // Créer la requête POST
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                // Gérer la réponse de succès
                callback.onSuccess()
                Toast.makeText(context, "Email envoyé avec succès !", Toast.LENGTH_SHORT).show()
            },
            { error ->
                // Gérer l'erreur
                callback.onError("Erreur lors de l'envoi de l'email : ${error.message}")
            }
        )

        // Ajouter la requête à la file d'attente de Volley
        Volley.newRequestQueue(context).add(request)
    }

}
