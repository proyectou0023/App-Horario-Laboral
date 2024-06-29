package com.company.apphorariolaboral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistroActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        val btnRegistrar: Button = findViewById(R.id.btnRegistrar)
        val btnVolver: Button = findViewById(R.id.btnVolver)

        btnRegistrar.setOnClickListener {
            val nombre = findViewById<EditText>(R.id.etNombre).text.toString()
            val cedula = findViewById<EditText>(R.id.etCedula).text.toString()
            val correo = findViewById<EditText>(R.id.etCorreo).text.toString()
            val contrasena = findViewById<EditText>(R.id.etContrasena).text.toString()

            if (nombre.isNotEmpty() && cedula.isNotEmpty() && correo.isNotEmpty() && contrasena.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val userId = user?.uid
                            val userInfo = Usuario(nombre, cedula, correo)

                            userId?.let {
                                database.child("usuarios").child(it).setValue(userInfo)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    data class Usuario(val nombre: String, val cedula: String, val correo: String)
}