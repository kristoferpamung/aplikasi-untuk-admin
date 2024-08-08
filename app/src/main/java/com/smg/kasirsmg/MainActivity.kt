package com.smg.kasirsmg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.smg.kasirsmg.data.home.HomeScreen
import com.smg.kasirsmg.data.login.LoginScreen
import com.smg.kasirsmg.ui.theme.KasirSMGTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            KasirSMGTheme {
                MainNavGraph()
            }
        }
    }
}

@Composable
fun MainNavGraph(
    controller: NavHostController = rememberNavController()
) {
    NavHost(
        navController = controller,
        startDestination = if (Firebase.auth.currentUser != null) "main_screen" else "login_screen"
    ) {
        composable(route = "login_screen") {
            LoginScreen (
                navigateToHome = {
                    controller.popBackStack()
                    controller.navigate(route= "main_screen")
                }
            )
        }
        composable(route = "main_screen"){
            HomeScreen(navigateToLogin = {
                controller.popBackStack()
                controller.navigate("login_screen")
            })
        }
    }
}