package com.smg.kasirsmg.model

data class ProdukItem (
    val namaBarang: String = "",
    val satuan: String = "",
    val bobot: Double = 1.0,
    var jumlah: Long = 0,
    val harga: Long = 0,
    val subTotal: Long = 0
)