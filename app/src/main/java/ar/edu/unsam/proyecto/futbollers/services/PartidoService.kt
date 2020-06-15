package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Partido
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.DATE_FORMAT
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.mediumPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.partidoPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONObject


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
                handleError(context, it, ::lambdaManejoErroresGetPartido)
            })
        request.retryPolicy = mediumPolicy

        queue.add(request)

    }

    fun postNuevoPartido(context: Context, partido: Partido, callback: () -> Unit, callbackError: (String) -> Unit) {


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
                handleErrorPostPartido(context, it, callbackError)
            })
        request.retryPolicy = partidoPolicy

        queue.add(request)

    }


    //TODO: Manejar errores de get partido
    fun lambdaManejoErroresGetPartido(context: Context, statusCode: Int) {
        //TODO: Atajar el error de la API REST, no solo el status code
        Toasty.error(context, "Ha habido un error inesperado al comunicarse con el servidor", Toast.LENGTH_SHORT, true).show()
    }

    fun handleErrorPostPartido(context: Context, error: VolleyError, callbackError: (String) -> Unit) {

        Log.i("HomeActivity", "[DEBUG]: API Rest Error: +" + error)
        if (error is AuthFailureError) {
            Toast.makeText(context, "Las credenciales son invalidas", Toast.LENGTH_SHORT).show()
        } else if (error is NoConnectionError) {
            Toast.makeText(context, "Revise su conexion a internet", Toast.LENGTH_SHORT).show()
        } else if (error is ClientError) {
            val networkResponse = error.networkResponse
            if (networkResponse.data != null) {
                if ( networkResponse.statusCode == 404) {
                    callbackError("No se han encontrado suficentes jugadores para cubrir los puestos con esos parametros de busqueda")
                } else {
                    callbackError("Ha habido un error inesperado al comunicarse con el servidor")
                }
            }
        }








    }

    fun confirmarPartido(context: Context, partido: Partido, callback: () -> Unit) {
       val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/confirmar-partido/${partido.idPartido}"

        val request = JsonObjectRequest(
            Request.Method.POST, url, null,

            Response.Listener<JSONObject> { response ->
                Log.i("ArmarPartidoActivity","response: "+response.toString())
                callback()
            },
            Response.ErrorListener {
                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErroresGetPartido)
            })
        request.retryPolicy = partidoPolicy

        queue.add(request)
    }


}