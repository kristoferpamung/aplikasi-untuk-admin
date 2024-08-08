package com.smg.kasirsmg.data.pesanan

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.smg.kasirsmg.R
import com.smg.kasirsmg.data.kasir.KasirViewModel
import com.smg.kasirsmg.model.Penjualan
import com.smg.kasirsmg.model.Pesanan
import com.smg.kasirsmg.ui.components.CustomTextField
import com.smg.kasirsmg.utils.formatRupiah
import com.smg.kasirsmg.utils.isTimestampToday

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PesananScreen (
    pesananViewModel: PesananViewModel = viewModel(modelClass = PesananViewModel::class.java),
    kasirViewModel: KasirViewModel = viewModel(modelClass = KasirViewModel::class.java)
) {

    val pesanan by pesananViewModel.pesanan.collectAsState()
    val context: Context = LocalContext.current


    val sortByDate = pesanan.sortedByDescending { it.tanggal }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Pesanan Terbaru",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val empty = sortByDate.filter { it.statusTransaksi == "Menunggu Konfirmasi" || it.statusTransaksi == "Diterima" }

            if (empty.isEmpty()) {
                items(1) {
                    Text(text = "Tidak ada pesanan")
                }
            } else {
                items(sortByDate) { pesanan ->
                    if (pesanan.statusTransaksi == "Menunggu Konfirmasi" || pesanan.statusTransaksi == "Diterima") {
                        CardPesanan(
                            transaksi = pesanan,
                            pesananViewModel = pesananViewModel,
                            kasirViewModel = kasirViewModel,
                            context = context
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CardPesanan(transaksi: Pesanan, pesananViewModel: PesananViewModel, kasirViewModel: KasirViewModel, context: Context) {

    fun getTotalHarga() : Long {
        return transaksi.item.sumOf { it.subTotal }
    }
    var showDialog by remember {mutableStateOf(false)}
    var showDialogTolak by remember { mutableStateOf(false) }

    if (showDialog){
        var bayar by remember { mutableIntStateOf(0) }
        var kembalian by remember { mutableLongStateOf(0) }
        fun getTotalHarga() : Long {
            return transaksi.item.sumOf { it.subTotal }
        }
        Dialog(onDismissRequest = { showDialog = !showDialog }) {
            Surface (
                modifier = Modifier.fillMaxWidth(),
                shape = CardDefaults.shape
            ) {
                Column (
                    modifier = Modifier
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = "Invoice", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Daftar Pesanan")
                    Row (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "No.", modifier = Modifier.weight(0.2f))
                        Text(text = "Nama Barang", modifier = Modifier.weight(1f))
                        Text(text = "Harga", modifier = Modifier.weight(0.6f))
                        Text(text = "Jumlah", modifier = Modifier.weight(0.4f))
                        Text(text = "Subtotal", modifier = Modifier.weight(0.6f))
                    }
                    transaksi.item.forEachIndexed { index, it ->
                        Row (
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${index +1}",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(0.2f)

                            )
                            Text(
                                text = it.namaBarang,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = formatRupiah(it.harga),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(0.6f)
                            )
                            Text(
                                text = "${it.jumlah} ${it.satuan}",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(0.4f)
                            )
                            Text(
                                text = formatRupiah(it.subTotal),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(0.6f)
                            )
                        }
                    }
                    Row (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.weight(2.2f),
                            text = "Total"
                        )
                        Text(
                            modifier = Modifier.weight(0.6f),
                            text = formatRupiah(getTotalHarga())
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTextField(
                        label = "Bayar",
                        value = bayar.toString(),
                        placeholder = "Di bayar",
                        keyboardType = KeyboardType.NumberPassword,
                        onValueChanged = {
                            if (it.isEmpty()) {
                                bayar = 0
                            } else {
                                bayar = it.toInt()
                                kembalian = it.toLong() - getTotalHarga()
                            }
                        }
                    )

                    Row {
                        Text(text = "Kembalian: ")
                        Text(text =  formatRupiah(kembalian))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (kembalian < 0 || bayar == 0) {
                                Toast.makeText(context, "Belum dibayar atau kurang", Toast.LENGTH_LONG).show()
                            } else {
                                val penjualan = Penjualan (
                                    idKasir = Firebase.auth.currentUser?.uid ?: "",
                                    item = transaksi.item,
                                    total = getTotalHarga(),
                                    idPembeli = transaksi.idPengguna,
                                    bayar = bayar.toLong(),
                                    kembalian = kembalian,
                                    tanggal = Timestamp.now(),
                                    namaPembeli = transaksi.namaPengguna
                                )
                                kasirViewModel.createTransaksiPenjualan(
                                    penjualan = penjualan,
                                    onSuccess = {
                                        if (it) {
                                            Toast.makeText(context, "Transaksi Penjualan Berhasil", Toast.LENGTH_LONG).show()
                                            showDialog = !showDialog
                                            pesananViewModel.changePesananToUser(
                                                idKasir = Firebase.auth.currentUser?.uid ?: "",
                                                status = "Selesai",
                                                bayar = bayar.toLong(),
                                                kembalian = kembalian,
                                                id = transaksi.idTransaksi
                                            )
                                        } else {
                                            Toast.makeText(context, "Transaksi Penjualan Gagal", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(text = "Selesai")
                    }
                }
            }
        }
    }

    if (showDialogTolak){
        Dialog(onDismissRequest = { showDialogTolak = !showDialogTolak }) {
            Surface (
                shape = CardDefaults.shape
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Tolak Pesananan", style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = buildAnnotatedString {
                        append(text = "Anda yakin ingin menghapus pesanan ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ){
                            append(text = transaksi.idTransaksi)
                        }
                    })
                    Spacer(modifier = Modifier.height(24.dp))
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = { showDialogTolak = !showDialogTolak }, colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )) {
                            Text(text = "Tidak")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                pesananViewModel.changeStatus(id = transaksi.idTransaksi, status = "Ditolak", onSuccess = {
                                    if (it) {
                                        Toast.makeText(context, "Berhasil menolak pesanan", Toast.LENGTH_LONG).show()
                                    }
                                })
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(text = "Hapus")
                        }
                    }
                }
            }
        }

    }

    Card (
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column (
            modifier = Modifier.padding(16.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        modifier = Modifier.padding(horizontal = 2.dp),
                        contentDescription = "",
                        tint = if(isTimestampToday(transaksi.tanggal)) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if(isTimestampToday(transaksi.tanggal)) "Hari ini" else "Kemarin",
                        color = if(isTimestampToday(transaksi.tanggal)) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error
                    )
                }
                Surface (
                    shape = MaterialTheme.shapes.extraSmall,
                    color = when (transaksi.statusTransaksi) {
                        "Menunggu Konfirmasi" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                ) {
                    Text(
                        text = transaksi.statusTransaksi,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            Row {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = transaksi.namaPengguna + " (${transaksi.idPengguna})")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No.",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = "Nama Barang",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Harga",
                    modifier = Modifier.weight(0.6f)
                )
                Text(
                    text = "Satuan",
                    modifier = Modifier.weight(0.4f)
                )
                Text(
                    text = "Subtotal",
                    modifier = Modifier.weight(0.6f)
                )
            }
            transaksi.item.forEachIndexed { index, it ->
                Row (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${index +1}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(0.2f)

                    )
                    Text(
                        text = it.namaBarang,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatRupiah(it.harga),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(0.6f)
                    )
                    Text(
                        text = "${it.jumlah} ${it.satuan}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(0.4f)
                    )
                    Text(
                        text = formatRupiah(it.subTotal),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Total",
                    modifier = Modifier
                        .weight(2.2f)
                        .padding(horizontal = 8.dp),
                    textAlign = TextAlign.End
                )
                Text(
                    text = formatRupiah(getTotalHarga()),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(0.6f)
                        .border(width = 1.dp, color = MaterialTheme.colorScheme.outline)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    enabled = transaksi.statusTransaksi == "Menunggu Konfirmasi",
                    onClick = {
                        pesananViewModel.changeStatus(transaksi.idTransaksi, status = "Diterima", onSuccess = {
                            kasirViewModel.kurangiStok(transaksi.item)
                            Toast.makeText(context, "Pesanan diterima", Toast.LENGTH_LONG).show()
                        })
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Text(text = "Terima Pesanan")
                }
                Button(
                    onClick = {
                        showDialog = !showDialog
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(text = "Pembayaran")
                }
                Button(
                    onClick = {
                        showDialogTolak = !showDialogTolak
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = "Tolak Pesanan")
                }
            }
        }
    }
}