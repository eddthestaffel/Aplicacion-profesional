package com.example.profesional

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ReservaRepository {

    private val db = FirebaseFirestore.getInstance()

    fun escucharReservas(
        estado: String,
        onResultado: (List<Reserva>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {

        var consulta: Query = db.collection("reservas")

        if (estado != "Todos") {
            val estadoFirestore = when (estado) {
                "Solicitado" -> "SOLICITADA"
                "Aceptado" -> "ACEPTADA"
                "Rechazado" -> "RECHAZADA"
                "Reprogramado" -> "REPROGRAMADA"
                else -> estado
            }

            consulta = consulta.whereEqualTo(
                "estado",
                estadoFirestore
            )
        }

        return consulta.addSnapshotListener { resultado, error ->

            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            if (resultado == null) {
                return@addSnapshotListener
            }

            val reservas = mutableListOf<Reserva>()
            for (documento in resultado) {

                val fechaOriginal = documento.getString("fecha")
                val fechaNueva = documento.getString("fechaNueva")
                val horaOriginal = documento.getString("hora")
                val horaNueva = documento.getString("horaNueva")
                val fechaMostrar = fechaNueva ?: fechaOriginal ?: "Sin fecha"
                val horaMostrar = horaNueva ?: horaOriginal ?: "Sin hora"
                val reserva = Reserva(
                    id = documento.id,
                    nombreCliente = documento.getString("nombreCliente") ?: "Sin nombre",
                    servicio = documento.getString("servicio") ?: "Sin servicio",
                    fecha = fechaMostrar,
                    hora = horaMostrar,
                    estado = documento.getString("estado") ?: "Sin estado",
                    observacion = documento.getString("observacion") ?: "Sin observación"
                )
                reservas.add(reserva)
            }
            onResultado(reservas)
        }
    }

    fun obtenerReserva(idReserva: String) =
        db.collection("reservas")
            .document(idReserva)
            .get()

    fun actualizarEstado(
        idReserva: String,
        nuevoEstado: String
    ) = db.collection("reservas")
        .document(idReserva)
        .update(
            "estado",
            when (nuevoEstado) {
                "Aceptado" -> "ACEPTADA"
                "Rechazado" -> "RECHAZADA"
                "Reprogramado" -> "REPROGRAMADA"
                else -> nuevoEstado
            }
        )

    fun rechazarReserva(
        idReserva: String,
        observacion: String
    ) =
        db.collection("reservas")
            .document(idReserva)
            .update(
                mapOf(
                    "estado" to "RECHAZADA",
                    "observacion" to observacion
                )
            )

    fun reprogramarReserva(
        idReserva: String,
        fecha: String,
        hora: String
    ) =
        db.collection("reservas")
            .document(idReserva)
            .update(
                mapOf(
                    "fechaNueva" to fecha,
                    "horaNueva" to hora,
                    "estado" to "REPROGRAMADA"
                )
            )
}