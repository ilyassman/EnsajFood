package com.example.myapplication.ClientUi

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.adapter.CategoryAdapter
import com.example.myapplication.beans.Category
import org.json.JSONException

class MainFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private val categories = arrayListOf<Category>()
    private lateinit var nom: TextView
    private lateinit var progressBar: ProgressBar // Déclaration du ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate le layout pour ce fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        // Initialisation du RecyclerView
        recyclerView = view.findViewById(R.id.categview)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        progressBar = view.findViewById(R.id.progressBar2)
        nom=view.findViewById(R.id.nomwelcom)
        val sharedPrefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val nomsh = sharedPrefs.getString("nom", null)
        val imageView = view.findViewById<ImageView>(R.id.imageView4)
        Glide.with(this)
            .load("https://ui-avatars.com/api/name=${nomsh}?background=random")
            .apply(RequestOptions.circleCropTransform())
            .override(150, 150) // Définit la taille souhaitée (largeur, hauteur)
            .into(imageView)


        nom.setText(nomsh)
        loadCategories()

        return view
    }

    private fun loadCategories() {
        val url = "http://10.0.2.2/foodapp/ws/loadcateg.php"

        // Création de la requête JSON
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.POST, url, null,
            { response ->
                try {
                    // Parcourir le tableau JSON et ajouter les catégories
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        val imagePath = jsonObject.getString("imagePath")
                        val name = jsonObject.getString("name")
                        categories.add(Category(id, imagePath, name))
                    }
                    // Initialiser l'adaptateur avec les catégories récupérées
                    categoryAdapter = CategoryAdapter(categories)
                    recyclerView.adapter = categoryAdapter
                    progressBar.visibility = ProgressBar.GONE
                    recyclerView.visibility = RecyclerView.VISIBLE
                } catch (e: JSONException) {
                    Log.e("MainFragment", "JSON parsing error: ${e.message}")
                }
            },
            { error ->
                Log.e("MainFragment", "Volley error: ${error.message}")
            }
        )

        // Ajouter la requête à la file de requêtes
        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest)
    }
}