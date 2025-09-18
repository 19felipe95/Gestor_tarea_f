package co.edu.upb.moviles.gestor_tarea_f.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import co.edu.upb.moviles.gestor_tarea_f.model.Tarea
import co.edu.upb.moviles.gestor_tarea_f.model.Recordatorio

private const val DB_NAME = "gestor.db"
private const val DB_VERSION = 4 // súbelo si no ves cambios

// ---------- TABLA TAREAS ----------
private const val SQL_CREATE_TAREAS = """
    CREATE TABLE IF NOT EXISTS tareas (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        titulo TEXT NOT NULL,
        descripcion TEXT,
        fecha TEXT
    );
"""
private const val SQL_DROP_TAREAS = "DROP TABLE IF EXISTS tareas;"

// ---------- TABLA RECORDATORIOS ----------
private const val SQL_CREATE_RECORDATORIOS = """
    CREATE TABLE IF NOT EXISTS recordatorios (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        titulo TEXT NOT NULL,
        nota TEXT,
        fecha TEXT NOT NULL,
        hora TEXT NOT NULL
    );
"""
private const val SQL_DROP_RECORDATORIOS = "DROP TABLE IF EXISTS recordatorios;"

class BaseDeDatosHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TAREAS)
        db.execSQL(SQL_CREATE_RECORDATORIOS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // para el proyecto académico, drop + recreate
        db.execSQL(SQL_DROP_TAREAS)
        db.execSQL(SQL_DROP_RECORDATORIOS)
        onCreate(db)
    }

    // =============== CRUD TAREAS ===============
    /** Compatibilidad con tu código: MainActivity llama agregarTarea(...) */
    fun agregarTarea(t: Tarea): Long = insertarTarea(t)

    fun insertarTarea(t: Tarea): Long {
        return try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("titulo", t.titulo)
                put("descripcion", t.descripcion)
                put("fecha", t.fecha)
            }
            val rowId = db.insert("tareas", null, values)
            Log.d("DB", "Insert tarea rowId=$rowId")
            rowId
        } catch (e: Exception) {
            Log.e("DB", "Error insertando tarea", e)
            -1L
        }
    }

    fun obtenerTareas(): List<Tarea> {
        val lista = mutableListOf<Tarea>()
        try {
            val db = readableDatabase
            val c = db.rawQuery(
                "SELECT id, titulo, descripcion, fecha FROM tareas ORDER BY id DESC",
                null
            )
            c.use {
                while (it.moveToNext()) {
                    lista.add(
                        Tarea(
                            id = it.getInt(0),
                            titulo = it.getString(1) ?: "",
                            descripcion = it.getString(2) ?: "",
                            fecha = it.getString(3) ?: ""
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("DB", "Error leyendo tareas", e)
        }
        Log.d("DB", "obtenerTareas() -> ${lista.size} items")
        return lista
    }

    /** Compatibilidad con tu código: MainActivity llama actualizarTarea(t = ...) */
    fun actualizarTarea(t: Tarea) {
        if (t.id == null) return
        try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("titulo", t.titulo)
                put("descripcion", t.descripcion)
                put("fecha", t.fecha)
            }
            val rows = db.update("tareas", values, "id = ?", arrayOf(t.id.toString()))
            Log.d("DB", "Actualizar tarea id=${t.id} rows=$rows")
        } catch (e: Exception) {
            Log.e("DB", "Error actualizando tarea", e)
        }
    }

    fun eliminarTarea(id: Int) {
        try {
            val db = writableDatabase
            val rows = db.delete("tareas", "id = ?", arrayOf(id.toString()))
            Log.d("DB", "Eliminar tarea id=$id rows=$rows")
        } catch (e: Exception) {
            Log.e("DB", "Error eliminando tarea", e)
        }
    }

    // =============== CRUD RECORDATORIOS (para tu segunda vista XML) ===============
    fun insertarRecordatorio(r: Recordatorio): Long {
        return try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("titulo", r.titulo)
                put("nota", r.nota)
                put("fecha", r.fecha)
                put("hora", r.hora)
            }
            db.insert("recordatorios", null, values)
        } catch (e: Exception) {
            Log.e("DB", "Error insertando recordatorio", e)
            -1L
        }
    }

    fun obtenerRecordatorios(): List<Recordatorio> {
        val lista = mutableListOf<Recordatorio>()
        try {
            val db = readableDatabase
            val c = db.rawQuery(
                "SELECT id, titulo, nota, fecha, hora FROM recordatorios ORDER BY id DESC",
                null
            )
            c.use {
                while (it.moveToNext()) {
                    lista.add(
                        Recordatorio(
                            id = it.getInt(0),
                            titulo = it.getString(1) ?: "",
                            nota = it.getString(2) ?: "",
                            fecha = it.getString(3) ?: "",
                            hora = it.getString(4) ?: ""
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("DB", "Error leyendo recordatorios", e)
        }
        return lista
    }

    fun eliminarRecordatorio(id: Int) {
        try {
            val db = writableDatabase
            db.delete("recordatorios", "id = ?", arrayOf(id.toString()))
        } catch (e: Exception) {
            Log.e("DB", "Error eliminando recordatorio", e)
        }
    }
}

