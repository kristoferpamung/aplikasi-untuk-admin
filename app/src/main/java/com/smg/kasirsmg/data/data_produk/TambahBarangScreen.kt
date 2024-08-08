package com.smg.kasirsmg.data.data_produk

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.smg.kasirsmg.R
import com.smg.kasirsmg.model.HargaProduk
import com.smg.kasirsmg.model.Produk
import com.smg.kasirsmg.ui.components.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahBarangScreen(
    onPopUp: () -> Unit
) {

    val listSatuan = listOf(
        "Kg",
        "Karton",
        "Ball",
        "Pcs",
        "Krat",
        "Botol",
        "Dusin",
        "Renteng"
    )

    var imgUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imgUri = it
    }

    var expanded by remember { mutableStateOf(false) }

    var namaBarang by remember { mutableStateOf("") }
    var satuan by remember { mutableStateOf("") }

    var tambahDialog by remember { mutableStateOf(false) }
    var errorTextfield by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val listHarga = remember {
        mutableListOf<HargaProduk>()
    }

    val context: Context = LocalContext.current

    Scaffold  {
        Column (
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {

            val painter: Painter = if (imgUri != null) {
                rememberAsyncImagePainter(model = imgUri)
            } else {
                painterResource(id = R.drawable.no_product_img)
            }
            
            Text(text = "Tambah Barang", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))

            Row (modifier = Modifier.padding(horizontal = 16.dp)) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                        .border(width = 2.dp, color = Color.Black, shape = CardDefaults.shape)
                        .clip(shape = CardDefaults.shape)
                        .clickable {
                            launcher.launch("image/*")
                        }
                )

                Column (modifier = Modifier.padding(horizontal = 16.dp)) {
                    CustomTextField(
                        label = "Nama Barang",
                        value = namaBarang,
                        placeholder = "Masukan Nama Barang",
                        keyboardType = KeyboardType.Text,
                        onValueChanged = { newValue ->
                            namaBarang = newValue
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                isError = errorTextfield,
                                value = satuan,
                                onValueChange = {},
                                placeholder = {
                                    Text(text = "Pilih Satuan")
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                colors = OutlinedTextFieldDefaults.colors(),
                                modifier = Modifier
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                listSatuan.forEach { option: String ->
                                    DropdownMenuItem(
                                        text = { Text(text = option) },
                                        onClick = {
                                            expanded = false
                                            satuan = option
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            onClick = {
                                tambahDialog = !tambahDialog
                            },
                            shape = MaterialTheme.shapes.extraSmall,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(text = "+ Tambah Harga")
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Daftar Harga")
                    if (listHarga.isEmpty()) {
                        Text(text = "Belum ada harga")
                    }
                    listHarga.forEach {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column (
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row (
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Nama Satuan")
                                    Text(text = "Harga")
                                    Text(text = "Bobot Satuan")
                                }
                                Row (
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = it.satuan, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(text = it.harga.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(text = it.amount.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        enabled = listHarga.isNotEmpty(),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        onClick = {
                            if(listHarga.isNotEmpty()) {
                                if (namaBarang.isNotEmpty() && satuan.isNotEmpty()){
                                    errorTextfield = false
                                    isLoading = true

                                    val barang = Produk(
                                        nama = namaBarang,
                                        satuan = satuan,
                                        stok = 0.0,
                                        hargaProduk = listHarga
                                    )
                                    saveBarang(barang, onSuccess = { id ->
                                        uploadImageToFirebase(uri = imgUri, name = "$id.jpg", onSuccess = {
                                            isLoading = false
                                            onPopUp.invoke()
                                        })
                                    })

                                } else {
                                    errorTextfield = true
                                }
                            } else {
                                Toast.makeText(context, "Belum ada harga", Toast.LENGTH_LONG).show()
                            }
                        }
                    ) {
                        Text(text = "Simpan Data")

                        if (isLoading){
                            Spacer(modifier = Modifier.width(8.dp))
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLowest)
                        }
                    }
                }
            }
        }
    }

    if (tambahDialog){
        Dialog(onDismissRequest = { tambahDialog = !tambahDialog }) {
            var satuanHarga by remember { mutableStateOf("") }
            var harga by remember { mutableLongStateOf(0) }
            var bobot by remember { mutableDoubleStateOf(0.0) }

            Surface (
                shape = CardDefaults.shape
            ) {
                Column (modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Tambah Harga")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        label = { Text(text = "Nama Satuan")},
                        placeholder = {
                        Text(text = "Masukan Nama Satuan")
                    }, value = satuanHarga, onValueChange = {satuanHarga = it}, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        label = { Text(text = "Harga Satuan")},
                        placeholder = {
                        Text(text = "Masukan Harga")
                    }, value = harga.toString(), onValueChange = {harga = it.toLong()}, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        label = { Text(text = "Bobot Satuan")},
                        placeholder = {
                        Text(text = "Masukan Bobot Satuan")
                    }, value = bobot.toString(), onValueChange = {bobot = it.toDouble()}, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        listHarga.add(HargaProduk(amount = bobot, satuan = satuanHarga, harga = harga))
                        tambahDialog = !tambahDialog
                    }) {
                        Text(text = "Tambahkan")
                    }
                }
            }
        }
    }
}

fun uploadImageToFirebase (uri: Uri?, name: String, onSuccess: () -> Unit) {
    val storage = FirebaseStorage.getInstance()
    val storageReference = storage.reference
    val imageReference = storageReference.child("products/$name")

    val uploadTask = uri.let {
        imageReference.putFile(it!!)
    }

    uploadTask.addOnSuccessListener {
        onSuccess.invoke()
    }
        .addOnFailureListener {
            println("Not uploaded")
        }
}

fun saveBarang (produk: Produk, onSuccess: (String)->Unit) {
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("products")

    collection.add(produk)
        .addOnSuccessListener {
            val id = it.id
            onSuccess.invoke(id)
        }
        .addOnFailureListener {
            println("Gagal")
        }
}