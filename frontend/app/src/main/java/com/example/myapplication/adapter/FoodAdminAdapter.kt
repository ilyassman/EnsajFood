package com.example.myapplication.adapter

import android.content.Intent
import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import android.widget.Filter
import android.widget.Filterable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myapplication.R
import com.example.myapplication.activities.EditFoodActivity
import com.example.myapplication.beans.Food

class FoodAdminAdapter(private val originalItems: ArrayList<Food>) :
    RecyclerView.Adapter<FoodAdminAdapter.ViewHolder>(), Filterable {

    // Liste filtrée qui sera affichée
    private var filteredItems: ArrayList<Food> = ArrayList(originalItems)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflate = LayoutInflater.from(context).inflate(R.layout.item_foodadmin, parent, false)
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = filteredItems[position]  // Utiliser filteredItems au lieu de items
        holder.foodImage.clipToOutline = true
        val context1 = holder.itemView.context
        val resources = context1.resources
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditFoodActivity::class.java)
            intent.putExtra("id", food.id)
            intent.putExtra("quantity", food.quantity)
            intent.putExtra("name", food.name)
            intent.putExtra("description", food.desciption)
            intent.putExtra("price", food.price)
            intent.putExtra("rating", food.rating)
            intent.putExtra("imagePath", food.imagePath)

            context.startActivity(intent)
        }

        holder.foodImage.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height,
                    resources.getDimension(R.dimen.image_corner_radius))
            }
        }

        holder.titleTxt.text = food.name
        holder.priceTxt.text = "${food.price} DH"
        holder.ratingTxt.text = food.rating.toString()
        holder.addcart.setOnClickListener{
            // Votre code pour addcart
        }

        val context = holder.itemView.context
        Glide.with(context)
            .load("http://10.0.2.2/foodapp/uploads/produit/${food.imagePath}")
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.image_corner_radius)))
            .into(holder.foodImage)
    }

    override fun getItemCount(): Int {
        return filteredItems.size  // Utiliser filteredItems au lieu de items
    }

    fun getItem(position: Int): Food {
        return filteredItems[position]  // Utiliser filteredItems au lieu de items
    }

    fun removeAt(position: Int) {
        val food = filteredItems[position]
        filteredItems.removeAt(position)
        originalItems.remove(food)
        notifyItemRemoved(position)
    }

    // Implémentation de l'interface Filterable
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchString = constraint?.toString()?.lowercase() ?: ""

                val filtered = if (searchString.isEmpty()) {
                    ArrayList(originalItems)
                } else {
                    ArrayList(originalItems.filter { food ->
                        food.name.lowercase().contains(searchString)
                    })
                }

                return FilterResults().apply {
                    values = filtered
                    count = filtered.size
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as? ArrayList<Food> ?: ArrayList()
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodImage: ImageView = itemView.findViewById(R.id.foodpic)
        val titleTxt: TextView = itemView.findViewById(R.id.titlefood)
        val priceTxt: TextView = itemView.findViewById(R.id.pricefood)
        val ratingTxt: TextView = itemView.findViewById(R.id.ratingfood)
        val addcart: ConstraintLayout = itemView.findViewById(R.id.showdetialbtn)
    }
}