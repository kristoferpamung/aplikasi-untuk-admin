package com.smg.kasirsmg.data.kasir

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.smg.kasirsmg.R
import com.smg.kasirsmg.model.Penjualan
import com.smg.kasirsmg.model.Produk
import com.smg.kasirsmg.ui.components.CustomTextField
import com.smg.kasirsmg.ui.components.ProductCard
import com.smg.kasirsmg.ui.components.ProductCardSmall
import com.smg.kasirsmg.ui.theme.KasirSMGTheme
import com.smg.kasirsmg.utils.formatRupiah

@Composable
fun KasirScreen (
    kasirViewModel: KasirViewModel = viewModel(modelClass = KasirViewModel::class.java)
) {

    var pencarian by remember {
        mutableStateOf("")
    }

    val context: Context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }

    val listProducts by kasirViewModel.listProduct.collectAsState()
    val keranjang by kasirViewModel.keranjang.collectAsState()

    fun getImg(nama : String) : String {
        val product = listProducts.filter {
            it.nama == nama
        }
        return product[0].id
    }

    fun getTotalHarga() : Long {
        return keranjang.sumOf { it.subTotal }
    }

    val cariProduk = listProducts.filter {
        it.nama.contains(pencarian, true)
    }

    Row (
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = pencarian,
                onValueChange = {
                    pencarian = it
                },
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.search), contentDescription = "")
                },
                placeholder = {
                    Text(text = "Cari Barang")
                },
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .shadow(
                        elevation = 2.dp,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .fillMaxWidth(0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Daftar Produk")
            Spacer(modifier = Modifier.height(8.dp))
            if (listProducts.isEmpty()) {
                Text("Belum ada Product")
                CircularProgressIndicator()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(cariProduk) {
                        ProductCard(
                            produk = it,
                            kasirViewModel = kasirViewModel
                        )
                    }
                }
            }
        }
        Column (
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(text = "Keranjang")
            Spacer(modifier = Modifier.height(8.dp))
            if (keranjang.isEmpty()) {
                Text(
                    text = "Masih kosong"
                )
            } else {
                LazyColumn (
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(keranjang) {
                        ProductCardSmall(
                            produkItem = it,
                            img = getImg(it.namaBarang),
                            kasirViewModel = kasirViewModel
                        )
                    }
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    onClick = {
                        showDialog = !showDialog
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Pesan")
                }
            }
        }
    }

    if (showDialog) {
        var bayar by remember { mutableIntStateOf(0) }
        var kembalian by remember { mutableLongStateOf(0) }


        Dialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = true
            ),
            onDismissRequest = { showDialog = !showDialog }
        ) {
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
                    keranjang.forEachIndexed { index, it ->
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
                                    item = keranjang,
                                    total = getTotalHarga(),
                                    idPembeli = Firebase.auth.currentUser?.uid ?: "",
                                    namaPembeli = "pelanggan non aplikasi",
                                    bayar = bayar.toLong(),
                                    kembalian = kembalian,
                                    tanggal = Timestamp.now()
                                )
                                kasirViewModel.kurangiStok(keranjang)
                                kasirViewModel.createTransaksiPenjualan(
                                    penjualan = penjualan,
                                    onSuccess = {
                                        if (it) {
                                            kasirViewModel.hapusSemuaItem()
                                            Toast.makeText(context, "Transaksi Penjualan Berhasil", Toast.LENGTH_LONG).show()
                                            showDialog = !showDialog
                                        } else {
                                            println("failed")
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
}