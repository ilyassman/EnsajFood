package com.example.myapplication.ClientUi

import android.graphics.Outline
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myapplication.R
import com.example.myapplication.util.CartManager

class DetailFoodActivity : AppCompatActivity() {
    private lateinit var nomproduit: TextView
    private lateinit var descreption: TextView
    private lateinit var price: TextView
    private lateinit var btnaddcart: Button
    private lateinit var btnback: ImageView
    private lateinit var ratingBar: RatingBar
    private lateinit var imagefood: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_food)
        nomproduit=findViewById(R.id.titlefood)
        descreption=findViewById(R.id.descreption)
        imagefood=findViewById(R.id.productimage)
        btnaddcart=findViewById(R.id.buttonaddcart2)
        btnback=findViewById(R.id.backbtn)
        imagefood.clipToOutline = true
        imagefood.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height,
                    resources.getDimension(R.dimen.image_corner_radius))
            }
        }

        btnback.setOnClickListener{
            finish()
        }
        price=findViewById(R.id.pricefood)
        ratingBar=findViewById(R.id.ratingBar)
        val id=intent.getIntExtra("id",0)
        val name =intent.getStringExtra("foodname")
        val nombrerating=intent.getFloatExtra("nombrerating",0f)
        val price=  intent.getDoubleExtra("price", 0.0)
        val description =  intent.getStringExtra("description")
        val image= intent.getStringExtra("image")
        var cartManager: CartManager
        cartManager= CartManager(this)
        btnaddcart.setOnClickListener{
            cartManager.addToCart(id.toString(),1)
            Toast.makeText(this, name + " ajouté au panier ", Toast.LENGTH_SHORT).show()
        }
        descreption.setText(description)
        this.price.setText(""+price+" DH")
        ratingBar.setRating(nombrerating.toFloat())
        Glide.with(this)
            .load("http://10.0.2.2/foodapp/uploads/produit/$image")
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.image_corner_radius)))
            .into(imagefood)
        nomproduit.setText(name)


    }

}