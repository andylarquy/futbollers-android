package ar.edu.unsam.proyecto.futbollers.activities.ArmarPartido.steps.elegirCancha

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.domain.Cancha
import com.squareup.picasso.Picasso


class CanchaAdapter: RecyclerView.Adapter<CanchaAdapter.CanchaViewHolder?>() {


    class CanchaViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var cv: CardView
        var cantidad_maxima: TextView
        var superficie: TextView
        var foto: ImageView

        init{
            cv = itemView.findViewById(R.id.cv)
            cantidad_maxima = itemView.findViewById(R.id.cantidad_maxima)
            superficie = itemView.findViewById(R.id.superficie)
            foto = itemView.findViewById(R.id.cancha_foto) as ImageView
        }
    }

    //[Debug]: Hardcodeo la cancha


    var canchas: MutableList<Cancha> = canchasHardcodeadas()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CanchaViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_cancha, viewGroup, false)
        return CanchaViewHolder(v)

    }

    override fun getItemCount(): Int {
        return canchas.size
    }

    override fun onBindViewHolder(canchaViewHolder: CanchaViewHolder, i: Int) {

        canchaViewHolder.cantidad_maxima.text = canchas[i].cantidadJugadores.toString()
        canchaViewHolder.superficie.text = canchas[i].superficie
        Picasso.get().load(canchas[i].foto).into(canchaViewHolder.foto)
    }

    fun canchasHardcodeadas(): MutableList<Cancha> {
        var debugCancha = Cancha()
        debugCancha.foto = "https://i.imgur.com/8tKp3V1.jpg"
        debugCancha.id = "C1"
        debugCancha.cantidadJugadores = 5
        debugCancha.superficie = "sintetico"

        return mutableListOf(debugCancha)
    }

}