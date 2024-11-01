package com.example.myapplication.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.myapplication.AdminUi.MainActivityAdmin
import com.example.myapplication.R
import com.example.myapplication.beans.Food
import org.json.JSONObject

class EditFoodActivity : AppCompatActivity() {
    private lateinit var foodImage: ImageView
    private lateinit var nameEdit: EditText
    private lateinit var descriptionEdit: EditText
    private lateinit var priceEdit: EditText
    private lateinit var ratingText: TextView
    private lateinit var saveButton: Button
    private lateinit var changeImageButton: Button
    private lateinit var backButton: ImageView

    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_food)

        // Initialize views
        initializeViews()

        // Get food data from intent
        val id = intent.getIntExtra("id", -1)
        val quantity = intent.getIntExtra("quantity", 0)
        val name = intent.getStringExtra("name") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val price = intent.getDoubleExtra("price", 0.0)
        val rating = intent.getFloatExtra("rating", 0f)
        val imagePath = intent.getStringExtra("imagePath") ?: ""

        // Créer l'objet Food
        val food = Food(id, quantity, name, description, price, rating, imagePath)

        // Mettre à jour les vues
        nameEdit.setText(food.name)
        descriptionEdit.setText(food.desciption)
        priceEdit.setText(food.price.toString())
        ratingText.text = "Note: ${food.rating}"

        // Charger l'image
        Glide.with(this)
            .load("http://10.0.2.2/foodapp/uploads/produit/${food.imagePath}")
            .into(foodImage)

        setupClickListeners(food)

    }

    private fun initializeViews() {
        foodImage = findViewById(R.id.foodImageEdit)
        nameEdit = findViewById(R.id.nameEdit)
        descriptionEdit = findViewById(R.id.descriptionEdit)
        priceEdit = findViewById(R.id.priceEdit)
        ratingText = findViewById(R.id.ratingText)
        saveButton = findViewById(R.id.saveButton)
        changeImageButton = findViewById(R.id.changeImageButton)
        backButton = findViewById(R.id.backBtn)
    }

    private fun setupClickListeners(food: Food?) {
        // Handle image change button
        changeImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Handle back button
        backButton.setOnClickListener {
            finish()
        }

        // Handle save button
        saveButton.setOnClickListener {
            val updatedName = nameEdit.text.toString()
            val updatedDescription = descriptionEdit.text.toString()
            val updatedPrice = priceEdit.text.toString().toDoubleOrNull() ?: 0.0

        food?.let {
                updateFood(
                    id = food.id,
                    name = updatedName,
                    description = updatedDescription,
                    price = updatedPrice,

                )
            }
        }
    }

    private fun updateFood(id: Int, name: String, description: String, price: Double) {
        // Validation des champs
        if (name.isEmpty() || description.isEmpty() || price <= 0) {
            Toast.makeText(this, "Veuillez remplir tous les champs correctement", Toast.LENGTH_SHORT).show()
            return
        }

        val queue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2/foodapp/ws/updateProduit.php?id=$id"

        // Créer les paramètres
        val params = HashMap<String, String>()
        params["nom"] = name
        params["description"] = description
        params["prix"] = price.toString()
        // Remplacez par la vraie catégorie si nécessaire

        // Ajouter l'image en base64 si une nouvelle image est sélectionnée
        selectedImageUri?.let { uri ->
            try {
                val imageBase64 = convertImageToBase64(uri)
                params["image"] = "data:image/jpeg;base64,$imageBase64"
            } catch (e: Exception) {
                Toast.makeText(this, "Erreur lors de la conversion de l'image", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val request = object : StringRequest(
            Method.POST,
            url,
            { response ->
                Toast.makeText(this, "Produit mis à jour avec succès", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivityAdmin::class.java)
                this.startActivity(intent)
                finish()
            },
            { error ->
                Toast.makeText(this, "Erreur lors de la mise à jour: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateProduct", "Error: ${error.message}")
            }
        ) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        queue.add(request)
    }

    // Fonction utilitaire pour convertir l'image en Base64
    private fun convertImageToBase64(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        return if (bytes != null) {
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } else {
            throw Exception("Impossible de lire l'image")
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            selectedImageUri?.let {
                Glide.with(this)
                    .load(it)
                    .into(foodImage)
            }
        }
    }
}