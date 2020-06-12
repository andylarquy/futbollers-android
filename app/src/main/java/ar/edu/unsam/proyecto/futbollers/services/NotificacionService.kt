package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Notificacion
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONArray

object NotificacionService {

    fun getNotificacionesDeCandidatosDelUsuario(context: Context, usuario: Usuario, callback: (MutableList<Notificacion>) -> Unit) {
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/notificaciones-candidatos/"

        val request = JsonArrayRequest(
            Request.Method.GET, url + UsuarioLogueado.usuario.idUsuario, null,

            Response.Listener<JSONArray> { response ->

                val gson = GsonBuilder()
                    .setDateFormat(Constants.DATE_FORMAT)
                    .create()

                val notificaciones = gson.fromJson(response.toString(), Array<Notificacion>::class.java)

                Log.i("CandidatosActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("CandidatosActivity","Candidatos response: "+ response.toString())
                callback(notificaciones.toMutableList())
            },
            Response.ErrorListener {
                Log.i("CandidatosActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = Constants.defaultPolicy

        queue.add(request)

    }

    fun lambdaManejoErrores(context: Context, statusCode: Int) {
        if (statusCode == 400) {
            Toast.makeText(context, "Server Response: Bad Request (TODO: mensaje mas bonito)", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error inesperado al comunicarse con el servidor", Toast.LENGTH_SHORT).show()
        }
    }

}