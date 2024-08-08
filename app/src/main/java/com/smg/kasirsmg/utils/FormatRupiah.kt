package com.smg.kasirsmg.utils

import java.text.NumberFormat
import java.util.Locale

fun formatRupiah (number : Long) : String {
    val localeID = Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    numberFormat.maximumFractionDigits = 0
    return numberFormat.format(number)
}