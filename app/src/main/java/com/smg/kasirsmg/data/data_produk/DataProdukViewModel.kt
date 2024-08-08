package com.smg.kasirsmg.data.data_produk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.smg.kasirsmg.model.Produk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DataProdukViewModel() : ViewModel () {
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    private val _products = MutableStateFlow<List<Produk>>(emptyList())
    val products: StateFlow<List<Produk>> get() = _products

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        listenerRegistration = db.collection("products")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null && !value.isEmpty) {
                    val productList = value.toObjects(Produk::class.java)
                    _products.value = productList
                }
            }
    }

    fun tambahProduk (
        produk: Produk,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                db.collection("products")
                    .add(produk)
                    .await()
                onSuccess.invoke()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}