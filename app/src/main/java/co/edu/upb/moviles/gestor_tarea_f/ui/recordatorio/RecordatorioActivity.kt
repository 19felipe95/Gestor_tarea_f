package co.edu.upb.moviles.gestor_tarea_f.ui.recordatorio
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.edu.upb.moviles.gestor_tarea_f.R
import co.edu.upb.moviles.gestor_tarea_f.data.BaseDeDatosHelper
import co.edu.upb.moviles.gestor_tarea_f.model.Recordatorio

class RecordatorioActivity : AppCompatActivity() {

    private val calSeleccionado: Calendar = Calendar.getInstance()
    private var fechaFormateada: String = ""  // "YYYY-MM-DD"
    private var horaFormateada: String = ""   // "HH:mm"


    private lateinit var db: BaseDeDatosHelper
    private lateinit var adapter: RecordatorioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recordatorios)

        db = BaseDeDatosHelper(this)

        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val etNota   = findViewById<EditText>(R.id.etNota)
        val etFecha  = findViewById<EditText>(R.id.etFecha)
        val etHora   = findViewById<EditText>(R.id.etHora)
        // Al tocar cualquiera, abrimos el flujo Fecha -> Hora
        val abrirPicker: () -> Unit = {
            mostrarDateTimePicker(
                onResult = { fecha, hora, millis ->
                    fechaFormateada = fecha
                    horaFormateada = hora
                    etFecha.setText(fechaFormateada)
                    etHora.setText(horaFormateada)
                }
            )
        }

        etFecha.setOnClickListener { abrirPicker() }
        etHora.setOnClickListener { abrirPicker() }

        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val rv = findViewById<RecyclerView>(R.id.rvRecordatorios)

        adapter = RecordatorioAdapter(mutableListOf()) { r ->
            r.id?.let { db.eliminarRecordatorio(it) }
            cargar()
        }

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter


        btnAgregar.setOnClickListener {
            // Si el usuario aún no eligió fecha/hora, abrimos el picker y salimos
            if (fechaFormateada.isBlank() || horaFormateada.isBlank()) {
                mostrarDateTimePicker { fecha, hora, _ ->
                    fechaFormateada = fecha
                    horaFormateada = hora
                    etFecha.setText(fechaFormateada)
                    etHora.setText(horaFormateada)
                }
                return@setOnClickListener
            }

            val titulo = etTitulo.text.toString().trim()
            val nota   = etNota.text.toString().trim()

            if (titulo.isEmpty()) {
                etTitulo.error = "Escribe un título"
                return@setOnClickListener
            }

            val r = Recordatorio(
                titulo = titulo,
                nota   = nota,
                fecha  = fechaFormateada, // ← viene del DatePicker
                hora   = horaFormateada   // ← viene del TimePicker
            )

            val rowId = db.insertarRecordatorio(r)

            // (Opcional) programa notificación si ya agregaste ReminderReceiver y permiso
            // if (rowId > 0) {
            //     programarAlarma(rowId.toInt(), r.titulo, r.nota, r.fecha, r.hora)
            // }

            // Limpia campos
            etTitulo.text.clear()
            etNota.text.clear()
            fechaFormateada = ""; horaFormateada = ""
            etFecha.setText("")
            etHora.setText("")

            // Recarga lista
            cargar()
        }


        cargar()
    }

    private fun mostrarDateTimePicker(onResult: (String, String, Long) -> Unit) {
        // 1) DatePicker
        val ahora = Calendar.getInstance()
        val dp = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Guardamos fecha
                calSeleccionado.set(Calendar.YEAR, year)
                calSeleccionado.set(Calendar.MONTH, month)
                calSeleccionado.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // 2) TimePicker
                val tp = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        calSeleccionado.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calSeleccionado.set(Calendar.MINUTE, minute)
                        calSeleccionado.set(Calendar.SECOND, 0)
                        calSeleccionado.set(Calendar.MILLISECOND, 0)

                        // Formatos
                        val fmtFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val fmtHora  = SimpleDateFormat("HH:mm", Locale.getDefault())
                        val fecha = fmtFecha.format(calSeleccionado.time)
                        val hora  = fmtHora.format(calSeleccionado.time)

                        onResult(fecha, hora, calSeleccionado.timeInMillis)
                    },
                    ahora.get(Calendar.HOUR_OF_DAY),
                    ahora.get(Calendar.MINUTE),
                    true // formato 24h
                )
                tp.show()
            },
            ahora.get(Calendar.YEAR),
            ahora.get(Calendar.MONTH),
            ahora.get(Calendar.DAY_OF_MONTH)
        )
        dp.datePicker.minDate = ahora.timeInMillis // opcional: no permitir fechas pasadas
        dp.show()
    }


    private fun cargar() {
        adapter.actualizar(db.obtenerRecordatorios())
    }
}
