package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Partido
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.DATE_FORMAT
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.defaultPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.mediumPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.partidoPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.*


object PartidoService {

    fun getPartidosDelUsuario(context: Context, usuario: Usuario, callback: (MutableList<Partido>) -> Unit) {

        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/partidos/"
        Log.i("HomeActivity", url + usuario.idUsuario)

        val request = JsonArrayRequest(
            Request.Method.GET, url + usuario.idUsuario, null,

            Response.Listener<JSONArray> { response ->

                val gson = GsonBuilder()
                    .setDateFormat(DATE_FORMAT)
                    .create()

                val partidos = gson.fromJson(response.toString(), Array<Partido>::class.java)

                Log.i("HomeActivity", "[DEBUG]:Communication with API Rest Suceeded")
                Log.i("HomeActivity", response.toString())
                callback(partidos.toMutableList())
            },
            Response.ErrorListener {
                Log.i("HomeActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = mediumPolicy

        queue.add(request)

    }

    fun postNuevoPartido(context: Context, partido: Partido, callback: () -> Unit) {


        var partidoParseado = Partido().toJson(partido)
        //Log.i("ArmarPartidoAcitivty",  JSONObject(Constants.simpleDateFormatter.format(partido.fechaDeReserva!!.time)).toString())

        val fechaParseada = Constants.simpleDateFormatter.format(partido.fechaDeReserva!!.time)
        partidoParseado?.put("fechaDeReserva", fechaParseada)

        Log.i("ArmarPartidoActivity", "Partido parseado: "+partidoParseado)

        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/partidos"



        val request = JsonObjectRequest(
            Request.Method.POST, url, partidoParseado,

            Response.Listener<JSONObject> { response ->
                Log.i("ArmarPartidoActivity","response: "+response.toString())
                callback()
            },
            Response.ErrorListener {
                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Failed")
                Log.i("ArmarPartidoActivity",it.javaClass.name)
                //handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = partidoPolicy

        queue.add(request)

    }


    fun lambdaManejoErrores(context: Context, statusCode: Int) {
        //TODO: Corregir esto que salio de patron copypaste
        if (statusCode == 422) {
            Toast.makeText(context, "Ese mail ya pertenece a un usuario", Toast.LENGTH_SHORT).show()
        } else {

           Toast.makeText(
                context,
                "Error inesperado al comunicarse con el servidor",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

}