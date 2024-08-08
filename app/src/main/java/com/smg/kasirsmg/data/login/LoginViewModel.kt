package com.smg.kasirsmg.data.login


import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel() : ViewModel() {

    fun hasUser () : Boolean {
        return Firebase.auth.currentUser != null
    }

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoginError = MutableStateFlow<String?>(null)
    val isLoginError: StateFlow<String?> = _isLoginError

    fun onEmailChanged (newEmail : String) {
        _email.value = newEmail
    }

    fun onPasswordChanged (newPassword : String) {
        _password.value = newPassword
    }

    fun login(
        onLoginSuccess: () -> Unit
    ) {
        isPasswordValid(_password.value)
        isEmailValid(_email.value)
        if (_emailError.value == null && _passwordError.value == null) {
            viewModelScope.launch {
                _isLoading.value = true
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.value, password.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user != null) {
                                checkIsAdmin(user.uid, onLoginSuccess)
                            }
                        } else {
                            val err = task.exception?.message?.contains("The supplied")
                            _isLoading.value = false
                            _isLoginError.value = if (err == true) "Email atau password salah!" else task.exception?.message
                        }
                    }
            }
        }
    }

    private fun isEmailValid (email: String) {
        val isEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (email.isBlank()) {
            _emailError.value = "Email tidak boleh kosong!"
        } else if (!isEmail) {
            _emailError.value = "Format email tidak valid!"
        } else {
            _emailError.value = null
        }
    }

    private fun isPasswordValid (password: String) {
        if (password.isBlank()) {
            _passwordError.value = "Password tidak boleh kosong!"
        } else {
            _passwordError.value = null
        }
    }

    private fun checkIsAdmin(uid: String, onLoginSuccess: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(uid)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists() && document != null) {
                    val isAdmin = document.getBoolean("admin")
                    if (isAdmin != null) {
                        _isLoading.value = false
                        onLoginSuccess.invoke()
                    } else {
                        _isLoading.value = false
                        FirebaseAuth.getInstance().signOut()
                        _isLoginError.value = "Anda bukan admin, akses ditolak"
                    }
                }
                else {
                    _isLoading.value = false
                    FirebaseAuth.getInstance().signOut()
                    _isLoginError.value = "User tidak ada"
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                FirebaseAuth.getInstance().signOut()
                _isLoginError.value = e.localizedMessage
            }
    }
}