package com.smg.kasirsmg.data.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.size.Size
import com.smg.kasirsmg.R


@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(modelClass = LoginViewModel::class.java),
    navigateToHome: () -> Unit
) {

    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val loginError by loginViewModel.isLoginError.collectAsState()
    val isLoading by loginViewModel.isLoading.collectAsState()
    val emailError by loginViewModel.emailError.collectAsState()
    val passwordError by loginViewModel.passwordError.collectAsState()


    val context = LocalContext.current
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(key1 = loginViewModel.hasUser()) {
        if (loginViewModel.hasUser()) {
            navigateToHome.invoke()
        }
    }

    Scaffold { padding ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.welcome_img),
                contentDescription = "backgound image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column (
                    modifier = Modifier
                        .padding(horizontal = 80.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            shape = CardDefaults.shape
                        )
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column (
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Silakan Masuk",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            loginError?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            OutlinedTextField(
                                value = email,
                                isError = emailError != null,
                                onValueChange = {
                                    loginViewModel.onEmailChanged(it)
                                },
                                label = {
                                    Text(text = "Email")
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Email
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            emailError?.let { Text(text = "* $it", color = MaterialTheme.colorScheme.error, fontSize = 10.sp) }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField (
                                value = password,
                                isError = passwordError != null,
                                onValueChange = {
                                    loginViewModel.onPasswordChanged(it)
                                },
                                label = {
                                    Text(text = "Password")
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Password
                                ),
                                trailingIcon = {
                                    IconButton(
                                        onClick = { isPasswordVisible = !isPasswordVisible }
                                    ) {
                                        if (isPasswordVisible) {
                                            Icon(painter = painterResource(id = R.drawable.eye_slash_fill), contentDescription = "")
                                        } else {
                                            Icon(painter = painterResource(id = R.drawable.eye_fill), contentDescription = "")
                                        }
                                    }
                                },
                                visualTransformation = if (!isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                                modifier = Modifier.fillMaxWidth()
                            )
                            passwordError?.let { Text(text = "* $it", color = MaterialTheme.colorScheme.error, fontSize = 10.sp) }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    loginViewModel.login {
                                        Toast.makeText(context, "Login berhasil", Toast.LENGTH_LONG).show()
                                        navigateToHome.invoke()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Masuk")
                                if (isLoading) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        Column (
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(painter = painterResource(id = R.drawable.shop), contentDescription = "", tint = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(56.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "CV. Sukses Makmur Gemilang",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}