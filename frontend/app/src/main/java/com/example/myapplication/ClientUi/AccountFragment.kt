package com.example.myapplication.ClientUi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.example.myapplication.LoginActivity
import com.example.myapplication.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONException
import org.json.JSONObject

class AccountFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Récupérer les SharedPreferences
        val sharedPrefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val nomsh = sharedPrefs.getString("nom", null)!!.split(" ")

        val nom=nomsh[0]
        val prenom=nomsh[1]

        // Trouver le TextView après l'inflation de la vue
        val nomtext = view.findViewById<TextView>(R.id.nameText)
        val image=view.findViewById<ImageView>(R.id.profileImage)
        val logout=view.findViewById<Button>(R.id.logoutButton)
        val prenomtext = view.findViewById<TextView>(R.id.prenomText)
        val emailtext=view.findViewById<TextView>(R.id.emailText)
        logout.setOnClickListener{
            val editor = sharedPrefs.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()


        }
        Glide.with(this)
            .load("https://ui-avatars.com/api/name=${nom}%20${prenom}?background=random")
            .apply(RequestOptions.circleCropTransform())
            .override(150, 150) // Définit la taille souhaitée (largeur, hauteur)
            .into(image)
        if (nomtext != null) {
            // Mettre à jour le texte avec la valeur de nomsh
            nomtext.text = nom ?: "Nom non trouvé" // Affiche un message si nomsh est null
            prenomtext.text=prenom
            emailtext.text=sharedPrefs.getString("email", null)
        }
        val editProfileButton = view.findViewById<Button>(R.id.editProfileButton)

        editProfileButton.setOnClickListener {
            showEditProfileDialog()
        }

        return view // Retourner la vue à la fin
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

        private fun showEditProfileDialog() {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.editdialog, null)

            val editNom = dialogView.findViewById<TextInputEditText>(R.id.editNom)
            val editPrenom = dialogView.findViewById<TextInputEditText>(R.id.editPrenom)
            val editEmail = dialogView.findViewById<TextInputEditText>(R.id.editEmail)
            val editPassword = dialogView.findViewById<TextInputEditText>(R.id.editPassword)

            val sharedPrefs = requireContext().getSharedPreferences("UserPrefs",
                Context.MODE_PRIVATE
            )
            val nomsh = sharedPrefs.getString("nom", "")!!.split(" ")

            editNom.setText(nomsh[0])
            editPrenom.setText(nomsh[1])
            editEmail.setText(sharedPrefs.getString("email", ""))

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Modifier le profil")
                .setView(dialogView)
                .setPositiveButton("Sauvegarder") { dialog, _ ->
                    val newNom = editNom.text.toString()
                    val newPrenom = editPrenom.text.toString()
                    val newEmail = editEmail.text.toString()
                    val newPassword = editPassword.text.toString()

                    if (newNom.isNotEmpty() && newPrenom.isNotEmpty() && newEmail.isNotEmpty()) {
                        val id = sharedPrefs.getString("id", null)
                        if (id != null) {
                            updateProfile(
                                id.toInt(),
                                newNom,
                                newPrenom,
                                newEmail,
                                newPassword
                            )
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Veuillez remplir tous les champs obligatoires",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        private fun updateProfile(id: Int, nom: String, prenom: String, email: String, password: String) {
            val url = "http://10.0.2.2/foodapp/ws/updateEtudaint.php" // Remplacez par votre URL

            val requestQueue = Volley.newRequestQueue(requireContext())



            val stringRequest = object : StringRequest(
                Method.POST, url,
                { response ->

                    try {
                        val jsonResponse = JSONObject(response)
                        val success = jsonResponse.getBoolean("success")
                        val message = jsonResponse.getString("message")

                        if (success) {

                            // Mettre à jour SharedPreferences
                            val sharedPrefs = requireContext().getSharedPreferences("UserPrefs",
                                Context.MODE_PRIVATE
                            )
                            val editor = sharedPrefs.edit()
                            editor.putString("nom", "$nom $prenom")
                            editor.putString("email", email)
                            if (password.isNotEmpty()) {
                                editor.putString("password", password)
                            }
                            editor.apply()

                            // Mettre à jour l'UI
                            view?.let { view ->
                                view.findViewById<TextView>(R.id.nameText)?.text = nom
                                view.findViewById<TextView>(R.id.prenomText)?.text = prenom
                                view.findViewById<TextView>(R.id.emailText)?.text = email

                                // Mettre à jour l'image de profil
                                view.findViewById<ImageView>(R.id.profileImage)?.let { profileImage ->
                                    Glide.with(this)
                                        .load("https://ui-avatars.com/api/name=${nom}%20${prenom}?background=random")
                                        .apply(RequestOptions.circleCropTransform())
                                        .override(150, 150)
                                        .into(profileImage)
                                }
                            }

                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            context,
                            "Erreur lors du traitement de la réponse",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                { error ->

                    Toast.makeText(
                        context,
                        "Erreur de connexion: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["id"] = id.toString()
                    params["nom"] = nom
                    params["prenom"] = prenom
                    params["email"] = email
                    if (password.isNotEmpty()) {
                        params["password"] = password
                    }
                    else
                        params["password"] =""
                    return params
                }
            }
            requestQueue.add(stringRequest)
        }
    }