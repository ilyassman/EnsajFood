package com.example.myapplication.AdminUi
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
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
import com.example.myapplication.adapter.FoodAdminAdapter
import com.example.myapplication.beans.Category
import com.example.myapplication.beans.Food
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainAdminFragment : Fragment() {
    private var listFood = arrayListOf<Food>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdminAdapter
    private lateinit var progressBar: ProgressBar
    private var selectedImageUri: Uri? = null
    private lateinit var searchView: SearchView

    private lateinit var deleteIcon: Drawable
    private val background = ColorDrawable(Color.RED)

    private lateinit var  btnChooseImage: Button
    private lateinit var floatadd: FloatingActionButton
    private val categories = arrayListOf<Category>()


    private lateinit var categlist: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_admin, container, false)
        deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.trash)!!
        // Initialiser les vues
        initializeViews(view)
        searchView = view.findViewById(R.id.searchView)

        // Configurer le RecyclerView
        setupRecyclerView()
        val itemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // Récupérer la position de l'élément
                    val position = viewHolder.adapterPosition
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("Voulez-vous vraiment supprimer cette commande ?")
                        .setPositiveButton("Oui") { dialog, _ ->
                           val id = foodAdapter.getItem(position).id

                            // Supprimer l'élément si l'utilisateur confirme
                            DeleteCommande(id)
                           foodAdapter.removeAt(position)
                            Toast.makeText(context, "Produit supprimée", Toast.LENGTH_SHORT)
                                .show()
                            dialog.dismiss()
                        }
                        .setNegativeButton("Non") { dialog, _ ->
                            // Annuler la suppression et restaurer l'élément
                            foodAdapter.notifyItemChanged(position)
                            dialog.dismiss()
                        }
                        .create()
                        .show()

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
                    val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2

                    // Dessiner l'arrière-plan rouge uniquement lors d'un glissement vers la gauche
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    background.draw(c)

                    // Définir la position de l'icône de suppression
                    val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                    val iconBottom = iconTop + deleteIcon.intrinsicHeight
                    val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin

                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteIcon.draw(c)

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
        // Charger les données
        loadFood()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                foodAdapter.filter.filter(newText)
                return true
            }
        })

        return view
    }
    private fun DeleteCommande(id:Int) {
        val loginUrl = "http://10.0.2.2/foodapp/ws/deleteProduct.php"

        // Créer la requête
        val stringRequest = object : StringRequest(
            Method.POST, loginUrl,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")


                    if (success) {
                        // Connexion réussie
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
                params["id"] = id.toString()
                return params
            }
        }

        // Ajouter la requête à la file d'attente
        Volley.newRequestQueue(context).add(stringRequest)
    }
    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.foodlist)
        floatadd=view.findViewById(R.id.fab_add)
        progressBar = view.findViewById(R.id.progressBarListfood)
        categlist = view.findViewById(R.id.listfoodcateg)


        floatadd.setOnClickListener {
            // Charger les catégories d'abord et attendre la réponse
            val url = "http://10.0.2.2/foodapp/ws/loadcateg.php"
            categories.clear() // Vider la liste avant de charger

            val jsonArrayRequest = JsonArrayRequest(
                Request.Method.POST, url, null,
                { response ->
                    try {
                        // Parcourir le tableau JSON et ajouter les catégories
                        for (i in 0 until response.length()) {
                            val jsonObject = response.getJSONObject(i)
                            val id = jsonObject.getInt("id")
                            val imagePath = jsonObject.getString("imagePath")
                            val name = jsonObject.getString("name")
                            categories.add(Category(id, imagePath, name))
                        }

                        // Une fois les catégories chargées, afficher le dialogue
                        showAddProductDialog()

                    } catch (e: JSONException) {
                        Log.e("MainFragment", "JSON parsing error: ${e.message}")
                        Toast.makeText(context, "Erreur lors du chargement des catégories", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Log.e("MainFragment", "Volley error: ${error.message}")
                    Toast.makeText(context, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                }
            )

            Volley.newRequestQueue(requireContext()).add(jsonArrayRequest)
        }
    }
    private var imagePreview: ImageView? = null
    private fun showAddProductDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.dialog_add_product)
            .create()

        dialog.show()

        // Récupérer les références des vues du dialogue
        val dialogView = dialog.findViewById<View>(android.R.id.content)?.parent as View
        val editName = dialogView.findViewById<TextInputEditText>(R.id.edit_product_name)
        val editDescription = dialogView.findViewById<TextInputEditText>(R.id.edit_product_description)
        val editPrice = dialogView.findViewById<TextInputEditText>(R.id.edit_product_price)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinner_category)
        imagePreview = dialogView.findViewById<ImageView>(R.id.image_preview)
        val btnChooseImage = dialogView.findViewById<Button>(R.id.btn_choose_image)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnAdd = dialogView.findViewById<Button>(R.id.btn_add)

        // Configurer le spinner avec les catégories
        val categoryNames = categories.map { it.name }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Pour récupérer l'ID de la catégorie sélectionnée
        fun getSelectedCategoryId(): Int {
            val selectedPosition = spinnerCategory.selectedItemPosition
            return categories[selectedPosition].id
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

            btnAdd.setOnClickListener {
                val name = editName.text.toString()
                val description = editDescription.text.toString()
                val price = editPrice.text.toString()
                val categoryId = getSelectedCategoryId()

                if (name.isEmpty() || description.isEmpty() || price.isEmpty() || selectedImageUri == null) {
                    Toast.makeText(context, "Veuillez remplir tous les champs et sélectionner une image", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Convertir l'image en Base64
                val imageBase64 = convertImageToBase64(selectedImageUri!!)

                // Créer l'URL de votre API
                val url = "http://10.0.2.2/foodapp/ws/addProduit.php"



                // Créer les paramètres
                val params = HashMap<String, String>()
                params["nom"] = name
                params["description"] = description
                params["prix"] = price
                params["categorie_id"] = categoryId.toString()
                params["image"] = "data:image/jpeg;base64,$imageBase64"

                // Faire la requête POST
                val request = object : StringRequest(
                    Method.POST, url,
                    { response ->

                        Toast.makeText(context, "Produit ajouté avec succès", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        // Recharger la liste des produits
                        listFood.clear()
                        loadFood()
                    },
                    { error ->

                        Toast.makeText(context, "Erreur lors de l'ajout du produit", Toast.LENGTH_SHORT).show()
                        Log.e("AddProduct", "Error: ${error.message}")
                    }) {
                    override fun getParams(): Map<String, String> {
                        return params
                    }

                }

                // Ajouter la requête à la file d'attente
                Volley.newRequestQueue(context).add(request)
            }
           

        val PICK_IMAGE_REQUEST = 1
        btnChooseImage.setOnClickListener {
            // Créer un Intent pour ouvrir la galerie
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Sélectionner une image"), PICK_IMAGE_REQUEST)
        }


    }

    private fun convertImageToBase64(uri: Uri): String {
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            try {
                // Charger l'image dans l'ImageView
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            requireActivity().contentResolver,
                            selectedImageUri!!
                        )
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)
                }
                imagePreview?.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadFood() {
        val url = "http://10.0.2.2/foodapp/ws/getAllProduct.php"

        // Création de la requête JSON
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.POST, url, null,
            { response ->
                try {
                    // Parcourir le tableau JSON et ajouter les catégories
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        val name = jsonObject.getString("nom")
                        val nombrerating = jsonObject.getString("note")
                        val description = jsonObject.getString("description")
                        val price = jsonObject.getString("prix")
                        val image = jsonObject.getString("image_url")
                        Log.d("namefood", name)
                        listFood.add(
                            Food(
                                id,
                                0,
                                name,
                                description,
                                price.toDouble(),
                                nombrerating.toFloat(),
                                image
                            )
                        )
                    }
                    // Initialiser l'adaptateur avec les catégories récupérées
                    foodAdapter = FoodAdminAdapter(listFood)
                    recyclerView.adapter = foodAdapter
                    progressBar.visibility = ProgressBar.GONE
                    recyclerView.visibility = RecyclerView.VISIBLE
                } catch (e: JSONException) {
                    Log.e("MainActivity", "JSON parsing error: ${e.message}")
                }
            },
            { error ->
                Log.e("MainActivity", "Volley error: ${error.message}")
            }
        )

        // Ajouter la requête à la file de requêtes
        Volley.newRequestQueue(context).add(jsonArrayRequest)
    }

}