package com.kotlin.biteback.ui.productDetail

import androidx.lifecycle.ViewModel
import com.kotlin.biteback.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Product(
    val name: String,
    val price: Double,
    val oldPrice: Double,
    val imageRes: Int,
    val timeLeft: String,
    val savings: Double,
    val store: String,
    val distance: String,
    val description: String
)

class ProductDetailViewModel : ViewModel() {
    private val _product = MutableStateFlow(
        Product(
            name = "Magnificarne",
            price = 12000.0,
            oldPrice = 25000.0,
            imageRes = R.drawable.steak_image,
            timeLeft = "12 horas",
            savings = 13000.0,
            store = "MCpolas",
            distance = "2km",
            description = "Se trata de una gran hamburguesa con queso, cocinando en una hamburguesa grande con dos hamburguesas como bollos, hay muchas capas de queso cheddar derretido, y también lleva Ketchup del Himalaya (Salsa del Himalaya), tiene dos bollos de pan usados como bollos."
        )
    )

    val product: StateFlow<Product> = _product
}
