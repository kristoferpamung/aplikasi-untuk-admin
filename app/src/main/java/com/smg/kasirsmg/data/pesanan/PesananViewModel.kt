package com.smg.kasirsmg.data.pesanan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.smg.kasirsmg.model.Pesanan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PesananViewModel : ViewModel() {

    private var listenerRegistration : ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()

    private val _pesanan = MutableStateFlow<List<Pesanan>>(emptyList())
    val pesanan: StateFlow<List<Pesanan>> = _pesanan

    init {
        fetchPesanan()
    }

    fun fetchPesanan() {
        listenerRegistration = db.collection("transaksi")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null && !value.isEmpty) {
                    val pesanan = value.toObjects(Pesanan::class.java)
                    _pesanan.value = pesanan
                }
            }
    }

    fun changeStatus(id: String, status: String, onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            db.collection("transaksi")
                .document(id)
                .update("statusTransaksi", status)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess.invoke(true)
                    } else {
                        onSuccess.invoke(false)

                    }
                }
        }
    }

    fun changePesananToUser(id: String, status: String, idKasir: String, bayar: Long, kembalian: Long) {

        val updates = mapOf<String, Any>(
            "idKasir" to idKasir,
            "bayar" to bayar,
            "statusTransaksi" to status,
            "kembalian" to kembalian
        )

        viewModelScope.launch {
            db.collection("transaksi")
                .document(id)
                .update(updates)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        println("OK")
                    } else {
                        println("Not OK")
                    }
                }
        }
    }

}