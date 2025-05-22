package com.kotlin.biteback.ui.productDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotlin.biteback.data.repository.ProductDetailRepository

object RepositoryHolder {
    val sharedProductDetailRepository by lazy { ProductDetailRepository() }
}

class ProductDetailViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            return ProductDetailViewModel(RepositoryHolder.sharedProductDetailRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

