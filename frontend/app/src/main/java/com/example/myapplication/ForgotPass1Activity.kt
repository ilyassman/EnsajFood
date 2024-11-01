package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ClientUi.DetailFoodActivity
import com.example.myapplication.util.EmailCallback

import com.example.myapplication.util.EmailService
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgotPass1Activity : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var sendCodeButton: MaterialButton
    private lateinit var backButton: ImageButton
    private lateinit var emailService: EmailService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass1)

        // Initialisation des vues et du service
        emailInput = findViewById(R.id.emailInput)
        emailLayout = findViewById(R.id.emailLayout)
        sendCodeButton = findViewById(R.id.sendCodeButton)
        backButton = findViewById(R.id.backButton)
        emailService = EmailService(this)

        backButton.setOnClickListener {
            finish()
        }

        sendCodeButton.setOnClickListener {
            handleSendCode()
        }
    }

    private fun handleSendCode() {
        val email = emailInput.text.toString().trim()

        // Réinitialiser l'état d'erreur
        emailLayout.error = null

        if (email.isEmpty()) {
            emailLayout.error = "Veuillez entrer votre email"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.error = "Veuillez entrer un email valide"
            return
        }

        // Générez un code aléatoire
        val verificationCode = generateRandomCode()
        sendCodeButton.isEnabled = false
        sendCodeButton.text = "Envoi en cours..."
        // Appelez la méthode sendEmail avec un callback
        emailService.sendEmail(email, verificationCode, object : EmailCallback {
            override fun onSuccess() {

                val intent = Intent(this@ForgotPass1Activity, ForgetPass2Activity::class.java)
                intent.putExtra("code", verificationCode)
                intent.putExtra("email", email)
                startActivity(intent)


            }

            override fun onError(message: String) {
                emailLayout.error = "L'email spécifié n'existe pas."
                sendCodeButton.isEnabled = true
                sendCodeButton.text = "Envoyer le code"
            }
        })
    }

    fun generateRandomCode(): String {
        val code = (1000..9999).random() // Génère un nombre aléatoire entre 1000 et 9999
        return code.toString()
    }




}