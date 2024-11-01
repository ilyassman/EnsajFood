package com.example.myapplication.ClientUi

import CartAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.beans.Food
import com.example.myapplication.util.CartManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartFragment : Fragment() {
    private lateinit var adapter: CartAdapter
    private var cartItems = ArrayList<Food>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        val cartManager = CartManager(requireContext())
        val listview = cartManager.getCartItems()
        val emptyCartTxt = view.findViewById<TextView>(R.id.textView3)
        val ordersummary = view.findViewById<TextView>(R.id.textView6)
        val totalconstraint = view.findViewById<ConstraintLayout>(R.id.totalconstraint)
        val checkOutBtn = view.findViewById<Button>(R.id.checkOutBtn)
        if (listview.isEmpty()) {
            ordersummary.visibility = View.GONE
            totalconstraint.visibility = View.GONE
            checkOutBtn.visibility = View.GONE
        }
        emptyCartTxt.visibility = if (listview.isEmpty()) View.VISIBLE else View.GONE
        loadFood(view)
        return view
    }

    private fun loadFood(view: View) {
        val url = "http://10.0.2.2/foodapp/ws/getAllProduct.php"

        // Création de la requête JSON
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.POST, url, null,
            { response ->
                try {
                    val cartManager = CartManager(requireContext())
                    val listview = cartManager.getCartItems()

                    // Parcourir le tableau JSON et ajouter les catégories
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        if (listview.containsKey(id.toString())) {
                            val name = jsonObject.getString("nom")
                            val nombrerating = jsonObject.getString("note")
                            val description = jsonObject.getString("description")
                            val price = jsonObject.getString("prix")
                            val image = jsonObject.getString("image_url")
                            cartItems.add(
                                Food(
                                    id,
                                    listview[id.toString()]?.toInt() ?: 1,
                                    name,
                                    description,
                                    price.toDouble(),
                                    nombrerating.toFloat(),
                                    image
                                )
                            )
                        }
                    }


                    // Initialiser le RecyclerView
                    setupRecyclerView(view)

                    // Configurer les boutons
                    setupClickListeners(view)

                } catch (e: JSONException) {
                    Log.e("CartFragment", "JSON parsing error: ${e.message}")
                }
            },
            { error ->
                Log.e("CartFragment", "Volley error: ${error.message}")
            }
        )

        // Ajouter la requête à la file de requêtes
        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest)
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.cartrecyleview)
        val totalAmountTxt = view.findViewById<TextView>(R.id.totalAmount)
        val emptyCartTxt = view.findViewById<TextView>(R.id.textView3)
        val ordersummary = view.findViewById<TextView>(R.id.textView6)
        val totalconstraint = view.findViewById<ConstraintLayout>(R.id.totalconstraint)
        val checkOutBtn = view.findViewById<Button>(R.id.checkOutBtn)

        adapter = CartAdapter(cartItems) { total ->
            totalAmountTxt.text = "${String.format("%.2f", total)} DH"
            emptyCartTxt.visibility = if (cartItems.isEmpty()) View.VISIBLE else View.GONE
            if (cartItems.isEmpty()) {
                ordersummary.visibility = View.GONE
                totalconstraint.visibility = View.GONE
                checkOutBtn.visibility = View.GONE
            }
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CartFragment.adapter
        }

        // Calculer le total initial
        val initialTotal = cartItems.sumOf { it.price * it.quantity }
        totalAmountTxt.text = "${String.format("%.2f", initialTotal)} DH"
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<View>(R.id.backbtn).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        view.findViewById<View>(R.id.checkOutBtn).setOnClickListener {
            val url = "http://10.0.2.2/foodapp/ws/addCommande.php"

            // Créer le corps de la requête JSON
            val jsonBody = JSONObject()
            val produitsArray = JSONArray()

            // Ajouter les détails de chaque produit dans le panier
            cartItems.forEach { food ->
                val produitObject = JSONObject().apply {
                    put("produit_id", food.id)
                    put("quantite", food.quantity)
                    put("prix_unitaire", food.price)
                }
                produitsArray.put(produitObject)
            }

            // Calculer le total
            val total = cartItems.sumOf { it.price * it.quantity }
            try {
                val sharedPrefs = requireContext().getSharedPreferences("UserPrefs",
                    Context.MODE_PRIVATE
                )
                val id = sharedPrefs.getString("id", null)
                jsonBody.put("iduser", id) // ID de l'utilisateur
                jsonBody.put("total", total) // Montant total réel
                jsonBody.put("produits", produitsArray) // JSONArray des IDs des produits
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            // Créer une requête JSON
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                { response ->
                    Log.d("SUCCESS", response.toString())
                    Toast.makeText(context, "Commande envoyée avec succès", Toast.LENGTH_SHORT)
                        .show()

                    // Vider le panier après succès
                    val cartManager = CartManager(requireContext())
                    cartManager.clearCart()
                    cartItems.clear()
                    adapter.notifyDataSetChanged()

                    // Mettre à jour l'interface
                    view.findViewById<TextView>(R.id.totalAmount).text = "0.00 DH"
                    view.findViewById<TextView>(R.id.textView3).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.textView6).visibility = View.GONE
                    view.findViewById<ConstraintLayout>(R.id.totalconstraint).visibility = View.GONE
                    view.findViewById<Button>(R.id.checkOutBtn).visibility = View.GONE
                },
                { error ->

                    Toast.makeText(context, "Commande envoyée avec succès", Toast.LENGTH_SHORT)
                        .show()

                    // Vider le panier après succès
                    val cartManager = CartManager(requireContext())
                    cartManager.clearCart()
                    cartItems.clear()
                    adapter.notifyDataSetChanged()

                    // Mettre à jour l'interface
                    view.findViewById<TextView>(R.id.totalAmount).text = "0.00 DH"
                    view.findViewById<TextView>(R.id.textView3).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.textView6).visibility = View.GONE
                    view.findViewById<ConstraintLayout>(R.id.totalconstraint).visibility = View.GONE
                    view.findViewById<Button>(R.id.checkOutBtn).visibility = View.GONE
                }
            )

            Volley.newRequestQueue(context).add(jsonObjectRequest)

        }
    }
}