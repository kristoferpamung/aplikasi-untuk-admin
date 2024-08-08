package com.smg.kasirsmg.data.dashboard

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smg.kasirsmg.R
import com.smg.kasirsmg.model.Penjualan
import com.smg.kasirsmg.ui.components.CardDashboard
import com.smg.kasirsmg.utils.createNota
import com.smg.kasirsmg.utils.formatRupiah
import com.smg.kasirsmg.utils.isTimestampH2
import com.smg.kasirsmg.utils.isTimestampH3
import com.smg.kasirsmg.utils.isTimestampH4
import com.smg.kasirsmg.utils.isTimestampH5
import com.smg.kasirsmg.utils.isTimestampH6
import com.smg.kasirsmg.utils.isTimestampToday
import com.smg.kasirsmg.utils.isTimestampYesterday
import com.smg.kasirsmg.utils.openPdf

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen (
    dashboardViewModel: DashboardViewModel = viewModel(modelClass = DashboardViewModel::class.java)
) {
    val pesanan by dashboardViewModel.penjualan.collectAsState()

    val context = LocalContext.current


    var penjualanHariIni = mutableListOf<Penjualan>()
    var penjualanH1 = mutableListOf<Penjualan>()
    var penjualanH2 = mutableListOf<Penjualan>()
    var penjualanH3 = mutableListOf<Penjualan>()
    var penjualanH4 = mutableListOf<Penjualan>()
    var penjualanH5 = mutableListOf<Penjualan>()
    var penjualanH6 = mutableListOf<Penjualan>()

    pesanan.forEach {
        if(isTimestampToday(it.tanggal)) {
            penjualanHariIni.add(it)
        }
        if (isTimestampYesterday(it.tanggal)){
            penjualanH1.add(it)
        }
        if (isTimestampH2(it.tanggal)){
            penjualanH2.add(it)
        }
        if (isTimestampH3(it.tanggal)){
            penjualanH3.add(it)
        }
        if (isTimestampH4(it.tanggal)){
            penjualanH4.add(it)
        }
        if (isTimestampH5(it.tanggal)){
            penjualanH5.add(it)
        }
        if (isTimestampH6(it.tanggal)){
            penjualanH6.add(it)
        }
    }

    val pendapatanHariIni = penjualanHariIni.sumOf {
        it.total
    }
    val pendapatanKemarin = penjualanH1.sumOf {
        it.total
    }

    val persen = persenHariIni(pendapatanHariIni.toDouble(), pendapatanKemarin.toDouble())


    Column (
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Dasboard Penjualan",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row (
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            CardDashboard(icon = R.drawable.cash_coin, isPersen = true, persen = persen, title = "Pendapatan Hari ini", body = formatRupiah(pendapatanHariIni), color = MaterialTheme.colorScheme.primaryContainer)
            CardDashboard(icon = R.drawable.cash_coin, isPersen = false, title = "Pendapatan Kemarin", body = formatRupiah(pendapatanKemarin), color = MaterialTheme.colorScheme.secondaryContainer)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Grafik Jumlah Penjualan", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        PenjualanBarChart(listOf(penjualanH6.size, penjualanH5.size, penjualanH4.size, penjualanH3.size, penjualanH2.size, penjualanH1.size, penjualanHariIni.size))

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Penjualan Hari Ini",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn (
            modifier = Modifier.height(360.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(penjualanHariIni.sortedByDescending { it.tanggal }){ penjualan ->
                val pdfFile = remember { createNota(context, penjualan) }
                CardPenjualan(penjualan = penjualan, onClick = {
                    openPdf(context, pdfFile)
                })
            }
        }
    }
}

@Composable
fun CardPenjualan(penjualan: Penjualan, onClick: () -> Unit) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(0.4f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "id :" +penjualan.idTransaksi)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(id = R.drawable.person), contentDescription = "")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = penjualan.namaPembeli)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.cash_coin),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = formatRupiah(penjualan.total), style = MaterialTheme.typography.titleSmall)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { onClick.invoke() }, colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.file_earmark),
                    contentDescription = ""
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
fun persenHariIni(pendapatanHarIni : Double, pendapatanKemarin: Double) : Double {
    return if (pendapatanKemarin == 0.0) {
        0.0
    } else {
        return ((pendapatanHarIni - pendapatanKemarin) / pendapatanKemarin) * 100
    }
}