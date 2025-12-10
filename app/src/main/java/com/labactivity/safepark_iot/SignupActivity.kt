package com.labactivity.safepark_iot

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
import com.labactivity.parkguard.R

class SignupActivity : AppCompatActivity() {
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var signupBtn : Button
    private lateinit var loginHere : TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        password = findViewById(R.id.editTxtPassword)
        email = findViewById(R.id.editTxtEmail)
        signupBtn = findViewById(R.id.btnSignUp)
        loginHere = findViewById(R.id.btnLoginHere)
        auth = FirebaseAuth.getInstance()

        loginHere.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        signupBtn.setOnClickListener{
            performSignUp()
        }

    }
    private fun performSignUp() {
        val inputEmail = email.text.toString()
        val inputPassword = password.text.toString()

        if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}