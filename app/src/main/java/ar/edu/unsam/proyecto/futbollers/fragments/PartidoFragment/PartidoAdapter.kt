package ar.edu.unsam.proyecto.futbollers.fragments.PartidoFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.domain.Partido
import com.squareup.picasso.Picasso


class PartidoAdapter : RecyclerView.Adapter<PartidoAdapter.PartidoViewHolder?>() {
    class PartidoViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var cv: CardView

        var empresa_nombre: TextView
        var empresa_direccion: TextView
        var equipo1_nombre: TextView
        var equipo2_nombre: TextView
        var partido_foto: ImageView

        init {
            cv = itemView.findViewById(R.id.cv)

            empresa_nombre = itemView.findViewById(R.id.empresa_nombre)
            empresa_direccion = itemView.findViewById(R.id.empresa_direccion)
            equipo1_nombre = itemView.findViewById(R.id.equipo1_nombre)
            equipo2_nombre = itemView.findViewById(R.id.equipo2_nombre)
            partido_foto = itemView.findViewById(R.id.partido_foto) as ImageView

        }

    }

    var partidos: MutableList<Partido>? = ArrayList<Partido>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PartidoViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_fragment_partido, viewGroup, false)
        return PartidoViewHolder(v)
    }

    override fun getItemCount(): Int {
        return partidos!!.size
    }

    override fun onBindViewHolder(partidoViewHolder: PartidoViewHolder, i: Int) {

        //TODO: Adaptar y mostrar fecha y hora
        partidoViewHolder.empresa_nombre.setText(partidos!!.get(i).empresa!!.nombre)
        partidoViewHolder.empresa_direccion.setText(partidos!!.get(i).empresa!!.direccion)
        partidoViewHolder.equipo1_nombre.setText(partidos!!.get(i).equipo1!!.nombre)
        partidoViewHolder.equipo2_nombre.setText(partidos!!.get(i).equipo2!!.nombre)
        Picasso.get().load(partidos!!.get(i).empresa!!.foto).into(partidoViewHolder.partido_foto)



    }

}
