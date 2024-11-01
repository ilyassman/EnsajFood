package com.example.myapplication.ClientUi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivity : AppCompatActivity() {
    private lateinit var chipNavigationBar: ChipNavigationBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chipNavigationBar = findViewById(R.id.bottommenu)
        chipNavigationBar.setOnItemSelectedListener { index ->
            val itemName = when (index) {
                R.id.home -> supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MainFragment())
                    .commit()
                R.id.cart -> supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CartFragment()) // Assurez-vous d'avoir un conteneur pour le fragment dans votre layout
                    .commit()
                R.id.demande -> supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ListeDemande()) // Assurez-vous d'avoir un conteneur pour le fragment dans votre layout
                    .commit()
                R.id.profile -> supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AccountFragment()) // Assurez-vous d'avoir un conteneur pour le fragment dans votre layout
                    .commit()
                else -> "Inconnu"
            }
            // Affichez un Toast avec le nom de l'élément cliqué

        }
        // Charger le fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment()) // Assurez-vous d'avoir un conteneur pour le fragment dans votre layout
            .commit()
    }
}