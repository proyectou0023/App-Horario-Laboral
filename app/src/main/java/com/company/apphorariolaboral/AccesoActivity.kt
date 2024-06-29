package com.company.apphorariolaboral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AccesoActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceso)

        auth = FirebaseAuth.getInstance()

        val btnAcceder: Button = findViewById(R.id.btnAcceder)
        val btnVolver2: Button = findViewById(R.id.btnVolver2)

        btnAcceder.setOnClickListener {
            val correo = findViewById<EditText>(R.id.etCorreo).text.toString()
            val contrasena = findViewById<EditText>(R.id.etContrasena).text.toString()

            if (correo.isNotEmpty() && contrasena.isNotEmpty()) {
                auth.signInWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}