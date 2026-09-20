package com.example.profesional


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            val intent = Intent(this, ReservasActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

/*package com.example.profesional

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val reserva = hashMapOf(
            "nombreCliente" to "Prueba",
            "servicio" to "Prueba Firebase",
            "fecha" to "2026-09-14",
            "hora" to "22:00",
            "estado" to "SOLICITADA"
        )

        db.collection("reservas")
            .add(reserva)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    "FIREBASE",
                    "Firestore funciona correctamente. ID: ${documentReference.id}"
                )
            }
            .addOnFailureListener { error ->
                Log.e(
                    "FIREBASE",
                    "Error Firestore: ${error.message}",
                    error
                )
            }
    }
}*/