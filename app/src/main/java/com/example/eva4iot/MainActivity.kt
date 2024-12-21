package com.example.eva4iot

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    private lateinit var sonidoTextView: TextView
    private lateinit var actualizarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inicialización de vistas
        sonidoTextView = findViewById(R.id.sonidoTextView)
        actualizarButton = findViewById(R.id.actualizarButton)

        // Configuración de la referencia a Firebase
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("datos")

        // Función para leer y mostrar el nivel de sonido
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Obtener el último dato agregado
                val data = dataSnapshot.children.lastOrNull()
                if (data != null) {
                    val soundLevel = data.child("sonido").getValue(Int::class.java)
                    if (soundLevel != null) {
                        // Actualizar el TextView con el nivel de sonido
                        sonidoTextView.text = "Nivel de Sonido: $soundLevel"
                    } else {
                        sonidoTextView.text = "No se ha recibido el valor"
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejo de errores
                Toast.makeText(applicationContext, "Error al acceder a los datos", Toast.LENGTH_SHORT).show()
            }
        }

        // Escuchar cambios en los datos de Firebase
        myRef.addValueEventListener(valueEventListener)

        // Función para actualizar el nivel de sonido al hacer clic en el botón
        actualizarButton.setOnClickListener {
            // Este botón actualizará los datos al hacer clic
            myRef.addListenerForSingleValueEvent(valueEventListener)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}