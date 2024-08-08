package com.smg.kasirsmg.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Penjualan (
    @DocumentId val idTransaksi : String = "",
    val tanggal : Timestamp = Timestamp.now(),
    val idPembeli : String = "",
    val namaPembeli: String = "",
    val idKasir : String = "",
    val total : Long = 0,
    val bayar: Long = 0,
    val kembalian: Long = 0,
    val item: List<ProdukItem> = emptyList()
)