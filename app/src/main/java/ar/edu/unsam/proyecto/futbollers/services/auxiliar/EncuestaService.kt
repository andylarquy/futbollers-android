package ar.edu.unsam.proyecto.futbollers.services.auxiliar

import android.content.Context
import android.util.Log
import ar.edu.unsam.proyecto.futbollers.domain.Empresa
import ar.edu.unsam.proyecto.futbollers.domain.Encuesta
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.EmpresaService
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

object EncuestaService {

    fun getEncuestasDelUsuario(context: Context, usuario: Usuario, callback: (MutableList<Encuesta>) -> Unit) {
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/encuestas-usuario/"+usuario.idUsuario

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,

            Response.Listener<JSONArray> { response ->

                val encuestas = Gson().fromJson(response.toString(), Array<Encuesta>::class.java)

                Log.i("EncuestasActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("EncuestasActivity", response.toString())
                callback(encuestas.toMutableList())
            },
            Response.ErrorListener {
                Log.i("EncuestasActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, EmpresaService::lambdaManejoErrores)
            })
        request.retryPolicy = Constants.defaultPolicy

        queue.add(request)

    }

    fun updateEncuesta(context: Context, encuesta: Encuesta, callback: () -> Unit){
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/encuestas"

        val request = JsonObjectRequest(
            Request.Method.PUT, url, Encuesta().toJson(encuesta),

            Response.Listener<JSONObject> { response ->

                Log.i("EncuestasActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("EncuestasActivity", response.toString())
                callback()
            },
            Response.ErrorListener {
                Log.i("EncuestasActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, EmpresaService::lambdaManejoErrores)
            })
        request.retryPolicy = Constants.defaultPolicy

        queue.add(request)
    }

}