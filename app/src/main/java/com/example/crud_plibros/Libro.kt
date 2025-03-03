package com.example.crud_plibros

data class Libro(
    val id: Int,
    val nombre: String,
    val genero: String,
    val autor: String,
    val year: String,
    val estatus: String,
    val fotoUri: String
)