package co.edu.upb.moviles.gestor_tarea_f
import androidx.compose.ui.text.style.TextAlign
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.upb.moviles.gestor_tarea_f.data.BaseDeDatosHelper
import co.edu.upb.moviles.gestor_tarea_f.model.Tarea

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dbHelper = BaseDeDatosHelper(this)

        setContent {
            MaterialTheme {
                PantallaPrincipal(dbHelper)
            }
        }
    }
}

@Composable
fun PantallaPrincipal(dbHelper: BaseDeDatosHelper) {
    var tareaEnEdicion by remember { mutableStateOf<Tarea?>(null) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var tareas by remember { mutableStateOf(dbHelper.obtenerTareas()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Gestor Tareas",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        // Campos para ingresar tarea

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para guardar
        Button(onClick = {
            if (titulo.isNotBlank() && descripcion.isNotBlank() && fecha.isNotBlank()) {
                if (tareaEnEdicion == null) {
                    // Agregar nueva tarea
                    val nuevaTarea = Tarea(titulo = titulo, descripcion = descripcion, fecha = fecha)
                    dbHelper.agregarTarea(nuevaTarea)
                } else {
                    // Editar tarea existente
                    val tareaActualizada = tareaEnEdicion!!.copy(
                        titulo = titulo,
                        descripcion = descripcion,
                        fecha = fecha
                    )
                    dbHelper.actualizarTarea(tareaActualizada)
                    tareaEnEdicion = null
                }

                // Limpiar campos
                tareas = dbHelper.obtenerTareas()
                titulo = ""
                descripcion = ""
                fecha = ""
            }
        }) {
            Text(if (tareaEnEdicion == null) "Agregar Tarea" else "Actualizar Tarea")
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Lista de tareas
        LazyColumn {
            items(tareas) { tarea ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "📌 ${tarea.titulo}", style = MaterialTheme.typography.titleMedium)
                        Text(text = tarea.descripcion)
                        Text(text = "🗓️ ${tarea.fecha}", style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                dbHelper.eliminarTarea(tarea.id)
                                tareas = dbHelper.obtenerTareas() // refrescar
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                titulo = tarea.titulo
                                descripcion = tarea.descripcion
                                fecha = tarea.fecha
                                tareaEnEdicion = tarea
                            }
                        ) {
                            Text("Editar")
                        }

                        Button(
                            onClick = {
                                dbHelper.eliminarTarea(tarea.id)
                                tareas = dbHelper.obtenerTareas()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                        }
                    }

                }
            }
        }

    }
}


