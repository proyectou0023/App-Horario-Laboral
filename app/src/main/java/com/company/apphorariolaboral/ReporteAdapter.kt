package com.company.apphorariolaboral

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Reporte(
    val ingreso: String = "",
    val salida: String = "",
    val horasTrabajadas: String = ""
)

class ReporteAdapter(private val reportes: List<Reporte>) :
    RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder>() {

    class ReporteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIngreso: TextView = itemView.findViewById(R.id.tvIngreso)
        val tvSalida: TextView = itemView.findViewById(R.id.tvSalida)
        val tvHorasTrabajadas: TextView = itemView.findViewById(R.id.tvHorasTrabajadas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReporteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reporte, parent, false)
        return ReporteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReporteViewHolder, position: Int) {
        val reporte = reportes[position]
        holder.tvIngreso.text = reporte.ingreso
        holder.tvSalida.text = reporte.salida
        holder.tvHorasTrabajadas.text = reporte.horasTrabajadas
    }

    override fun getItemCount(): Int = reportes.size
}