package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Partido
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.defaultPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray


object PartidoService {

    fun getPartidosDelUsuario(
        context: Context,
        usuario: Usuario,
        callback: (MutableList<Partido>) -> Unit
    ) {

        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/partidos/"
        Log.i("HomeActivity", url + usuario.idUsuario)

        val request = JsonArrayRequest(
            Request.Method.GET, url + usuario.idUsuario, null,

            Response.Listener<JSONArray> { response ->

                val partidos = Gson().fromJson(response.toString(), Array<Partido>::class.java)

                Log.i("HomeActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("HomeActivity", response.toString())
                callback(partidos.toMutableList())
            },
            Response.ErrorListener {
                Log.i("HomeActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)

    }

    fun lambdaManejoErrores(context: Context, statusCode: Int) {
        //TODO: Corregir esto que salio de patron copypaste
        if (statusCode == 422) {
            Toast.makeText(context, "Ese mail ya pertenece a un usuario", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error inesperado al comunicarse con el servidor", Toast.LENGTH_SHORT).show()
        }

    }

}