package com.smg.kasirsmg.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Pesanan (
    @DocumentId val idTransaksi : String = "",
    val tanggal : Timestamp = Timestamp.now(),
    val idPengguna : String = "",
    val namaPengguna: String = "",
    val idKasir : String = "",
    val statusTransaksi : String = "Menunggu Konfirmasi",
    val total : Long = 0,
    val bayar: Long = 0,
    val kembalian: Long = 0,
    val expiredDate : Timestamp = Timestamp.now(),
    val item: List<ProdukItem> = emptyList()
)