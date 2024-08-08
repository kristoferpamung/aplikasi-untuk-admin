package com.smg.kasirsmg.model

import com.google.firebase.Timestamp

data class Pengguna (
    val nama: String = "",
    val email: String = "",
    val fotoProfil: String = "",
    val nomorHp: String = "",
    val isAdmin: Boolean = true,
    val keranjang: List<ProdukItem> = emptyList(),
    val tanggalDaftar: Timestamp = Timestamp.now()
)