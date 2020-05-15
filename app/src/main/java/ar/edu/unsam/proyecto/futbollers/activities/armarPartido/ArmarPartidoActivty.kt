package ar.edu.unsam.proyecto.futbollers.activities.ArmarPartido

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ar.edu.unsam.proyecto.futbollers.R

class ArmarPartidoActivty : AppCompatActivity() {
    val activityArmarPartido = R.layout.activity_armar_partido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityArmarPartido)

    }
}