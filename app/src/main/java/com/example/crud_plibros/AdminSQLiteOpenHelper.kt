package com.example.crud_plibros

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class AdminSQLiteOpenHelper(
    context: Context,
    name: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DEBUG", "Base de datos cargada correctamente")
        db.execSQL(
            "CREATE TABLE libros (" +
                    "id_libro INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "genero TEXT NOT NULL, " +
                    "autor TEXT NOT NULL," +
                    "year TEXT , " +
                    "estatus TEXT NOT NULL," +
                    "fotoUri TEXT)"
        )
    }

    fun obtenerLibros(): List<Libro> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM libros", null)
        val listaLibros = mutableListOf<Libro>()

        Log.d("DEBUG", "Ejecutando consulta: SELECT * FROM libros")

        try {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_libro"))
                    val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                    val genero = cursor.getString(cursor.getColumnIndexOrThrow("genero"))
                    val autor = cursor.getString(cursor.getColumnIndexOrThrow("autor"))
                    val year = cursor.getString(cursor.getColumnIndexOrThrow("year"))
                    val estatus = cursor.getString(cursor.getColumnIndexOrThrow("estatus"))
                    val fotoUri = cursor.getString(cursor.getColumnIndexOrThrow("fotoUri"))

                    listaLibros.add(Libro(id, nombre, genero, autor, year, estatus, fotoUri))

                    Log.d("DEBUG", "Ver contacto: nombre=$nombre, genero=$genero, autor=$autor, year=$year, estatus=$estatus, fotoUri=$fotoUri")
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error al obtener los libros: ${e.message}")
        } finally {
            cursor.close()
            db.close()
        }

        return listaLibros
    }

    fun agregarLibro(
        nombre: String, genero: String, autor: String,
        year: String,
        estatus: String,
        fotoUri: String?
    ) {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("genero", genero)
            put("autor", autor)
            put("year", year)
            put("estatus", estatus)

            val uri = fotoUri ?: "android.resource://mx.edu.utng.crud_plibros/drawable/icono"
            put("fotoUri", uri)
        }

        val id = db.insert("libros", null, valores)
        if (id == -1L) {
            Log.e("ERROR", "No se pudo agregar el libro")
        } else {
            Log.d("DEBUG", "Libro agregado exitosamente con ID: $id")
        }

        db.close()
    }

    fun eliminarLibro(id: Int) {
        val db = this.writableDatabase
        val result = db.delete("libros", "id_libro = ?", arrayOf(id.toString()))
        if (result > 0) {
            Log.d("DEBUG", "Libro eliminado exitosamente con ID: $id")
        } else {
            Log.e("ERROR", "No se pudo eliminar el Libro con ID: $id")
        }
        db.close()
    }

    fun actualizarLibro(
        id_libro: Int, nuevoNombre: String, nuevoGenero: String, nuevoAutor: String,
        nuevoYear: String, nuevoEstatus: String, nuevaFotoUri: String?
    ) {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nuevoNombre)
            put("genero", nuevoGenero)
            put("autor", nuevoAutor)
            put("year", nuevoYear)
            put("estatus", nuevoEstatus)
            if (nuevaFotoUri != null) {
                put("fotoUri", nuevaFotoUri)
            }
        }

        val result = db.update("libros", valores, "id_libro = ?", arrayOf(id_libro.toString()))
        if (result > 0) {
            Log.d("DEBUG", "Libro actualizado exitosamente con ID: $id_libro")
        } else {
            Log.e("ERROR", "No se pudo actualizar el Libro con ID: $id_libro ")
        }

        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS libros")
        onCreate(db)
    }
}