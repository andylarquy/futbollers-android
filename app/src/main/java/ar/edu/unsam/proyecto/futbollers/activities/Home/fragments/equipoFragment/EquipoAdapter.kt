package ar.edu.unsam.proyecto.futbollers.activities.Home.fragments.EquipoFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import com.squareup.picasso.Picasso


class EquipoAdapter : RecyclerView.Adapter<EquipoAdapter.EquipoViewHolder?>() {

    val usuarioLogueado = UsuarioLogueado.usuario

   class EquipoViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var cv: CardView
        //var nombreEquipo1: TextView
        //var personAge: TextView

        var equipo_nombre: TextView
        var equipo_foto: ImageView
        var owner_icon: ImageView
        var abandon_icon: ImageView
        var trash_icon: ImageView
        var edit_icon: ImageView

        init {
            cv = itemView.findViewById(R.id.cv)
            equipo_nombre = itemView.findViewById(R.id.equipo_nombre)
            equipo_foto = itemView.findViewById(R.id.equipo_foto) as ImageView
            owner_icon = itemView.findViewById(R.id.owner_icon) as ImageView
            abandon_icon = itemView.findViewById(R.id.abandon_icon)
            trash_icon = itemView.findViewById(R.id.trash_icon)
            edit_icon = itemView.findViewById(R.id.edit_icon)
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

        //Esto esta mal...
        // https://pbs.twimg.com/media/EMwGgNJXsAQWH4o.jpg

        if(equipos!!.get(i).esOwnerById(usuarioLogueado)){
            equipoViewHolder.owner_icon.setImageResource(R.drawable.ic_portrait_black_24dp)
            //TODO: Darle comportamiento a los botones
            equipoViewHolder.trash_icon.setImageResource(R.drawable.ic_delete_black_24dp)
            equipoViewHolder.edit_icon.setImageResource(R.drawable.ic_create_black_24dp)
        }else{
            equipoViewHolder.owner_icon.setImageResource(0)
            equipoViewHolder.trash_icon.setImageResource(0)
            equipoViewHolder.edit_icon.setImageResource(0)
        }

        equipoViewHolder.abandon_icon.setImageResource(R.drawable.ic_exit_to_app_black_24dp)
        equipoViewHolder.equipo_nombre.setText(equipos!!.get(i).nombre)
        Picasso.get().load(equipos!!.get(i).foto).into(equipoViewHolder.equipo_foto)



    }



}
