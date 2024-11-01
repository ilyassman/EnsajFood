package com.example.myapplication.ClientUi

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapp.CommandeAdapter
import com.example.myapplication.R
import org.json.JSONException
import org.json.JSONObject

class ListeDemande : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val commandeAdapter = CommandeAdapter()
    private lateinit var deleteIcon: Drawable
    private val background = ColorDrawable(Color.RED)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_liste_demande, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialiser les vues
        recyclerView = view.findViewById(R.id.foodlist)
        progressBar = view.findViewById(R.id.progressBarListfood)
        deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.trash)!!


        // Configurer le RecyclerView
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commandeAdapter
        }
        // Configurer le glissement pour suppression
        val itemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // Récupérer la position de l'élément
                    val position = viewHolder.adapterPosition
                    // Afficher le dialogue de confirmation
                    if (commandeAdapter.getCommande(position).status == "en cours") {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage("Voulez-vous vraiment supprimer cette commande ?")
                            .setPositiveButton("Oui") { dialog, _ ->
                                val id = commandeAdapter.getCommande(position).numero

                                // Supprimer l'élément si l'utilisateur confirme
                                DeleteCommande(id.toInt())
                                commandeAdapter.removeAt(position)
                                Toast.makeText(context, "Commande supprimée", Toast.LENGTH_SHORT)
                                    .show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("Non") { dialog, _ ->
                                // Annuler la suppression et restaurer l'élément
                                commandeAdapter.notifyItemChanged(position)
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    } else {
                        Toast.makeText(context, "Commande deja préparé", Toast.LENGTH_SHORT).show()
                        commandeAdapter.notifyItemChanged(position)
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2

                    // Dessiner l'arrière-plan rouge uniquement lors d'un glissement vers la gauche
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    background.draw(c)

                    // Définir la position de l'icône de suppression
                    val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                    val iconBottom = iconTop + deleteIcon.intrinsicHeight
                    val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin

                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteIcon.draw(c)

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            })

        itemTouchHelper.attachToRecyclerView(recyclerView)
        loadFood()

    }
    private fun DeleteCommande(id:Int) {

        val loginUrl = "http://10.0.2.2/foodapp/ws/deleteCommande.php"

        // Créer la requête
        val stringRequest = object : StringRequest(
            Method.POST, loginUrl,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")


                    if (success) {
                        // Connexion réussie
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    } else {
                        // Erreur de connexion
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                   // Toast.makeText(context, "Erreur lors de la connexion", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(context, "Erreur réseau: ${error.message}", Toast.LENGTH_LONG).show()
            }) {

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = id.toString()
                return params
            }
        }

        // Ajouter la requête à la file d'attente
        Volley.newRequestQueue(context).add(stringRequest)
    }
    private fun loadFood() {
        val sharedPrefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val id = sharedPrefs.getString("id", null)
        val url = "http://10.0.2.2/foodapp/ws/findCommandeuser.php?id=${id}"

        val commandesList = mutableListOf<CommandeAdapter.Commande>()

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.POST, url, null,
            { response ->
                try {
                    // Parcourir chaque commande dans le JSON
                    for (i in 0 until response.length()) {
                        val commandeJson = response.getJSONObject(i)

                        // Extraire les informations de la commande
                        val idCommande = commandeJson.getString("idcommande")
                        val montant = commandeJson.getString("montant").toDouble()
                        val status = commandeJson.getString("status")

                        // Récupérer et parser le tableau de produits
                        val produitsArray = commandeJson.getJSONArray("produits")
                        val produits = mutableListOf<CommandeAdapter.ProduitCommande>()

                        // Parcourir chaque produit de la commande
                        for (j in 0 until produitsArray.length()) {
                            val produitJson = produitsArray.getJSONObject(j)
                            val nomProduit = produitJson.getString("nom")
                            val quantite = produitJson.getInt("quantite")
                            produits.add(CommandeAdapter.ProduitCommande(nomProduit, quantite))
                        }

                        // Créer l'objet Commande et l'ajouter à la liste
                        commandesList.add(
                            CommandeAdapter.Commande(
                                numero = idCommande,
                                produits = produits,
                                total = montant,
                                status = status
                            )
                        )
                    }

                    // Mettre à jour l'adaptateur avec les données
                    commandeAdapter.updateCommandes(commandesList)

                    // Cacher le ProgressBar
                    progressBar.visibility = View.GONE

                } catch (e: JSONException) {
                    Log.e("ListeDemande", "Erreur de parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("ListeDemande", "Erreur Volley: ${error.message}")
            }
        )
        Volley.newRequestQueue(context).add(jsonArrayRequest)
    }

}