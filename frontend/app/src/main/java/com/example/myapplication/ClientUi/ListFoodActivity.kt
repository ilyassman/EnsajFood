package com.example.myapplication.ClientUi

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.adapter.FoodAdapter
import com.example.myapplication.beans.Food
import org.json.JSONException

class ListFoodActivity : AppCompatActivity() {
    private var listFood= arrayListOf<Food>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var btnback: ImageView
    private lateinit var categlist: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_food)
        // Trouve le RecyclerView dans le layout
        recyclerView = findViewById<RecyclerView>(R.id.foodlist)
        btnback=findViewById(R.id.backbtn)
        btnback.setOnClickListener{
            finish()
        }
        progressBar=findViewById(R.id.progressBarListfood)
        categlist=findViewById(R.id.listfoodcateg)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val categoryId = intent.getIntExtra("category_id",-1)
        val categoryName = intent.getStringExtra("category_name")
        categlist.setText(categoryName)

        loadFood(categoryId)


    }

    private fun loadFood(categoryId:Int) {
        val url = "http://10.0.2.2/foodapp/ws/loadproduit.php?id=${categoryId}"

        // Création de la requête JSON
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.POST, url, null,
            { response ->
                try {
                    // Parcourir le tableau JSON et ajouter les catégories
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        val name = jsonObject.getString("nom")
                        val nombrerating = jsonObject.getString("note")
                        val description = jsonObject.getString("description")
                        val price = jsonObject.getString("prix")
                        val image = jsonObject.getString("image_url")
                        Log.d("namefood", name)
                        listFood.add(
                            Food(
                                id,
                                0,
                                name,
                                description,
                                price.toDouble(),
                                nombrerating.toFloat(),
                                image
                            )
                        )
                    }
                    // Initialiser l'adaptateur avec les catégories récupérées
                    foodAdapter = FoodAdapter(listFood)
                    recyclerView.adapter = foodAdapter
                    progressBar.visibility = ProgressBar.GONE
                    recyclerView.visibility = RecyclerView.VISIBLE
                } catch (e: JSONException) {
                    Log.e("MainActivity", "JSON parsing error: ${e.message}")
                }
            },
            { error ->
                Log.e("MainActivity", "Volley error: ${error.message}")
            }
        )

        // Ajouter la requête à la file de requêtes
        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }
}