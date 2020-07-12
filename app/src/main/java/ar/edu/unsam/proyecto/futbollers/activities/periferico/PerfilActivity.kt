package ar.edu.unsam.proyecto.futbollers.activities.periferico

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.home.HomeActivity
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.AuxiliarService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.UsuarioService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.esafirm.imagepicker.model.Image
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.dialog_contrasenia.view.*

class PerfilActivity : AppCompatActivity() {

    val usuarioService = UsuarioService
    var usuarioLogueado = UsuarioLogueado.usuario
    var imagenSeleccionada: Bitmap? = null
    var nuevaContrasenia: String? = null
    var repetirContrasenia: String? = null
    var imagenCambioFlag: Boolean = false
    var auxiliarService = AuxiliarService
    lateinit var dialogContrasenia: MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        input_nombre.setText(usuarioLogueado.nombre)
        output_email.text = usuarioLogueado.email
        Picasso.get().load(usuarioLogueado.foto).into(foto_usuario)

        setupDialogContrasenia()

        icono_foto_usuario.setOnClickListener {
            uploadImage()
        }

        foto_usuario.setOnClickListener {
            uploadImage()
        }

        btn_cambiar_contrasenia.setOnClickListener {
            dialogContrasenia.show()
        }

        btn_aceptar_perfil.setOnClickListener{

            if(imagenCambioFlag) {
                uploadImageToServer(imagenSeleccionada!!)
            }else{
                callbackUploadImage(null)
            }

        }

    }

    fun uploadImageToServer(imagen: Bitmap){
        auxiliarService.uploadImage(this, imagen, ::callbackUploadImage)
    }

    //Aca se termina de procesar el update del perfil antes de mandar al back
    fun callbackUploadImage(url: String?) {

        var status = true
        val nuevoNombre = input_nombre.text.toString()

        val usuario: Usuario = Usuario()
        usuario.createCopy(usuarioLogueado)
        usuario.nombre = nuevoNombre

        if(imagenSeleccionada !== null){
            if(url !== null) {
                usuario.foto = url
            }
        }


        if(nuevoNombre.isBlank()){
            status = false
            Toasty.error(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
        }

        usuarioService.updatePerfil(this, usuario, ::callbackUpdatePerfil)
    }

    fun callbackUpdatePerfil(nuevoUsuario: Usuario){
        Toasty.success(this, "Has actualizado tu perfil satisfactoriamente!", Toast.LENGTH_SHORT).show()

        usuarioLogueado.nombre = nuevoUsuario.nombre

        if(!nuevoUsuario.password.isNullOrBlank()){
            usuarioLogueado.password = nuevoUsuario.password
        }

        if(!nuevoUsuario.foto.isNullOrBlank()){
            usuarioLogueado.foto = nuevoUsuario.foto
        }

        goToHome()
    }

    fun uploadImage() {
        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL)
            .toolbarArrowColor(Color.BLACK)
            .single()
            .limit(1)
            .showCamera(false)
            .imageDirectory("Camera")
            .enableLog(false)
            .start()
    }

    fun setupDialogContrasenia() {
        dialogContrasenia = MaterialDialog(this)
            .title(text = "Cambiar Contraseña")
            .customView(R.layout.dialog_contrasenia)
            .noAutoDismiss()
            .positiveButton(text = "Aceptar") {
                var status = true

                if (nuevaContrasenia.isNullOrBlank() || repetirContrasenia.isNullOrBlank()) {
                    status = false
                    Toasty.error(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT)
                        .show()
                }

                if (status && nuevaContrasenia!!.length < 8) {
                    status = false
                    Toasty.error(
                        this,
                        "La contraseña debe tener mas de 8 caracteres",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (status && !nuevaContrasenia.equals(repetirContrasenia)) {
                    status = false
                    Toasty.error(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }

                if (status) {
                    var usuario: Usuario = Usuario()
                    usuario.createCopy(usuarioLogueado)
                    usuario.password = nuevaContrasenia

                    usuarioService.updatePerfil(this, usuario, ::callbackUpdatePassword)
                    it.dismiss()
                }

            }
            .negativeButton(text = "Cancelar") {
                nuevaContrasenia = null
                repetirContrasenia = null
                it.dismiss()
            }

        //TODO: Delegar esto a otro lado (no va a pasar)
        val dialogView = dialogContrasenia.getCustomView()
        dialogView.nueva_contrasenia1.addTextChangedListener(object : TextWatcher {
            //Estos metodos tienen que estar implementados, burocracia
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(nuevoTexto: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nuevaContrasenia = nuevoTexto.toString()
                Log.i("PerfilActivity", nuevaContrasenia + " " + repetirContrasenia)
            }
        })

        dialogView.nueva_contrasenia2.addTextChangedListener(object : TextWatcher {
            //Estos metodos tienen que estar implementados, burocracia
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(nuevoTexto: CharSequence?, p1: Int, p2: Int, p3: Int) {
                repetirContrasenia = nuevoTexto.toString()
                Log.i("PerfilActivity", nuevaContrasenia + " " + repetirContrasenia)
            }
        })
    }

    fun callbackUpdatePassword(nuevoUsuario: Usuario) {
        usuarioLogueado = nuevoUsuario
        Toasty.success(this, "Has cambiado tu contraseña satisfactoriamente!", Toast.LENGTH_SHORT).show()
    }

    //Callback elegir foto
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image: Image = ImagePicker.getFirstImageOrNull(data)
            if (data === null) {
                Toasty.error(
                    this,
                    "Mira, no se que hiciste, pero no me vas a hackear. La imagen no puede ser null.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val bmImg: Bitmap = BitmapFactory.decodeFile(Uri.parse(image.path).toString())

                val drawableImage: Drawable =
                    BitmapDrawable(resources, Bitmap.createScaledBitmap(bmImg, 200, 200, true))

                foto_usuario.setImageDrawable(drawableImage)
                imagenSeleccionada = bmImg


                //Glide es async, asi que lo banco un toque
                Handler().postDelayed({
                    Log.i("PerfilActivity", foto_usuario.toString())
                    if (foto_usuario.drawable === null) {
                        Toasty.error(
                            this,
                            "Ha habido un error al procesar la imagen [DEBUG] Path: " + image.path + "]",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        imagenSeleccionada = foto_usuario.drawable.toBitmap()
                        imagenCambioFlag = true
                    }
                }, 200)

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        goToHome()
    }

    fun goToHome(){
        val intent = Intent(this, HomeActivity::class.java).apply{}
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
        this.finish()
    }

}