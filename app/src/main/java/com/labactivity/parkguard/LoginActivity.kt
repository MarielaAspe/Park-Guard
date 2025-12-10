package com.labactivity.parkguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var loginBtn : Button
    private lateinit var signupHere : TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        password = findViewById(R.id.editTxtPassword)
        email = findViewById(R.id.editTxtEmail)
        loginBtn = findViewById(R.id.btnLogin)
        signupHere = findViewById(R.id.btnSignUpHere)
        auth = FirebaseAuth.getInstance()

        signupHere.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        loginBtn.setOnClickListener{
            performLogin()
        }

    }
    private fun performLogin() {
        val inputEmail = email.text.toString()
        val inputPassword = password.text.toString()

        if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(this, "Please enter your email and password.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Welcome back, ${user?.email}!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}