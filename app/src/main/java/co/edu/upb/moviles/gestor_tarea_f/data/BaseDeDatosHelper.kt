package co.edu.upb.moviles.gestor_tarea_f.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import co.edu.upb.moviles.gestor_tarea_f.model.Tarea

class BaseDeDatosHelper(context: Context) :
    SQLiteOpenHelper(context, "TareasDB", null, 1) {

    private val TABLE_NAME = "tareas"

    override fun onCreate(db: SQLiteDatabase) {
        val crearTabla = """
            CREATE TABLE $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT,
                descripcion TEXT,
                fecha TEXT
            )
        """.trimIndent()
        db.execSQL(crearTabla)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun agregarTarea(tarea: Tarea): Long {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put("titulo", tarea.titulo)
            put("descripcion", tarea.descripcion)
            put("fecha", tarea.fecha)
        }
        return db.insert(TABLE_NAME, null, valores)
    }

    fun obtenerTareas(): List<Tarea> {
        val lista = mutableListOf<Tarea>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val tarea = Tarea(
                    id = cursor.getInt(0),
                    titulo = cursor.getString(1),
                    descripcion = cursor.getString(2),
                    fecha = cursor.getString(3)
                )
                lista.add(tarea)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun eliminarTarea(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "id=?", arrayOf(id.toString()))
    }

    fun actualizarTarea(tarea: Tarea) {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put("titulo", tarea.titulo)
            put("descripcion", tarea.descripcion)
            put("fecha", tarea.fecha)
        }
        db.update(TABLE_NAME, valores, "id=?", arrayOf(tarea.id.toString()))
    }
}
