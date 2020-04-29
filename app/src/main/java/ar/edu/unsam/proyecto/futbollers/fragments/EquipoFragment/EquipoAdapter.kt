package ar.edu.unsam.proyecto.futbollers.fragments.EquipoFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import com.squareup.picasso.Picasso


class EquipoAdapter : RecyclerView.Adapter<EquipoAdapter.EquipoViewHolder?>() {
    class EquipoViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var cv: CardView
        //var nombreEquipo1: TextView
        //var personAge: TextView

        var equipo_nombre: TextView
        var equipo_foto: ImageView
        var owner_icon: ImageView

        init {
            cv = itemView.findViewById(R.id.cv)
            equipo_nombre = itemView.findViewById(R.id.equipo_nombre)
            equipo_foto = itemView.findViewById(R.id.equipo_foto) as ImageView
            owner_icon = itemView.findViewById(R.id.owner_icon) as ImageView
        }

    }

    var equipos: MutableList<Equipo>? = ArrayList<Equipo>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): EquipoViewHolder {
        val v: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.row_fragment_equipo, viewGroup, false)
        return EquipoViewHolder(v)
    }

    override fun getItemCount(): Int {
        return equipos!!.size
    }

    override fun onBindViewHolder(equipoViewHolder: EquipoViewHolder, i: Int) {
        //personViewHolder.personName.setText(partidos.get(i).name)
        //personViewHolder.personAge.setText(partidos.get(i).age)

        //Es como decir equipoViewHolder.equipo_foto = equipo.get(i).equipo.foto
        equipoViewHolder.equipo_nombre.setText(equipos!!.get(i).nombre)
        Picasso.get().load(equipos!!.get(i).foto).into(equipoViewHolder.equipo_foto)

        //TODO: Transformer para owner

    }

}
