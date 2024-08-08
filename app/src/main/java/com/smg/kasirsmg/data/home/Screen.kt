package com.smg.kasirsmg.data.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smg.kasirsmg.R
import com.smg.kasirsmg.data.dashboard.DashboardScreen
import com.smg.kasirsmg.data.data_produk.DataProdukScreen
import com.smg.kasirsmg.data.data_produk.TambahBarangScreen
import com.smg.kasirsmg.data.kasir.KasirScreen
import com.smg.kasirsmg.data.laporan.LaporanScreen
import com.smg.kasirsmg.data.pesanan.PesananScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeNavGraph (
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.DashboardScreen.route
    ){
        composable(route = Screen.DashboardScreen.route) {
            DashboardScreen()
        }
        composable(route = Screen.KasirScreen.route) {
            KasirScreen()
        }
        composable(route = Screen.PesananScreen.route) {
            PesananScreen()
        }
        composable(route = Screen.DataBarangScreen.route) {
            DataProdukScreen(
                navigateToTambahBarang = {
                    navController.navigate("tambah_barang_screen")
                }
            )
        }
        composable(route = Screen.LaporanScreen.route) {
            LaporanScreen()
        }
        composable(route = "tambah_barang_screen") {
            TambahBarangScreen(onPopUp = {
                navController.navigateUp()
            })
        }
    }
}

sealed class Screen (
    val route: String,
    val label: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    data object DashboardScreen : Screen (
        route = "dashboard_screen",
        label = "Beranda",
        selectedIcon = R.drawable.house_door_fill,
        unselectedIcon = R.drawable.house_door
    )
    data object KasirScreen : Screen (
        route = "kasir_screen",
        label = "Penjualan",
        selectedIcon = R.drawable.plus_circle_fill,
        unselectedIcon = R.drawable.plus_circle
    )
    data object PesananScreen : Screen (
        route = "pesanan_screen",
        label = "Pesanan",
        selectedIcon = R.drawable.cart_fill,
        unselectedIcon = R.drawable.cart
    )
    data object DataBarangScreen: Screen(
        route = "data_barang_screen",
        label = "Data Barang",
        selectedIcon = R.drawable.collection_fill,
        unselectedIcon = R.drawable.collection
    )
    data object LaporanScreen: Screen(
        route = "pembeli_screen",
        label = "Laporan",
        selectedIcon = R.drawable.file_earmark_bar_graph_fill,
        unselectedIcon = R.drawable.file_earmark_bar_graph
    )
}