package com.example.profesional

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration
import java.util.Calendar

class ReservasActivity : AppCompatActivity() {

    private val repository = ReservaRepository()

    private lateinit var recyclerReservas: RecyclerView
    private lateinit var spinnerEstado: Spinner
    private lateinit var adapter: ReservaAdapter
    private lateinit var btnSeleccionarFecha: Button
    private lateinit var btnLimpiarFecha: Button

    private var fechaSeleccionada: String? = null
    private var listenerReservas: ListenerRegistration? = null

    private val listaReservas = mutableListOf<Reserva>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)

        recyclerReservas = findViewById(R.id.recyclerReservas)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha)
        btnLimpiarFecha = findViewById(R.id.btnLimpiarFecha)

        val estados = arrayOf(
            "Todos",
            "Solicitado",
            "Aceptado",
            "Rechazado",
            "Reprogramado"
        )
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            estados
        )
        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerEstado.adapter = spinnerAdapter
        recyclerReservas.layoutManager = LinearLayoutManager(this)
        adapter = ReservaAdapter(listaReservas) { reserva ->

            val intent = Intent(
                this,
                DetalleReservaActivity::class.java
            )
            intent.putExtra("idReserva", reserva.id)
            startActivity(intent)
        }

        recyclerReservas.adapter = adapter
        spinnerEstado.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    val estadoSeleccionado = estados[position]
                    cargarReservas(estadoSeleccionado)
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                    cargarReservas("Todos")
                }
            }

        btnSeleccionarFecha.setOnClickListener {

            val calendario = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->

                    val mes = String.format("%02d", month + 1)
                    val dia = String.format("%02d", dayOfMonth)

                    fechaSeleccionada = "$year-$mes-$dia"
                    btnSeleccionarFecha.text =
                        "Fecha: $fechaSeleccionada"
                    val estadoActual =
                        spinnerEstado.selectedItem.toString()
                    cargarReservas(estadoActual)
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        btnLimpiarFecha.setOnClickListener {
            fechaSeleccionada = null
            btnSeleccionarFecha.text =
                "Seleccionar fecha"
            val estadoActual =
                spinnerEstado.selectedItem.toString()
            cargarReservas(estadoActual)
        }
    }

    private fun cargarReservas(estado: String) {

        listenerReservas?.remove()
        listenerReservas = repository.escucharReservas(
            estado = estado,
            onResultado = { reservas ->
                listaReservas.clear()
                for (reserva in reservas) {

                    val estadoReserva = reserva.estado
                    if (estado != "Todos") {

                        val estadoBuscado = when (estado) {
                            "Solicitado" -> "SOLICITADA"
                            "Aceptado" -> "ACEPTADA"
                            "Rechazado" -> "RECHAZADA"
                            "Reprogramado" -> "REPROGRAMADA"
                            else -> estado
                        }
                        if (estadoReserva != estadoBuscado) {
                            continue
                        }
                    }
                    if (fechaSeleccionada != null &&
                        reserva.fecha != fechaSeleccionada
                    ) {
                        continue
                    }

                    listaReservas.add(reserva)
                }
                adapter.notifyDataSetChanged()
            },

            onError = { error ->
                Toast.makeText(
                    this, "Error al cargar las reservas: ${error.message}", Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    override fun onStart() {
        super.onStart()
        val estadoActual =
            spinnerEstado.selectedItem?.toString() ?: "Todos"
        cargarReservas(estadoActual)
    }

    override fun onStop() {
        super.onStop()
        listenerReservas?.remove()
        listenerReservas = null
    }
}