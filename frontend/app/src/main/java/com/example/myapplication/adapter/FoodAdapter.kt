package com.example.myapplication.adapter

import android.content.Intent
import android.graphics.Outline
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myapplication.ClientUi.DetailFoodActivity
import com.example.myapplication.ClientUi.ListFoodActivity
import com.example.myapplication.R
import com.example.myapplication.beans.Food
import com.example.myapplication.util.CartManager

class FoodAdapter(private val items: ArrayList<Food>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflate = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false)
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = items[position]
        holder.foodImage.clipToOutline = true
        val context1 = holder.itemView.context
        val resources = context1.resources
        var cartManager: CartManager

        holder.foodImage.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height,
                    resources.getDimension(R.dimen.image_corner_radius))
            }
        }
        cartManager = CartManager(context1)
        // Set food title, price, and rating
        holder.titleTxt.text = food.name
        holder.priceTxt.text = "${food.price} DH"
        holder.ratingTxt.text = food.rating.toString()
        holder.addcart.setOnClickListener{
            cartManager.addToCart(food.id.toString(),1)
            Toast.makeText(context1, food.name+" ajouté au panier ", Toast.LENGTH_SHORT).show()
        }

        // Set food image
        val context = holder.itemView.context

        Glide.with(context)
            .load("http://10.0.2.2/foodapp/uploads/produit/${food.imagePath}")
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.image_corner_radius)))
            .into(holder.foodImage)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailFoodActivity::class.java)
            intent.putExtra("id", food.id)
            intent.putExtra("foodname", food.name)
            intent.putExtra("nombrerating", food.rating)
            intent.putExtra("price", food.price)
            intent.putExtra("description", food.desciption)
            intent.putExtra("image", food.imagePath)


            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodImage: ImageView = itemView.findViewById(R.id.foodpic)
        val titleTxt: TextView = itemView.findViewById(R.id.titlefood)
        val priceTxt: TextView = itemView.findViewById(R.id.pricefood)
        val ratingTxt: TextView = itemView.findViewById(R.id.ratingfood)
        val addcart: ConstraintLayout = itemView.findViewById(R.id.showdetialbtn)
    }
}
