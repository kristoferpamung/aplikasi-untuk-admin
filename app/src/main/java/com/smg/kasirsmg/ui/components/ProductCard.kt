package com.smg.kasirsmg.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.smg.kasirsmg.R
import com.smg.kasirsmg.data.kasir.KasirViewModel
import com.smg.kasirsmg.model.Produk
import com.smg.kasirsmg.model.ProdukItem
import com.smg.kasirsmg.utils.formatRupiah

@Composable
fun ProductCard(
    produk: Produk,
    kasirViewModel: KasirViewModel
) {

    val hargaProduk = produk.hargaProduk
    val sortByHarga = hargaProduk.sortedBy { it.harga }

    var indexHarga by remember { mutableIntStateOf(0) }
    var jumlah by remember { mutableIntStateOf(1) }

    val imgUrl = "https://firebasestorage.googleapis.com/v0/b/toko-smg-da935.appspot.com/o/products%2F${produk.id}.jpg?alt=media&token=fef749a9-24a0-4529-bf97-4e20552f961a"

    Card (
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column (
            modifier = Modifier.padding(12.dp)
        ) {
            Box (
                contentAlignment = Alignment.BottomEnd
            ){
                AsyncImage(
                    model = imgUrl,
                    error = painterResource(id = R.drawable.no_product_img),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .clip(
                            shape = CardDefaults.shape
                        )
                )
                Surface (
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.8f)
                ) {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.box_fill),
                            contentDescription = "",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primaryContainer
                        )
                        Text(
                            text = produk.stok.toString() + " " + produk.satuan,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = produk.nama,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatRupiah(sortByHarga[indexHarga].harga),
                style = MaterialTheme.typography.bodyMedium
            )
            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(sortByHarga) { index, it ->
                    Surface (
                        onClick = {
                            indexHarga = index
                        },
                        shape = MaterialTheme.shapes.extraLarge,
                        color = if (index == indexHarga) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                        border = BorderStroke(width = 1.dp, color = if(index == indexHarga) Color.Transparent else MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = it.satuan,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = if (index == indexHarga) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Surface (
                    shape = CircleShape,
                    color = if (jumlah <= 1) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        if (jumlah <= 1) {
                            jumlah = 1
                        } else {
                            jumlah -= 1
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.dash),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = jumlah.toString())
                Spacer(modifier = Modifier.width(8.dp))
                Surface (
                    shape = CircleShape,
                    color = if (jumlah <= produk.stok) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                    onClick = {
                        if (jumlah <= produk.stok) {
                            jumlah += 1
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                enabled = produk.stok > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = {
                    val item = ProdukItem(
                        namaBarang = produk.nama,
                        jumlah = jumlah.toLong(),
                        harga = sortByHarga[indexHarga].harga,
                        subTotal = sortByHarga[indexHarga].harga * jumlah,
                        satuan = sortByHarga[indexHarga].satuan,
                        bobot = sortByHarga[indexHarga].amount
                    )
                    kasirViewModel.updateKeranjang(item){
                        println(it)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Tambah")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(painter = painterResource(id = R.drawable.cart_plus_fill), contentDescription = "")
            }
        }
    }
}