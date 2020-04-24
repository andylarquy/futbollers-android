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
        //var nombreEquipo1: TextView
        //var personAge: TextView

        var empresa_nombre: TextView
        var empresa_direccion: TextView
        var partido_foto: ImageView

        init {
            cv = itemView.findViewById(R.id.cv)
            //nombreEquipo1 = itemView.findViewById(R.id.person_name)
            //personAge = itemView.findViewById(R.id.person_age)
            empresa_nombre = itemView.findViewById(R.id.empresa_nombre)
            empresa_direccion = itemView.findViewById(R.id.empresa_direccion)
            partido_foto = itemView.findViewById(R.id.partido_foto) as ImageView
        }

    }

    var partidos: MutableList<Partido>? = ArrayList<Partido>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PartidoViewHolder {
        val v: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.row_fragment_partido, viewGroup, false)
        return PartidoViewHolder(v)
    }

    override fun getItemCount(): Int {
        return partidos!!.size
    }

    override fun onBindViewHolder(partidoViewHolder: PartidoViewHolder, i: Int) {
        //personViewHolder.personName.setText(partidos.get(i).name)
        //personViewHolder.personAge.setText(partidos.get(i).age)

        //Es como decir partidoViewHolder.partido_foto = partidos.get(i).empresa.foto
        partidoViewHolder.empresa_nombre.setText(partidos!!.get(i).empresa!!.nombre)
        partidoViewHolder.empresa_direccion.setText(partidos!!.get(i).empresa!!.direccion)
        Picasso.get().load(partidos!!.get(i).empresa!!.foto).into(partidoViewHolder.partido_foto)

    }

}
