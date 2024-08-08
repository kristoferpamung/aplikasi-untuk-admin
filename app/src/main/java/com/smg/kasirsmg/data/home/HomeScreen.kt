package com.smg.kasirsmg.data.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.smg.kasirsmg.NotificationService
import com.smg.kasirsmg.R
import com.smg.kasirsmg.data.pesanan.PesananViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen (
    navigateToLogin: () -> Unit,
    pesananViewModel: PesananViewModel = viewModel(PesananViewModel::class.java)
) {

    val navController = rememberNavController()
    val pesanan by pesananViewModel.pesanan.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = pesanan) {
        val diterima = pesanan.filter {
            it.statusTransaksi == "Menunggu Konfirmasi"
        }

        if (diterima.isNotEmpty()){
            diterima.forEach {
                val notificationService = NotificationService(context = context, transaksi = it)
                notificationService.showNotification()
            }
        }
    }

    Scaffold { padding ->

        val screens = listOf(
            Screen.DashboardScreen, 
            Screen.KasirScreen,
            Screen.PesananScreen,
            Screen.DataBarangScreen,
            Screen.LaporanScreen
        )

        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NavigationRail {

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Text(
                    text = "SMG",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primaryContainer
                )

                Spacer(modifier = Modifier.weight(1f))
                screens.forEach { screen ->
                    NavigationRailItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true

                            }
                        },
                        icon = {
                            BadgedBox(badge = {
                                if(screen.route == "pesanan_screen") {
                                    val transaksiTerbaru = pesanan.filter { it.statusTransaksi == "Menunggu Konfirmasi" }
                                    if (transaksiTerbaru.isNotEmpty()){
                                        Badge {
                                            Text(text = transaksiTerbaru.size.toString())
                                        }
                                    }
                                }
                            }) {
                                Icon(painter = if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) painterResource(id = screen.selectedIcon) else painterResource(screen.unselectedIcon), contentDescription = screen.label)
                            }
                        },
                        label = {
                            Text(
                                text = screen.label,
                                fontWeight = if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                NavigationRailItem(
                    selected = false,
                    onClick = {
                        Firebase.auth.signOut()
                        navigateToLogin.invoke()
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.box_arrow_right),
                            contentDescription = "",
                            modifier = Modifier.rotate(180f),
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    label = {
                        Text(text = "Keluar")
                    }
                )
            }
            HomeNavGraph(navController = navController)
        }
    }
}