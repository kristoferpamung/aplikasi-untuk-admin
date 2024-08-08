package com.smg.kasirsmg.data.data_produk

import android.content.Context
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.smg.kasirsmg.R
import com.smg.kasirsmg.model.Produk
import com.smg.kasirsmg.ui.components.CustomTextField
import com.smg.kasirsmg.utils.formatRupiah

@Composable
fun DataProdukScreen(
    dataProdukViewModel: DataProdukViewModel = viewModel(modelClass = DataProdukViewModel::class.java),
    navigateToTambahBarang : () -> Unit
) {
    val dataProduk by dataProdukViewModel.products.collectAsState()

    var tambahStok by remember { mutableStateOf(false) }
    var cari by remember {
        mutableStateOf("")
    }
    var namaProductSelected by remember { mutableStateOf("") }
    var idProductSelected by remember { mutableStateOf("") }
    var satuanProductSelected by remember { mutableStateOf("") }

    var errorStok by remember {
        mutableStateOf(false)
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Data Barang",
                style = MaterialTheme.typography.titleLarge
            )
            OutlinedTextField(
                value = cari,
                onValueChange = {
                    cari = it
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
                    .weight(1f)
                    .shadow(
                        elevation = 2.dp,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .fillMaxWidth(0.5f)
            )
            Button(
                onClick = {
                    navigateToTambahBarang.invoke()
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
                Text(text = "Tambah Barang")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (dataProduk.isEmpty()) {
            Text(text = "Belum ada produk")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (cari.isEmpty()){
                    items(dataProduk.sortedBy { it.stok }) { produk ->
                        ProductItem(produk, onTambahStok = { nama, id, satuan ->
                            tambahStok = !tambahStok
                            namaProductSelected = nama
                            idProductSelected = id
                            satuanProductSelected = satuan

                        })
                    }
                } else {
                    items(dataProduk.sortedBy { it.stok }.filter { it.nama.contains(cari, true) }) { produk ->
                        ProductItem(produk, onTambahStok = { nama, id, satuan ->
                            tambahStok = !tambahStok
                            namaProductSelected = nama
                            idProductSelected = id
                            satuanProductSelected = satuan
                        })
                    }
                }
            }
        }
    }
    if (tambahStok){
        var stok by remember {
            mutableDoubleStateOf(0.0)
        }
        var isLoading by remember {
            mutableStateOf(false)
        }

        Dialog(onDismissRequest = { tambahStok = !tambahStok }) {
            Surface (
                shape = CardDefaults.shape
            ) {
                Column (
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Tambah Stok")
                    Text(text = namaProductSelected, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        TextField(
                            isError = errorStok,
                            value = stok.toString(),
                            onValueChange = { stok = it.toDouble() },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = satuanProductSelected)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        if (stok <= 0.0) {
                            errorStok = true
                        } else {
                            isLoading = true
                            errorStok = false
                            tambahStokFun(stok,idProductSelected, onSuccess = { isSuccess ->
                                if(isSuccess){
                                    tambahStok = !tambahStok
                                    stok =0.0
                                    isLoading = false
                                }
                                else {
                                    tambahStok = true
                                }
                            }
                            )
                        }
                    }) {
                        Text(text = "Tambah Stok")
                        if (isLoading){
                            Spacer(modifier = Modifier.width(8.dp))
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLowest)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(produk: Produk, onTambahStok: (String,String, String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.cardColors(
            containerColor = if (produk.stok < 5) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = "https://firebasestorage.googleapis.com/v0/b/toko-smg-da935.appspot.com/o/products%2F${produk.id}.jpg?alt=media&token=bebdfa7e-63ef-4e26-9010-4c5414ce403f",
                contentDescription = "",
                error = painterResource(id = R.drawable.no_product_img),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .clip(shape = CardDefaults.shape)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = produk.nama, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Stok: ${produk.stok}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Satuan: ${produk.satuan}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Harga:")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = produk.hargaProduk.joinToString { "${it.satuan} - ${formatRupiah(it.harga)}" })


            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = { onTambahStok.invoke(produk.nama, produk.id, produk.satuan) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Tambah Stok")
            }
        }
    }
}

fun tambahStokFun(stok:Double, idProduct: String, onSuccess: (Boolean) -> Unit){
    val db = FirebaseFirestore.getInstance()
    db.collection("products")
        .document(idProduct)
        .get()
        .addOnSuccessListener {
            if (it.exists()){
                val stokSaatIni = it.getDouble("stok") ?: 0.0
                val stokTerbaru = stokSaatIni + stok
                db.collection("products")
                    .document(idProduct)
                    .update("stok", (stokTerbaru))
                    .addOnSuccessListener {
                        onSuccess.invoke(true)
                    }
                    .addOnFailureListener {
                        onSuccess.invoke(false)
                    }
            }

        }
}