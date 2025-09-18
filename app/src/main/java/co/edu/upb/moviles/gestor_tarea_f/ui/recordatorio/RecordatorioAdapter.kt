package co.edu.upb.moviles.gestor_tarea_f.ui.recordatorio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.edu.upb.moviles.gestor_tarea_f.R
import co.edu.upb.moviles.gestor_tarea_f.model.Recordatorio

class RecordatorioAdapter(
    private var datos: MutableList<Recordatorio>,
    private val onEliminar: (Recordatorio) -> Unit
) : RecyclerView.Adapter<RecordatorioAdapter.VH>() {

    inner class VH(v: View): RecyclerView.ViewHolder(v) {
        val tvTitulo: TextView = v.findViewById(R.id.tvTitulo)
        val tvNota: TextView = v.findViewById(R.id.tvNota)
        val tvFechaHora: TextView = v.findViewById(R.id.tvFechaHora)
        val btnEliminar: Button = v.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recordatorio, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = datos[position]
        holder.tvTitulo.text = r.titulo
        holder.tvNota.text = r.nota
        holder.tvFechaHora.text = "${r.fecha}  •  ${r.hora}"
        holder.btnEliminar.setOnClickListener { onEliminar(r) }
    }

    override fun getItemCount() = datos.size

    fun actualizar(nuevaLista: List<Recordatorio>) {
        datos.clear()
        datos.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
