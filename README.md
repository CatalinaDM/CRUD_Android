
# Documentación del Proyecto CRUD de Libros en Android con SQLite

## *1. Introducción*
Este documento proporciona una explicación detallada del funcionamiento del sistema de gestión de libros en Android utilizando SQLite. Se incluyen detalles sobre las clases, sus métodos y su interacción en el proyecto.

---

## *2. Clases Principales*
### *2.1. AdminSQLiteOpenHelper.kt*
#### *Descripción:*
Esta clase es un helper que maneja la base de datos SQLite, permitiendo la creación y manipulación de registros de libros.

#### *Métodos principales:*
- *onCreate(db: SQLiteDatabase)*: Crea la base de datos y define la estructura de la tabla libros.
- *obtenerLibros()*: Obtiene todos los libros almacenados en la base de datos y devuelve una lista de objetos Libro.
- *agregarLibro(...)*: Inserta un nuevo libro en la base de datos.
- *eliminarLibro(id: Int)*: Elimina un libro según su ID.
- *actualizarLibro(...)*: Modifica los datos de un libro existente.
- *onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)*: Maneja la actualización de la base de datos.

---

### *2.2. Libro.kt*
#### *Descripción:*
Define la estructura de un libro como un objeto de datos.

#### *Propiedades:*
- id: Identificador único del libro.
- nombre: Nombre del libro.
- genero: Género literario.
- autor: Autor del libro.
- year: Año de publicación.
- estatus: Estado del libro (leído, pendiente, etc.).
- fotoUri: URI de la imagen del libro.

---

### *2.3. LibroAdapter.kt*
#### *Descripción:*
Este adaptador maneja la lista de libros y su representación en el RecyclerView.

#### *Métodos principales:*
- *onCreateViewHolder(...)*: Crea y devuelve un ViewHolder para cada elemento.
- *onBindViewHolder(...)*: Asigna los datos de un libro a los elementos de la interfaz.
- *getItemCount()*: Devuelve la cantidad de libros en la lista.
- *mostrarDialogoEditar(...)*: Abre un diálogo para editar los datos de un libro.
- *getFilter()*: Implementa un filtro de búsqueda para encontrar libros por nombre, año o estatus.

---

### *2.4. MainActivity.kt*
#### *Descripción:*
Clase principal de la aplicación que maneja la interfaz de usuario y las interacciones.

#### *Métodos principales:*
- *onCreate(...)*: Inicializa la actividad y configura la lista de libros.
- *cargarLibros()*: Obtiene los libros de la base de datos y los muestra en RecyclerView.
- *mostrarDialogoAgregarLibro()*: Despliega un formulario para agregar un nuevo libro.
- *abrirGaleria()*: Permite seleccionar una imagen de la galería.

---

## *3. Interacción Entre Componentes*
1. MainActivity se encarga de mostrar los libros y manejar las interacciones del usuario.
2. AdminSQLiteOpenHelper gestiona la base de datos y provee métodos para operaciones CRUD.
3. LibroAdapter facilita la visualización de los libros en RecyclerView, permitiendo edición y eliminación.
4. Libro define la estructura de los datos almacenados.

---
###Video demostrativo
 **Video demostrativo**: [Enlace al video demostrativo](https://drive.google.com/file/d/1FTg16rx7Tvdgq9VW35vj2H1oO_SWhsSK/view?usp=sharing)

## *4. Conclusión*
Este proyecto proporciona una gestión eficiente de libros mediante SQLite, ofreciendo una interfaz amigable y funcionalidades completas para agregar, modificar y eliminar registros.
