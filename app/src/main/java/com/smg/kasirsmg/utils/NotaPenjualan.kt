package com.smg.kasirsmg.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.smg.kasirsmg.model.Penjualan
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun createNota(context: Context, penjualan: Penjualan) : File {
    val width = 165
    var height = 200 + penjualan.item.size * 20
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
    val page = document.startPage(pageInfo)
    val canvas: Canvas = page.canvas
    val paint = Paint()

    val centerX = width / 2f

    // Title: "Toko Bagus" centered
    paint.textSize = 10f
    paint.textAlign = Paint.Align.CENTER
    canvas.drawText("Toko SMG Gombong", centerX, 20f, paint)

    var yPosition = 40f

    // Subtitle: "Nota Penjualan" aligned to the left
    paint.textSize = 6f
    paint.textAlign = Paint.Align.LEFT
    canvas.drawText("Nota Penjualan", 10f, yPosition, paint)
    yPosition += paint.descent() - paint.ascent()

    // Date
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val date = dateFormat.format(Date())
    paint.textSize = 6f
    canvas.drawText("Tanggal: $date", 10f, yPosition, paint)
    yPosition += paint.descent() - paint.ascent()

    // Buyer Name
    paint.textSize = 6f
    canvas.drawText("Nama Pembeli: ${penjualan.namaPembeli}", 10f, yPosition, paint)
    yPosition += paint.descent() - paint.ascent()

    paint.textSize = 6f
    canvas.drawText(" ", 10f, yPosition, paint)
    yPosition += paint.descent() - paint.ascent()
    paint.textSize = 6f
    canvas.drawText("Daftar belanjaan:", 10f, yPosition, paint)
    yPosition += paint.descent() - paint.ascent()
    // Items
    paint.textSize = 5f
    penjualan.item.forEach { item ->
        canvas.drawText("${item.namaBarang} - ${item.jumlah} x ${formatRupiah(item.harga) } = ${formatRupiah(item.subTotal)}", 10f, yPosition, paint)
        yPosition += paint.descent() - paint.ascent()
    }
    paint.textSize = 6f
    canvas.drawText(" ", 10f, yPosition, paint)
    yPosition += paint.descent() - paint.ascent()


    // Total Price
    paint.textSize = 6f
    canvas.drawText("Total Harga: ${formatRupiah(penjualan.total) }", 10f, yPosition, paint)
    yPosition += paint.descent() - paint.ascent()

    paint.textSize = 6f
    canvas.drawText(" ", 10f, yPosition, paint)
    yPosition += paint.descent() - paint.ascent()
    paint.textSize = 6f
    canvas.drawText(" ", 10f, yPosition, paint)
    yPosition += paint.descent() - paint.ascent()
    paint.textSize = 6f
    canvas.drawText("Terima Kasih", 10f, yPosition, paint)
    yPosition += paint.descent() - paint.ascent()



    document.finishPage(page)

    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "${penjualan.idTransaksi}.pdf")
    document.writeTo(FileOutputStream(file))
    document.close()

    return file
}

fun openPdf(context: Context, file: File) {
    val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, "application/pdf")
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    context.startActivity(intent)
}