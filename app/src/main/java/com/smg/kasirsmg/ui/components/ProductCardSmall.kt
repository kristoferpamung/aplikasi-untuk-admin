package com.smg.kasirsmg.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.smg.kasirsmg.R
import com.smg.kasirsmg.data.kasir.KasirViewModel
import com.smg.kasirsmg.model.ProdukItem
import com.smg.kasirsmg.utils.formatRupiah

@Composable
fun ProductCardSmall(
    produkItem: ProdukItem,
    kasirViewModel: KasirViewModel,
    img: String
) {

    val imgUrl = "https://firebasestorage.googleapis.com/v0/b/toko-smg-da935.appspot.com/o/products%2F${img}.jpg?alt=media&token=fef749a9-24a0-4529-bf97-4e20552f961a"
    
    
    Card (
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Row (
            modifier = Modifier.padding(8.dp)
        ){
            AsyncImage(
                model = imgUrl,
                error = painterResource(id = R.drawable.no_product_img),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(
                        shape = CardDefaults.shape
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column (
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = produkItem.namaBarang,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatRupiah(produkItem.subTotal),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${produkItem.jumlah} ${produkItem.satuan}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.height(72.dp),
                contentAlignment = Alignment.Center
            ){
                Surface (
                    color = MaterialTheme.colorScheme.error,
                    shape = CircleShape,
                    onClick = {
                        kasirViewModel.hapusBarangKeranjang(
                            produkItem = produkItem,
                            onSuccess = {
                                println(it)
                            }
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.trash3_fill),
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}