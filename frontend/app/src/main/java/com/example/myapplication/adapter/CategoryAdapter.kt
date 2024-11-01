package com.example.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ClientUi.ListFoodActivity
import com.example.myapplication.R
import com.example.myapplication.beans.Category

class CategoryAdapter(private val items: ArrayList<Category>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflate = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleTxt.text = items[position].name
        val category = items[position]
        val resourceId = holder.itemView.context.resources.getIdentifier(items[position].imagePath, "drawable", holder.itemView.context.packageName)
        holder.pic.setImageResource(resourceId)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ListFoodActivity::class.java) // L'activité que tu veux démarrer
            intent.putExtra("category_id", category.id)  // Passer l'id de la catégorie
            intent.putExtra("category_name", category.name)  // Passer le nom de la catégorie
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTxt: TextView = itemView.findViewById(R.id.titlecateg)
        val pic: ImageView = itemView.findViewById(R.id.imagecateg)
    }
}

