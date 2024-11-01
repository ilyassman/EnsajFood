package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.AdminUi.MainActivityAdmin
import com.example.myapplication.ClientUi.BaseActivity
import com.example.myapplication.ClientUi.MainActivity
import com.example.myapplication.databinding.ActivityIntroBinding
import com.example.myapplication.util.EmailService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IntoActivity : BaseActivity()  {
    private lateinit var binding: ActivityIntroBinding;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        binding=ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Créer une instance de EmailService


        binding.btngo.setOnClickListener {
            val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val id = sharedPrefs.getString("id", null)
            val role = sharedPrefs.getString("role", null)
            if(id.isNullOrEmpty()){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)}
            else {
                if(role.isNullOrEmpty()){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this, MainActivityAdmin::class.java)
                    startActivity(intent)
                }

            }
        }

    }
}