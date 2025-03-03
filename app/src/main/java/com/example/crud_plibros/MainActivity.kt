package com.example.crud_plibros

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: AdminSQLiteOpenHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAgregarLibro: Button
    private lateinit var adapter: LibroAdapter
    private lateinit var imgLibro: ImageView
    private var imageUri: Uri? = null
    private lateinit var searchView: SearchView

    private val REQUEST_CODE_STORAGE = 101
    private val PICK_IMAGE_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = AdminSQLiteOpenHelper(this, "librosDB", null, 1)

        recyclerView = findViewById(R.id.recyclerViewLibros)
        btnAgregarLibro = findViewById(R.id.btnAgregarLibro)
        searchView = findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        cargarLibros()

        btnAgregarLibro.setOnClickListener {
            mostrarCuadroDialogo()
        }

        // Configurar el SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText) // Filtra los libros cuando el texto cambia
                return true
            }
        })

        checkPermissions()
    }
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    REQUEST_CODE_STORAGE
                )
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE
                )
            }
        }
    }

    private fun cargarLibros() {
        val libros = dbHelper.obtenerLibros().toMutableList()
        adapter = LibroAdapter(this, libros, dbHelper)
        recyclerView.adapter = adapter
    }

    private fun mostrarCuadroDialogo() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_contacto, null)

        val nombreEditText = dialogView.findViewById<EditText>(R.id.etNombre)
        val generoEditText = dialogView.findViewById<EditText>(R.id.etGenero)
        val autorEditText = dialogView.findViewById<EditText>(R.id.etAutor)
        val yearEditText = dialogView.findViewById<EditText>(R.id.etYear)
        val estatusEditText = dialogView.findViewById<EditText>(R.id.etEstatus)
        imgLibro = dialogView.findViewById(R.id.imgLibro)
        val btnSeleccionarImagen = dialogView.findViewById<Button>(R.id.btnSeleccionarImagen)

        btnSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Agrega los datos del libro")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = nombreEditText.text.toString()
                val genero = generoEditText.text.toString()
                val autor = autorEditText.text.toString()
                val year = yearEditText.text.toString()
                val estatus = estatusEditText.text.toString()

                if (nombre.isNotEmpty() && genero.isNotEmpty()) {
                    dbHelper.agregarLibro(nombre, genero, autor, year, estatus, imageUri?.toString())
                    cargarLibros()
                    Toast.makeText(this, "Contacto agregado exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageUri = data?.data
            imgLibro.setImageURI(imageUri)
        }
    }
}