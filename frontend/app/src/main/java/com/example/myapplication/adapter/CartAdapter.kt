import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myapplication.R
import com.example.myapplication.beans.Food
import com.example.myapplication.util.CartManager
import kotlin.math.log

class CartAdapter(
    private val items: ArrayList<Food>,
    private val updateTotalListener: (Double) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodImage: ImageView = itemView.findViewById(R.id.imageView7)
        private val titleTxt: TextView = itemView.findViewById(R.id.textView4)
        private val priceTxt: TextView = itemView.findViewById(R.id.textView10)
        private val minusBtn: TextView = itemView.findViewById(R.id.textView12)
        private val plusBtn: TextView = itemView.findViewById(R.id.textView15)
        private val quantityTxt: TextView = itemView.findViewById(R.id.textView17)
        private val deleteBtn: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(item: Food) {

            // Set item data
            titleTxt.text = item.name
            priceTxt.text = "${item.price} DH"
            quantityTxt.text = item.quantity.toString()
            val context = itemView.context
            val resources = context.resources
            // Load image using Glide
            Glide.with(context)
                .load("http://10.0.2.2/foodapp/uploads/produit/${item.imagePath}")
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.image_corner_radius)))
                .into(foodImage)
            var cartManager: CartManager
            cartManager= CartManager(context)
            // Handle quantity decrease
            minusBtn.setOnClickListener {
                if (item.quantity > 0) {
                    item.quantity--
                    cartManager.addToCart(item.id.toString(),item.quantity)
                    quantityTxt.text = item.quantity.toString()
                    updateTotalPrice()

                    // If quantity reaches 0, remove item
                    if (item.quantity == 0) {
                        cartManager.removeFromCart(item.id.toString())
                        items.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                    }
                }
            }

            // Handle quantity increase
            plusBtn.setOnClickListener {
                item.quantity++
                cartManager.addToCart(item.id.toString(),item.quantity)
                quantityTxt.text = item.quantity.toString()
                updateTotalPrice()
            }

            // Handle item removal
            deleteBtn.setOnClickListener {

                cartManager.removeFromCart(items.get(adapterPosition).id.toString())
                items.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                updateTotalPrice()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    private fun updateTotalPrice() {
        val total = items.sumOf { it.price * it.quantity }
        updateTotalListener(total)
    }
}