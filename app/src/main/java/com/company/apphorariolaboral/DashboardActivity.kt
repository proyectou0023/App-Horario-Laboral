package com.company.apphorariolaboral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class DashboardActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var currentReportId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        val btnInicioJornada: Button = findViewById(R.id.btnInicioJornada)
        val btnFinJornada: Button = findViewById(R.id.btnFinJornada)
        val btnGenerarReporte: Button = findViewById(R.id.btnGenerarReporte)
        val btnCerrar: Button = findViewById(R.id.btnCerrar)

        btnInicioJornada.setOnClickListener {
            registrarIngreso()
        }

        btnFinJornada.setOnClickListener {
            registrarSalida()
        }

        btnGenerarReporte.setOnClickListener {
            val intent = Intent(this, ReporteActivity::class.java)
            startActivity(intent)
        }

        btnCerrar.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registrarIngreso() {
        val currentUser = auth.currentUser
        val currentDate = getCurrentDateTime()

        currentUser?.let {
            val userId = it.uid

            currentReportId = database.child("reportes").child(userId).push().key

            currentReportId?.let { reportId ->
                database.child("reportes").child(userId).child(reportId).child("INGRESO")
                    .setValue(currentDate)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Inicio de jornada registrado", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this,"Error al registrar inicio de jornada",Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun registrarSalida() {
        val currentUser = auth.currentUser
        val currentDate = getCurrentDateTime()

        currentUser?.let {
            val userId = it.uid

            currentReportId?.let { reportId ->
                database.child("reportes").child(userId).child(reportId).child("SALIDA").setValue(currentDate)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Fin de jornada registrado", Toast.LENGTH_SHORT)
                                .show()
                            calcularHorasTrabajadas(userId, reportId)
                        } else {
                            Toast.makeText(this,"Error al registrar fin de jornada",Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }
    }

    private fun calcularHorasTrabajadas(userId: String, reportId: String) {
        database.child("reportes").child(userId).child(reportId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ingreso = dataSnapshot.child("INGRESO").getValue(String::class.java)
                val salida = dataSnapshot.child("SALIDA").getValue(String::class.java)

                if (ingreso != null && salida != null) {
                    val horasTrabajadas = calcularDuracion(ingreso, salida)
                    database.child("reportes").child(userId).child(reportId).child("HORAS_TRABAJADAS").setValue(horasTrabajadas)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@DashboardActivity,
                    "Error al calcular horas trabajadas",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun calcularDuracion(ingreso: String, salida: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("es", "CO"))
        dateFormat.timeZone = TimeZone.getTimeZone("America/Bogota")
        return try {
            val fechaIngreso = dateFormat.parse(ingreso)
            val fechaSalida = dateFormat.parse(salida)
            if (fechaIngreso != null && fechaSalida != null) {
                val diffInMillis = fechaSalida.time - fechaIngreso.time
                val horas = TimeUnit.MILLISECONDS.toHours(diffInMillis)
                val minutos = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60
                val segundos = TimeUnit.MILLISECONDS.toSeconds(diffInMillis) % 60
                String.format("%d horas, %d minutos y %d segundos", horas, minutos, segundos)
            } else {
                "Datos de tiempo no válidos"
            }
        } catch (e: Exception) {
            "Error al calcular horas trabajadas"
        }
    }

    private fun getCurrentDateTime(): String {
        val timeZone = TimeZone.getTimeZone("America/Bogota")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("es", "CO"))
        dateFormat.timeZone = timeZone
        return dateFormat.format(Date())

    }
}