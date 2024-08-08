package com.smg.kasirsmg.data.laporan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.smg.kasirsmg.R
import com.smg.kasirsmg.model.Penjualan
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun LaporanScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember {
        mutableStateOf(false)
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
    ) {
        Text(text = "Laporan", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row (
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedCard (
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        val penjualanHariIni = getPenjualanHariIni()
                        val bulan = getCurrentDate()
                        val file = createExcelFile(context = context,bulan,penjualanHariIni)
                        openFile(context = context, file)
                        isLoading = false
                    }
                }
            ) {
                Column (modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(id = R.drawable.file_earmark), contentDescription = "", modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Laporan Harian")
                }
            }
            OutlinedCard (
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        val penjualanBulanIni = getPenjualanBulanan()
                        val bulan = getCurrentMonthName()
                        val file = createExcelFile(context = context,bulan,penjualanBulanIni)
                        isLoading = false
                        openFile(context = context, file)
                    }


                }
            ) {
                Column (modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(id = R.drawable.file_earmark), contentDescription = "", modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Laporan Bulanan")
                }
            }
        }
    }
    if (isLoading){
        Dialog(onDismissRequest = {  }) {
            Surface {
                Column (
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Mohon Tunggu Sebentar")
                }
            }
        }
    }
}

suspend fun getPenjualanHariIni(): List<Penjualan> {
    val firestore = FirebaseFirestore.getInstance()
    val penjualanList = mutableListOf<Penjualan>()
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfDay = calendar.time

    val document = firestore.collection("penjualan")
        .whereGreaterThanOrEqualTo("tanggal", startOfDay)
        .get()
        .await()

    for(penjualan in document.documents){
        penjualan.toObject(Penjualan::class.java)?.let { pjl ->
            penjualanList.add(pjl)
        }
    }

    return penjualanList
}

suspend fun getPenjualanBulanan(): List<Penjualan> {
    val firestore = FirebaseFirestore.getInstance()
    val penjualanList = mutableListOf<Penjualan>()
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    calendar.add(Calendar.MONTH, -1)
    val startOfDay = calendar.time

    val document = firestore.collection("penjualan")
        .whereGreaterThanOrEqualTo("tanggal", startOfDay)
        .get()
        .await()

    for(penjualan in document.documents){
        penjualan.toObject(Penjualan::class.java)?.let { pjl ->
            penjualanList.add(pjl)
        }
    }

    return penjualanList
}

fun createExcelFile(context: Context, bulan: String, penjualanList: List<Penjualan>): File {
    val workbook: Workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("Laporan Penjualan $bulan")

    // Buat header
    val headerRow = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("ID")
    headerRow.createCell(1).setCellValue("Tanggal")
    headerRow.createCell(2).setCellValue("Nama Pembeli")
    headerRow.createCell(3).setCellValue("Nama Barang")
    headerRow.createCell(4).setCellValue("Jumlah")
    headerRow.createCell(5).setCellValue("Harga")
    headerRow.createCell(6).setCellValue("Total Harga")

    // Isi data penjualan
    var totalPenjualan = 0.0
    var rowIndex = 1
    for (penjualan in penjualanList) {
        for (i in penjualan.item) {
            val row = sheet.createRow(rowIndex++)
            row.createCell(0).setCellValue(penjualan.idTransaksi)
            row.createCell(1).setCellValue(formatDate(penjualan.tanggal.toDate()))
            row.createCell(2).setCellValue(penjualan.namaPembeli)
            row.createCell(3).setCellValue(i.namaBarang)
            row.createCell(4).setCellValue(i.jumlah.toString() + " " +i.satuan)
            row.createCell(5).setCellValue(i.harga.toString())
            row.createCell(6).setCellValue(i.subTotal.toString())
        }
        totalPenjualan += penjualan.total
    }

    val totalRow = sheet.createRow(rowIndex)
    val cellStyle: CellStyle = workbook.createCellStyle()
    cellStyle.alignment = HorizontalAlignment.CENTER

    val mergedCell = totalRow.createCell(0)
    mergedCell.setCellValue("Total Penjualan")
    mergedCell.cellStyle = cellStyle

    sheet.addMergedRegion(CellRangeAddress(rowIndex, rowIndex, 0, 5))

    totalRow.createCell(6).setCellValue(totalPenjualan)

    // Simpan file
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "laporan-$bulan.xlsx")
    FileOutputStream(file).use { outputStream ->
        workbook.write(outputStream)
    }
    workbook.close()

    return file
}

fun formatDate(timestamp: Date): String {
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    return dateFormat.format(timestamp)
}

fun getCurrentMonthName(): String {
    val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
    return dateFormat.format(Date())
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}

fun openFile(context: Context, file: File) {
    val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    context.startActivity(intent)
}