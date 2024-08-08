package com.smg.kasirsmg.data.kasir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.smg.kasirsmg.model.Pengguna
import com.smg.kasirsmg.model.Penjualan
import com.smg.kasirsmg.model.Produk
import com.smg.kasirsmg.model.ProdukItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KasirViewModel() : ViewModel() {

    private var listenerRegistration : ListenerRegistration? = null

    private val db = FirebaseFirestore.getInstance()
    private val produkRef = db.collection("products")
    private val userRef = db.collection("users")
    private val idUser = FirebaseAuth.getInstance().currentUser?.uid
    private val penjualanRef = db.collection("penjualan")

    private val _listProduct = MutableStateFlow<List<Produk>>(emptyList())
    val listProduct: StateFlow<List<Produk>> get() = _listProduct

    private val _keranjang = MutableStateFlow<List<ProdukItem>>(emptyList())
    val keranjang : StateFlow<List<ProdukItem>> get() = _keranjang

    init {
        fetchProducts()
        fetchKeranjang()
    }

    fun fetchProducts() {
        listenerRegistration = produkRef
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null && !value.isEmpty) {
                    val productList = value.toObjects(Produk::class.java)
                    _listProduct.value = productList
                }
            }
    }

    fun fetchKeranjang() {
        listenerRegistration = idUser?.let {
            userRef
                .document(it)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        val user = value.toObject(Pengguna::class.java)
                        _keranjang.value = user?.keranjang ?: emptyList()
                    }
                }
        }
    }

    fun updateKeranjang(
        produkItem : ProdukItem,
        onSuccess: (Boolean) -> Unit
    ){
        viewModelScope.launch {
            idUser?.let {
                userRef.document(it)
                    .get()
                    .addOnSuccessListener { user ->
                        val pengguna = user.toObject(Pengguna::class.java)
                        if (pengguna != null) {
                            val daftarItem = pengguna.keranjang.toMutableList()

                            val existingItem = daftarItem.find { item ->
                                item.namaBarang == produkItem.namaBarang && item.satuan == produkItem.satuan
                            }

                            if (existingItem != null) {
                                val updatedJumlah = existingItem.jumlah + produkItem.jumlah
                                val totalSubTotal = produkItem.harga * updatedJumlah

                                val updatedItem = existingItem.copy(
                                    jumlah = updatedJumlah,
                                    subTotal = totalSubTotal
                                )
                                daftarItem.remove(existingItem)
                                daftarItem.add(updatedItem)
                            } else {
                                daftarItem.add(produkItem)
                            }

                            val updatedKeranjang = pengguna.copy(keranjang = daftarItem)

                            userRef.document(idUser)
                                .set(updatedKeranjang)
                                .addOnSuccessListener {
                                    onSuccess.invoke(true)
                                }
                                .addOnFailureListener {
                                    onSuccess.invoke(false)
                                }
                        }
                    }
            }
            onSuccess.invoke(false)
        }
    }

    fun hapusBarangKeranjang (produkItem: ProdukItem, onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            idUser?.let {
                userRef.document(it)
                    .get()
                    .addOnSuccessListener { userDocument ->
                        val pengguna = userDocument.toObject(Pengguna::class.java)
                        if (pengguna != null) {
                            val bukanItemYangDihapus = pengguna.keranjang.filterNot {
                                it.namaBarang == produkItem.namaBarang && it.satuan == produkItem.satuan
                            }

                            userRef.document(idUser)
                                .update("keranjang", bukanItemYangDihapus)
                                .addOnSuccessListener {
                                    onSuccess.invoke(true)
                                }
                                .addOnFailureListener {
                                    onSuccess.invoke(false)
                                }
                        } else {
                            onSuccess.invoke(false)
                        }
                }
            }
        }
    }

    fun createTransaksiPenjualan (penjualan: Penjualan, onSuccess: (Boolean) -> Unit) {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val now = Date()
        viewModelScope.launch {
            penjualanRef.document("PJ${dateFormat.format(now)}").set(penjualan)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess.invoke(true)
                    } else {
                        onSuccess.invoke(false)
                    }
                }
                .addOnFailureListener {
                    onSuccess.invoke(false)
                }
        }
    }

    fun kurangiStok (listItem : List<ProdukItem>) {
        viewModelScope.launch {
            for (item in listItem) {
                val querySnapshot = produkRef.whereEqualTo("nama", item.namaBarang).get().await()
                for (document in querySnapshot.documents){
                    val produk = document.toObject(Produk::class.java)

                    produk?.let {
                        val stokBaru = it.stok - (item.jumlah * item.bobot)
                        if (stokBaru >= 0) {
                            document.reference.update("stok", stokBaru).await()
                        } else {
                            println("Stok tidak mencukupi untuk ${item.namaBarang}")
                        }
                    }
                }
            }
        }
    }

    fun hapusSemuaItem() {
        viewModelScope.launch {
            idUser?.let { userRef.document(it).update("keranjang", emptyList<ProdukItem>()) }
        }
    }
}