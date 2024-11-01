package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject

class ForgetPass2Activity : AppCompatActivity() {

    private lateinit var emailText: TextView
    private lateinit var codeInput: TextInputEditText
    private lateinit var codeLayout: TextInputLayout
    private lateinit var newPasswordInput: TextInputEditText
    private lateinit var newPasswordLayout: TextInputLayout
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var resetPasswordButton: MaterialButton
    private lateinit var backButton: ImageButton

    private lateinit var email: String
    private lateinit var verificationCode: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_pass2)


        email = intent.getStringExtra("email") ?: ""
        verificationCode = intent.getStringExtra("code") ?: ""

        initializeViews()
        setupListeners()

    }

    private fun initializeViews() {
        emailText = findViewById(R.id.emailText)
        codeInput = findViewById(R.id.codeInput)
        codeLayout = findViewById(R.id.codeLayout)
        newPasswordInput = findViewById(R.id.newPasswordInput)
        newPasswordLayout = findViewById(R.id.newPasswordLayout)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout)
        resetPasswordButton = findViewById(R.id.resetPasswordButton)

        backButton = findViewById(R.id.backButton)
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }

        resetPasswordButton.setOnClickListener {
            validateAndResetPassword()
        }


    }

    private fun validateAndResetPassword() {
        // Réinitialiser les erreurs
        codeLayout.error = null
        newPasswordLayout.error = null
        confirmPasswordLayout.error = null

        val code = codeInput.text.toString().trim()
        val newPassword = newPasswordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        // Validation du code
        if (code.isEmpty()) {
            codeLayout.error = "Veuillez entrer le code"
            return
        }

        if (code != verificationCode) {
            codeLayout.error = "Code incorrect"
            return
        }

        // Validation du mot de passe
        if (newPassword.isEmpty()) {
            newPasswordLayout.error = "Veuillez entrer un nouveau mot de passe"
            return
        }

        if (newPassword.length < 6) {
            newPasswordLayout.error = "Le mot de passe doit contenir au moins 6 caractères"
            return
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.error = "Veuillez confirmer le mot de passe"
            return
        }

        if (newPassword != confirmPassword) {
            confirmPasswordLayout.error = "Les mots de passe ne correspondent pas"
            return
        }

        // Désactiver le bouton pendant la réinitialisation
        resetPasswordButton.isEnabled = false
        resetPasswordButton.text = "Réinitialisation en cours..."

        // Appel à votre API pour réinitialiser le mot de passe
        resetPassword(email, newPassword)
    }

    private fun resetPassword(email: String, newPassword: String) {
        // Créer l'objet JSON pour la requête
        val jsonBody = JSONObject().apply {
            put("email", email)
            put("new_password", newPassword)
        }

        // URL de votre API
        val url = "http://10.0.2.2/foodapp/ws/resetPassword.php"

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                if (response.has("error")) {
                    // Gérer l'erreur
                    runOnUiThread {
                        Toast.makeText(this, response.getString("error"), Toast.LENGTH_LONG).show()

                    }
                } else {
                    // Succès
                    runOnUiThread {
                        Toast.makeText(this, "Mot de passe réinitialisé avec succès", Toast.LENGTH_SHORT).show()
                        // Rediriger vers la page de connexion
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            },
            { error ->
                runOnUiThread {
                    Toast.makeText(this, "Erreur réseau : ${error.message}", Toast.LENGTH_LONG).show()

                }
            }
        )

        Volley.newRequestQueue(this).add(request)
    }



}