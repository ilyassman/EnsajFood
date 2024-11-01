package com.example.myapplication.AdminUi

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.ClientUi.AccountFragment
import com.example.myapplication.ClientUi.CartFragment
import com.example.myapplication.ClientUi.DemandesAdminFragment
import com.example.myapplication.ClientUi.ListeDemande
import com.example.myapplication.ClientUi.MainFragment
import com.example.myapplication.R
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivityAdmin : AppCompatActivity() {
    private lateinit var chipNavigationBar: ChipNavigationBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_admin)
        chipNavigationBar = findViewById(R.id.bottommenuadmin)
        chipNavigationBar.setOnItemSelectedListener { index ->
            val itemName = when (index) {
                R.id.home -> supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_containeradmin, MainAdminFragment())
                    .commit()
                R.id.demande -> supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_containeradmin, DemandesAdminFragment()) // Assurez-vous d'avoir un conteneur pour le fragment dans votre layout
                    .commit()
                R.id.profile -> supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_containeradmin, AccountFragment()) // Assurez-vous d'avoir un conteneur pour le fragment dans votre layout
                    .commit()
                else -> "Inconnu"
            }
            // Affichez un Toast avec le nom de l'élément cliqué

        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_containeradmin, MainAdminFragment()) // Assurez-vous d'avoir un conteneur pour le fragment dans votre layout
            .commit()
    }
}