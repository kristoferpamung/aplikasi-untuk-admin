package com.smg.kasirsmg.model

import com.google.firebase.firestore.DocumentId

data class Produk (
    @DocumentId val id: String ="",
    val nama: String = "",
    val stok: Double = 0.0,
    val satuan: String = "",
    val hargaProduk: List<HargaProduk> = emptyList()
)

data class HargaProduk (
    val amount: Double = 0.0,
    val satuan: String = "",
    val harga: Long = 0
)