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
import com.example.myapplication.R
import com.example.myapplication.adapter.CommandeAdminAdapter
import com.google.android.material.chip.Chip
import org.json.JSONException
import org.json.JSONObject

class DemandesAdminFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnall: Chip
    private lateinit var btnencours: Chip
    private lateinit var btnprepare: Chip
    private lateinit var progressBar: ProgressBar
    private val commandeAdapter = CommandeAdminAdapter()
    private lateinit var deleteIcon: Drawable
    private val background = ColorDrawable(Color.RED)
    private lateinit var checkIcon: Drawable
    private lateinit var clockIcon: Drawable
    private val backgroundGreen = ColorDrawable(Color.parseColor("#4CAF50"))  // Vert
    private val backgroundOrange = ColorDrawable(Color.parseColor("#FF9800"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_liste_demandesadmin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialiser les vues
        recyclerView = view.findViewById(R.id.foodlist)
        progressBar = view.findViewById(R.id.progressBarListfood)
        btnall=view.findViewById(R.id.chipAll)
        btnencours=view.findViewById(R.id.chipEnCours)
        btnprepare=view.findViewById(R.id.chipPrepare)
        btnprepare.setOnClickListener{
            commandeAdapter.filteredbyStatus("terminée")
        }
        btnencours.setOnClickListener{
            commandeAdapter.filteredbyStatus("en cours")
        }
        btnall.setOnClickListener{
            commandeAdapter.filteredbyStatus("all")
        }
        // Initialiser les icônes
        checkIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!  // Icône pour "préparé"
        clockIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clock)!!


        // Configurer le RecyclerView
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commandeAdapter
        }
        // Configurer le glissement pour suppression
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val commande = commandeAdapter.getCommande(position)

                if(commande.status == "en cours") {
                    // Changer le statut à "préparé"
                    Toast.makeText(context, "Commande marquée comme préparée", Toast.LENGTH_SHORT).show()
                    updateCommande(commande.numero,"terminée")
                    commandeAdapter.updateStatus(position,"terminée")

                } else {
                    Toast.makeText(context, "Commande marquée en cours", Toast.LENGTH_SHORT).show()
                    updateCommande(commande.numero,"en cours")
                    commandeAdapter.updateStatus(position,"en cours")
                }
                commandeAdapter.notifyItemChanged(position)
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
                val position = viewHolder.adapterPosition
                val commande = commandeAdapter.getCommande(position)

                // Sélectionner l'icône et la couleur en fonction du statut
                val (icon, background) = if(commande.status == "en cours") {
                    Pair(checkIcon, backgroundGreen)
                } else {
                    Pair(clockIcon, backgroundOrange)
                }

                val iconMargin = (itemView.height - icon.intrinsicHeight) / 2

                // Dessiner l'arrière-plan
                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)

                // Dessiner l'icône
                val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                val iconBottom = iconTop + icon.intrinsicHeight
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin

                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                icon.draw(c)

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
    private fun updateCommande(id:String,status:String) {
        // URL de votre API (à modifier selon votre configuration)
        val loginUrl = "http://10.0.2.2/foodapp/ws/updateCommande.php"

        // Créer la requête
        val stringRequest = object : StringRequest(
            Method.POST, loginUrl,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")


                    if (success) {

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
                params["id"] = id
                params["status"] = status
                return params
            }
        }

        // Ajouter la requête à la file d'attente
        Volley.newRequestQueue(context).add(stringRequest)
    }
    private fun loadFood() {
        val sharedPrefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val url = "http://10.0.2.2/foodapp/ws/getAllCommande.php"

        val commandesList = mutableListOf<CommandeAdminAdapter.Commande>()

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
                        val produits = mutableListOf<CommandeAdminAdapter.ProduitCommande>()

                        // Parcourir chaque produit de la commande
                        for (j in 0 until produitsArray.length()) {
                            val produitJson = produitsArray.getJSONObject(j)
                            val nomProduit = produitJson.getString("nom")
                            val quantite = produitJson.getInt("quantite")
                            produits.add(CommandeAdminAdapter.ProduitCommande(nomProduit, quantite))
                        }

                        // Créer l'objet Commande et l'ajouter à la liste
                        commandesList.add(
                            CommandeAdminAdapter.Commande(
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