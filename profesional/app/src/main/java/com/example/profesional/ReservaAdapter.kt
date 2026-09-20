package com.example.profesional
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.profesional.R

data class Reserva(
    val id: String,
    val nombreCliente: String,
    val servicio: String,
    val fecha: String,
    val hora: String,
    val estado: String,
    val observacion: String
)

class ReservaAdapter(
    private val reservas: List<Reserva>,
    private val onClick: (Reserva) -> Unit
) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtCliente: TextView = itemView.findViewById(R.id.txtCliente)
        val txtServicio: TextView = itemView.findViewById(R.id.txtServicio)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtHora: TextView = itemView.findViewById(R.id.txtHora)
        val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
        val txtObservacion: TextView = itemView.findViewById(R.id.txtObservacion)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: ReservaViewHolder, position: Int) {

        val reserva = reservas[position]

        holder.txtCliente.text = "Cliente: ${reserva.nombreCliente}"
        holder.txtServicio.text = "Servicio: ${reserva.servicio}"
        holder.txtFecha.text = "Fecha: ${reserva.fecha}"
        holder.txtHora.text = "Hora: ${reserva.hora}"
        holder.txtEstado.text = "Estado: ${reserva.estado}"
        holder.txtObservacion.text = "Observación: ${reserva.observacion}"
        holder.itemView.setOnClickListener {
            onClick(reserva)
        }
    }

    override fun getItemCount(): Int {
        return reservas.size
    }
}