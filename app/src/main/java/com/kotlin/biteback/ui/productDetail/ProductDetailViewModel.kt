package com.kotlin.biteback.ui.productDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.biteback.data.model.Business
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

    private val _businessLat = MutableStateFlow<Double?>(null)
    val businessLat: StateFlow<Double?> = _businessLat

    private val _businessLng = MutableStateFlow<Double?>(null)
    val businessLng: StateFlow<Double?> = _businessLng

    private val _business = MutableStateFlow<Business?>(null)
    val business: StateFlow<Business?> = _business


    fun fetchProduct(productId: String) {
        viewModelScope.launch {
            val fetchedProduct = repository.getProductById(productId)
            fetchedProduct?.let { product ->
                val business = repository.getBusinessById(product.businessId)
                val expirationText = formatExpirationDate(product.expirationDate)

                _product.value = product
                _business.value = business
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
