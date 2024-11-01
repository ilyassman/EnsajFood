package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.AdminUi.MainActivityAdmin
import com.example.myapplication.ClientUi.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var singup: TextView
    private lateinit var forgotPasswordText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialiser les vues
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        singup=findViewById(R.id.signupText)
        forgotPasswordText=findViewById(R.id.forgotPasswordText)
        singup.setOnClickListener{
            val intent = Intent(this, InscriptionActivity::class.java)
            startActivity(intent)
        }
        forgotPasswordText.setOnClickListener {
            startActivity(Intent(this, ForgotPass1Activity::class.java))
        }
        // Configurer le bouton de connexion
        loginButton.setOnClickListener {
            if (validateInputs()) {
                performLogin()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (emailInput.text.toString().trim().isEmpty()) {
            emailInput.error = "L'email est requis"
            isValid = false
        }

        if (passwordInput.text.toString().trim().isEmpty()) {
            passwordInput.error = "Le mot de passe est requis"
            isValid = false
        }

        return isValid
    }

    private fun performLogin() {
        // URL de votre API (à modifier selon votre configuration)
        val loginUrl = "http://10.0.2.2/foodapp/ws/loginUser.php"

        // Créer la requête
        val stringRequest = object : StringRequest(
            Request.Method.POST, loginUrl,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")
                    val id=jsonResponse.getString("id")
                    val nom=jsonResponse.getString("nom")
                    val role=jsonResponse.getString("role")

                    if (success) {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                        if(role=="0"){
                            val sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            sharedPrefs.edit().apply {
                                putString("id", id)
                                putString("nom", nom)
                                putString("role", role)
                                putString("email", jsonResponse.getString("email"))
                                putBoolean("IS_LOGGED_IN", true)
                                apply()
                            }
                            startActivity(Intent(this, MainActivityAdmin::class.java))
                            finish()
                        }
                        else {
                            // Connexion réussie

                            val sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            sharedPrefs.edit().apply {
                                putString("id", id)
                                putString("nom", nom)
                                putString("email", jsonResponse.getString("email"))
                                putBoolean("IS_LOGGED_IN", true)
                                apply()
                            }
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        // Erreur de connexion
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this, "Erreur lors de la connexion", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Erreur réseau: ${error.message}", Toast.LENGTH_LONG).show()
            }) {

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = emailInput.text.toString().trim()
                params["password"] = passwordInput.text.toString().trim()
                return params
            }
        }

        // Ajouter la requête à la file d'attente
        Volley.newRequestQueue(this).add(stringRequest)
    }
}