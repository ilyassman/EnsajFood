package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class InscriptionActivity : AppCompatActivity() {
    private lateinit var btnback: ImageButton
    private lateinit var nom: TextInputEditText
    private lateinit var prenom: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var mdp: TextInputEditText
    private lateinit var submit: Button

    // Initialiser la RequestQueue
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)

        btnback = findViewById(R.id.backButton2)
        nom = findViewById(R.id.nomInput)
        prenom = findViewById(R.id.prenomInput)
        email = findViewById(R.id.emailInput)
        mdp = findViewById(R.id.passwordInput)
        submit = findViewById(R.id.signupButton)

        // Initialiser la RequestQueue
        requestQueue = Volley.newRequestQueue(this)

        submit.setOnClickListener {

            registerUser()
        }

        btnback.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {
        val url = "http://10.0.2.2/foodapp/ws/addetudiant.php" // Remplace par l'URL de ton endpoint

        // Créer un objet JSON pour les données
        val jsonBody = JSONObject()
        jsonBody.put("nom", nom.text.toString())
        jsonBody.put("prenom", prenom.text.toString())
        jsonBody.put("email", email.text.toString())
        jsonBody.put("password", mdp.text.toString())

        // Créer une requête POST
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                // Traiter la réponse ici
                val jsonResponse = JSONObject(response)
                if (jsonResponse.getBoolean("success")) {
                    Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                // Gérer les erreurs
                Toast.makeText(this, "Erreur: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email.text.toString().trim()
                params["password"] = mdp.text.toString().trim()
                params["nom"] = nom.text.toString().trim()
                params["prenom"] = prenom.text.toString().trim()
                return params
            }
        }

        // Ajouter la requête à la queue
        requestQueue.add(stringRequest)
    }
}
