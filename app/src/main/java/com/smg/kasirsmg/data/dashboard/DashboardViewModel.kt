package com.smg.kasirsmg.data.dashboard

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.smg.kasirsmg.model.Penjualan
import com.smg.kasirsmg.model.Pesanan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

class DashboardViewModel() : ViewModel() {
    private var listenerRegistration : ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()

    private val _penjualan = MutableStateFlow<List<Penjualan>>(emptyList())
    val penjualan: StateFlow<List<Penjualan>> = _penjualan

    init {
        fetchPenjualan()
    }

    fun fetchPenjualan() {
        val startOfDay = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6)
            set(Calendar.HOUR_OF_DAY,0)
            set(Calendar.MINUTE,0)
            set(Calendar.SECOND,0)
            set(Calendar.MILLISECOND,0)
        }.time

        val endOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY,23)
            set(Calendar.MINUTE,59)
            set(Calendar.SECOND,59)
            set(Calendar.MILLISECOND,999)
        }.time

        listenerRegistration = db.collection("penjualan")
            .whereGreaterThanOrEqualTo("tanggal", Timestamp(startOfDay))
            .whereLessThanOrEqualTo("tanggal", Timestamp(endOfDay))
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null && !value.isEmpty) {
                    val listPenjualan = value.toObjects(Penjualan::class.java)
                    _penjualan.value = listPenjualan
                }
            }
    }

}