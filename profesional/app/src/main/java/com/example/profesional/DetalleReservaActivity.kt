package com.example.profesional

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class DetalleReservaActivity : AppCompatActivity() {

    private val repository = ReservaRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_reserva)

        val idReserva = intent.getStringExtra("idReserva")

        if (idReserva != null) {

            cargarReserva(idReserva)

            val button = findViewById<Button>(R.id.button)
            val button2 = findViewById<Button>(R.id.button2)
            val button3 = findViewById<Button>(R.id.button3)

            button.setOnClickListener {
                if (!hayInternet()) {
                    Toast.makeText(
                        this, "Sin conexión. Intente nuevamente", Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                actualizarEstado(idReserva, "Aceptado")
            }

            button2.setOnClickListener {
                mostrarDialogoRechazo(idReserva)
            }

            button3.setOnClickListener {
                seleccionarFecha(idReserva)
            }
        }
    }

    private fun hayInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }

    private fun cargarReserva(idReserva: String) {
        repository.obtenerReserva(idReserva)
            .addOnSuccessListener { documento ->
                if (documento.exists()) {
                    val nombreCliente = documento.getString("nombreCliente") ?: "Sin nombre"
                    val servicio = documento.getString("servicio") ?: "Sin servicio"
                    val profesional = documento.getString("profesional") ?: "Sin profesional"
                    val fecha =
                        documento.getString("fechaNueva")
                            ?: documento.getString("fecha")
                            ?: "Sin fecha"
                    val hora =
                        documento.getString("horaNueva")
                            ?: documento.getString("hora")
                            ?: "Sin hora"
                    val estado = documento.getString("estado") ?: "Sin estado"
                    val observacion = documento.getString("observacion") ?: "Sin observación"

                    val button = findViewById<Button>(R.id.button)
                    val button2 = findViewById<Button>(R.id.button2)
                    val button3 = findViewById<Button>(R.id.button3)

                    if (estado == "Solicitado" || estado == "SOLICITADA") {
                        button.visibility = Button.VISIBLE
                        button2.visibility = Button.VISIBLE
                        button3.visibility = Button.VISIBLE
                    } else {
                        button.visibility = Button.GONE
                        button2.visibility = Button.GONE
                        button3.visibility = Button.GONE
                    }

                    findViewById<TextView>(R.id.txtCliente).text = "Cliente: $nombreCliente"
                    findViewById<TextView>(R.id.txtServicio).text = "Servicio: $servicio"
                    findViewById<TextView>(R.id.txtProfesional).text = "Profesional: $profesional"
                    findViewById<TextView>(R.id.txtFecha).text = "Fecha: $fecha"
                    findViewById<TextView>(R.id.txtHora).text = "Hora: $hora"
                    findViewById<TextView>(R.id.txtEstado).text = "Estado: $estado"
                    findViewById<TextView>(R.id.txtObservacion).text = "Observación: $observacion"

                } else {

                    Toast.makeText(
                        this,
                        "Reserva no encontrada",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }
            }
            .addOnFailureListener { error ->

                if (error.message?.contains("PERMISSION_DENIED") == true) {

                    Log.e(
                        "Firestore",
                        "Permiso denegado al consultar la reserva",
                        error
                    )
                    Toast.makeText(
                        this, "No tienes permiso para consultar esta reserva", Toast.LENGTH_LONG
                    ).show()

                } else {
                    Toast.makeText(
                        this, "Error al cargar la reserva: ${error.message}", Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun seleccionarFecha(idReserva: String) {

        val calendario = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val fecha = String.format(
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    dayOfMonth
                )

                seleccionarHora(idReserva, fecha)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.datePicker.minDate =
            System.currentTimeMillis()

        datePicker.show()
    }

    private fun seleccionarHora(
        idReserva: String,
        fecha: String
    ) {

        val calendario = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            this,
            { _, hora, minuto ->

                val nuevaHora = String.format(
                    "%02d:%02d",
                    hora,
                    minuto
                )

                if (!hayInternet()) {
                    Toast.makeText(
                        this,
                        "Sin conexión. Intente nuevamente",
                        Toast.LENGTH_LONG
                    ).show()
                    return@TimePickerDialog
                }

                repository.reprogramarReserva(
                    idReserva,
                    fecha,
                    nuevaHora
                )
                    .addOnSuccessListener {
                        findViewById<TextView>(R.id.txtEstado).text = "Estado: Reprogramado"
                        findViewById<TextView>(R.id.txtFecha).text = "Fecha: $fecha"
                        findViewById<TextView>(R.id.txtHora).text = "Hora: $nuevaHora"
                        Toast.makeText(
                            this, "Reserva reprogramada correctamente", Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { error ->
                        if (error.message?.contains("PERMISSION_DENIED") == true) {
                            Log.e(
                                "Firestore", "Permiso denegado al reprogramar la reserva", error
                            )
                            Toast.makeText(
                                this, "No tienes permiso para reprogramar la reserva", Toast.LENGTH_LONG
                            ).show()

                        } else {
                            Toast.makeText(
                                this, "Error al reprogramar: ${error.message}", Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            },
            calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true
        )

        timePicker.show()
    }

    private fun actualizarEstado(
        idReserva: String,
        nuevoEstado: String
    ) {
        repository.actualizarEstado(
            idReserva,
            nuevoEstado
        )
            .addOnSuccessListener {
                findViewById<TextView>(R.id.txtEstado).text =
                    "Estado: $nuevoEstado"
                Toast.makeText(
                    this, "Reserva actualizada correctamente", Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { error ->
                if (error.message?.contains("PERMISSION_DENIED") == true) {
                    Log.e(
                        "Firestore", "Permiso denegado al modificar la reserva", error
                    )
                    Toast.makeText(
                        this, "No tienes permiso para modificar la reserva", Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this, "Error al actualizar la reserva: ${error.message}", Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun mostrarDialogoRechazo(
        idReserva: String
    ) {
        val input = EditText(this)
        input.hint =
            "Escribe el motivo para rechazar el servicio"
        AlertDialog.Builder(this)
            .setTitle("Rechazar reserva")
            .setMessage("Debe indicar una observación")
            .setView(input)
            .setPositiveButton("Rechazar") { _, _ ->
                val observacion =
                    input.text.toString().trim()
                if (observacion.isEmpty()) {
                    Toast.makeText(
                        this, "Debes ingresar una observación", Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }

                if (!hayInternet()) {
                    Toast.makeText(
                        this,
                        "Sin conexión. Intente nuevamente",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }

                repository.rechazarReserva(
                    idReserva,
                    observacion
                )
                    .addOnSuccessListener {
                        findViewById<TextView>(
                            R.id.txtEstado
                        ).text =
                            "Estado: Rechazado"
                        findViewById<TextView>(
                            R.id.txtObservacion
                        ).text =
                            "Observación: $observacion"
                        Toast.makeText(
                            this, "Reserva rechazada", Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { error ->

                        if (error.message?.contains("PERMISSION_DENIED") == true) {
                            Log.e(
                                "Firestore", "Permiso denegado al rechazar la reserva", error
                            )
                            Toast.makeText(
                                this, "No tienes permiso para rechazar la reserva", Toast.LENGTH_LONG
                            ).show()

                        } else {
                            Toast.makeText(
                                this, "Error al rechazar: ${error.message}", Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
            .setNegativeButton(
                "Cancelar",
                null
            )
            .show()
    }
}