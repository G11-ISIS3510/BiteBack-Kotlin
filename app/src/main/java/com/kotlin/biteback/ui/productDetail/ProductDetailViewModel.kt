package com.kotlin.biteback.ui.productDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.biteback.data.model.Product
import com.kotlin.biteback.data.repository.ProductDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(private val repository: ProductDetailRepository) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _formattedExpirationDate = MutableStateFlow("Cargando...")
    val formattedExpirationDate: StateFlow<String> = _formattedExpirationDate

    fun fetchProduct(productId: String) {
        viewModelScope.launch {
            val fetchedProduct = repository.getProductById(productId)
            fetchedProduct?.let { product ->
                val businessName = repository.getBusinessName(product.businessId)
                val expirationText = formatExpirationDate(product.expirationDate)

                _product.value = product.copy(
                    businessId = businessName
                )
                _formattedExpirationDate.value = expirationText
            }
        }
    }

    private fun formatExpirationDate(timestamp: Long): String {
        val currentTime = System.currentTimeMillis() / 1000
        val remainingSeconds = timestamp - currentTime
        val remainingDays = (remainingSeconds / 86400).toInt()
        return if (remainingDays > 0) "$remainingDays días" else "Expira hoy"
    }
}

