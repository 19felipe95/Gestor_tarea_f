package co.edu.upb.moviles.gestor_tarea_f.model

data class Recordatorio(
    val id: Int? = null,
    val titulo: String,
    val nota: String,
    val fecha: String,
    val hora: String
)

