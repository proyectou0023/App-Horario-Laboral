package com.company.apphorariolaboral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReporteActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var reporteAdapter: ReporteAdapter
    private val reportes = mutableListOf<Reporte>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.rvReportes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        reporteAdapter = ReporteAdapter(reportes)
        recyclerView.adapter = reporteAdapter

        val nombreUsuarioTextView = findViewById<TextView>(R.id.tvNombreUsuario)
        val currentUser = auth.currentUser
        val btnAtras: Button = findViewById(R.id.btnAtras)

        currentUser?.let { user ->
            val userId = user.uid

            // Obtener el nombre del usuario
            database.child("usuarios").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val nombre = dataSnapshot.child("nombre").getValue(String::class.java)
                    nombreUsuarioTextView.text = "Usuario: $nombre"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@ReporteActivity, "Error al obtener el nombre del usuario", Toast.LENGTH_SHORT).show()
                }
            })

            // Obtener reportes del usuario
            database.child("reportes").child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    reportes.clear()
                    for (reporteSnapshot in dataSnapshot.children) {
                        val ingreso = reporteSnapshot.child("INGRESO").getValue(String::class.java) ?: ""
                        val salida = reporteSnapshot.child("SALIDA").getValue(String::class.java) ?: ""
                        val horasTrabajadas = reporteSnapshot.child("HORAS_TRABAJADAS").getValue(String::class.java) ?: ""

                        val reporte = Reporte(ingreso, salida, horasTrabajadas)
                        reportes.add(reporte)
                    }
                    reporteAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@ReporteActivity, "Error al obtener los reportes", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnAtras.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}