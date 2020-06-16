package ar.edu.unsam.proyecto.futbollers.activities.nuevoEquipo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ar.edu.unsam.proyecto.futbollers.R
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.esafirm.imagepicker.model.Image
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_nuevo_equipo.*

class NuevoEquipoActivity : AppCompatActivity() {

    var nombreSeleccionado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_equipo)

        input_nombre_equipo.addTextChangedListener(object : TextWatcher {
            //Estos metodos tienen que estar implementados, burocracia
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(nuevoTexto: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nombreSeleccionado = nuevoTexto.toString()
            }
        })

        icono_foto_equipo.setOnClickListener{
            uploadImage()
        }

        foto_equipo.setOnClickListener{
            uploadImage()
        }




    }

    fun uploadImage(){
        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL)
            .toolbarArrowColor(Color.BLACK)
            .single()
            .limit(1)
            .showCamera(true)
            .imageDirectory("Camera")
            .enableLog(false)
            .start()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
             val image:Image = ImagePicker.getFirstImageOrNull(data)
            if(data === null){
                Toasty.error(this,"Mira, no se que hiciste, pero no me vas a hackear. La imagen no puede ser null.", Toast.LENGTH_SHORT).show()
            }else{
                    Glide.with(this)
                    .load(image.path)
                    .override(200,200)
                    .into(foto_equipo)
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

