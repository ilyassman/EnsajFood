package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class CommandeAdminAdapter : RecyclerView.Adapter<CommandeAdminAdapter.CommandeViewHolder>() {

    // Garder une liste de toutes les commandes originales
    private var allCommandes: MutableList<Commande> = mutableListOf()
    // Liste des commandes filtrées à afficher
    private var commandes: MutableList<Commande> = mutableListOf()

    // Class pour représenter une commande
    data class Commande(
        val numero: String,
        val produits: List<ProduitCommande>,
        val total: Double,
        var status: String
    )

    // Class pour représenter un produit dans la commande
    data class ProduitCommande(
        val nom: String,
        val quantite: Int
    )

    inner class CommandeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNumeroCommande: TextView = itemView.findViewById(R.id.tvNumeroCommande)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvNomProduits: TextView = itemView.findViewById(R.id.tvNomProduits)
        private val tvQuantites: TextView = itemView.findViewById(R.id.tvQuantites)
        private val tvTotalMontant: TextView = itemView.findViewById(R.id.tvTotalMontant)

        fun bind(commande: Commande) {
            // Afficher le numéro de commande
            tvNumeroCommande.text = "Commande #${commande.numero}"

            // Afficher le status
            tvStatus.text = commande.status
            // Changer la couleur du background selon le status
            tvStatus.setBackgroundResource(
                if (commande.status.lowercase() == "en cours")
                    R.color.status_en_cours
                else
                    R.color.status_prepare
            )

            // Construire la liste des produits et quantités
            val produitsText = StringBuilder()
            val quantitesText = StringBuilder()

            commande.produits.forEach { produit ->
                produitsText.append("${produit.nom}\n")
                quantitesText.append("x${produit.quantite}\n")
            }

            // Afficher les produits et quantités
            tvNomProduits.text = produitsText.toString().trim()
            tvQuantites.text = quantitesText.toString().trim()

            // Afficher le total
            tvTotalMontant.text = "${commande.total} DH"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommandeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_commande, parent, false)
        return CommandeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommandeViewHolder, position: Int) {
        holder.bind(commandes[position])
    }

    override fun getItemCount() = commandes.size
    fun getCommande(position:Int): Commande {
        return commandes.get(position)
    }

    fun updateCommandes(newCommandes: List<Commande>) {
        // Mettre à jour les deux listes
        allCommandes = newCommandes.toMutableList()
        commandes = newCommandes.toMutableList()
        notifyDataSetChanged()
    }
    fun updateStatus(position: Int, status: String) {
        // Mettre à jour le status dans les deux listes
        val commande = commandes[position]
        val originalIndex = allCommandes.indexOfFirst { it.numero == commande.numero }

        if (originalIndex != -1) {
            allCommandes[originalIndex].status = status
        }
        commandes[position].status = status
        notifyDataSetChanged()
    }
    fun filteredbyStatus(status: String) {
        // Si status est vide ou "all", afficher toutes les commandes
        if (status.isEmpty() || status.lowercase() == "all") {
            commandes = allCommandes.toMutableList()
        } else {
            // Filtrer les commandes selon le status
            commandes = allCommandes.filter { commande ->
                commande.status.lowercase() == status.lowercase()
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}