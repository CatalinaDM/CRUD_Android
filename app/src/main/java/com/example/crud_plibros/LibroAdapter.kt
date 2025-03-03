package com.example.crud_plibros

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LibroAdapter(
    private val context: Context,
    private var libros: MutableList<Libro>,
    private val dbHelper: AdminSQLiteOpenHelper
) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>(), Filterable {

    private var librosFiltrados: MutableList<Libro> = libros.toMutableList()

    class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgLibro: ImageView = view.findViewById(R.id.imgLibro)
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvGenero: TextView = view.findViewById(R.id.tvGenero)
        val tvAutor: TextView = view.findViewById(R.id.tvAutor)
        val tvEstatus: TextView = view.findViewById(R.id.tvEstatus)
        val tvYear: TextView = view.findViewById(R.id.tvYear)
        val btnEditar: ImageView = view.findViewById(R.id.btnEditar)
        val btnEliminar: ImageView = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contacto, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = librosFiltrados[position]
        holder.tvNombre.text = libro.nombre
        holder.tvGenero.text = libro.genero
        holder.tvAutor.text = libro.autor
        holder.tvYear.text = libro.year
        holder.tvEstatus.text = libro.estatus

        val uri = libro.fotoUri?.let { Uri.parse(it) }
        Glide.with(context)
            .load(uri ?: R.drawable.icono)
            .placeholder(R.drawable.icono)
            .error(R.drawable.icono)
            .into(holder.imgLibro)

        holder.btnEliminar.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Eliminar Libro")
                .setMessage("¿Estás seguro de eliminar a ${libro.nombre}?")
                .setPositiveButton("Sí") { _, _ ->
                    dbHelper.eliminarLibro(libro.id)

                    // Eliminar el libro de la lista principal
                    libros.remove(libro)

                    // Eliminar el libro de la lista filtrada
                    librosFiltrados.remove(libro)

                    // Notificar al adaptador que el elemento ha sido eliminado
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, librosFiltrados.size)

                    Toast.makeText(context, "Libro eliminado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        holder.btnEditar.setOnClickListener {
            mostrarDialogoEditar(libro, position)

        }
    }

    fun actualizarListaCompleta(nuevaLista: MutableList<Libro>) {
        libros = nuevaLista
        librosFiltrados.clear()
        librosFiltrados.addAll(libros)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = librosFiltrados.size

    @SuppressLint("MissingInflatedId")
    private fun mostrarDialogoEditar(libro: Libro, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_agregar_contacto, null)

        // Usar los IDs correctos del layout
        val nombreEditText = dialogView.findViewById<EditText>(R.id.etNombre)
        val generoEditText = dialogView.findViewById<EditText>(R.id.etGenero)
        val autorEditText = dialogView.findViewById<EditText>(R.id.etAutor)
        val yearEditText = dialogView.findViewById<EditText>(R.id.etYear)
        val estatusEditText = dialogView.findViewById<EditText>(R.id.etEstatus)

        // Asignar los valores actuales del libro a los EditText
        nombreEditText.setText(libro.nombre)
        generoEditText.setText(libro.genero)
        autorEditText.setText(libro.autor)
        yearEditText.setText(libro.year)
        estatusEditText.setText(libro.estatus)

        AlertDialog.Builder(context)
            .setTitle("Editar Libros")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = nombreEditText.text.toString().trim()
                val nuevoGenero = generoEditText.text.toString().trim()
                val nuevoAutor = autorEditText.text.toString().trim()
                val nuevoYear = yearEditText.text.toString().trim()
                val nuevoEstatus = estatusEditText.text.toString().trim()

                if (nuevoNombre.isNotEmpty() && nuevoGenero.isNotEmpty()) {
                    dbHelper.actualizarLibro(
                        libro.id, nuevoNombre, nuevoGenero, nuevoAutor, nuevoYear, nuevoEstatus, libro.fotoUri
                    )
                    libros[position] = Libro(
                        libro.id, nuevoNombre, nuevoGenero, nuevoAutor, nuevoYear, nuevoEstatus, libro.fotoUri
                    )
                    notifyItemChanged(position)
                    Toast.makeText(context, "Libro actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Libro>()
                if (constraint.isNullOrEmpty()) {
                    // Si no hay texto de búsqueda, mostrar todos los libros
                    filteredList.addAll(libros)
                } else {
                    // Filtrar por nombre, año o estado
                    val filterPattern = constraint.toString().toLowerCase().trim()
                    for (libro in libros) {
                        if (libro.nombre.toLowerCase().contains(filterPattern) ||
                            libro.year.toLowerCase().contains(filterPattern) ||
                            libro.estatus.toLowerCase().contains(filterPattern)
                        ) {
                            filteredList.add(libro)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                librosFiltrados.clear()
                librosFiltrados.addAll(results?.values as List<Libro>)
                notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
            }
        }
    }

}