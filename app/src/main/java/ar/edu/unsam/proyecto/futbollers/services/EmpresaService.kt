package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Empresa
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.defaultPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

object EmpresaService {

    fun getEmpresas(context: Context, callback: (MutableList<Empresa>) -> Unit) {
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/empresas"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,

            Response.Listener<JSONArray> { response ->

                val equipos = Gson().fromJson(response.toString(), Array<Empresa>::class.java)

                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("ArmarPartidoActivity", response.toString())
                callback(equipos.toMutableList())
            },
            Response.ErrorListener {
                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)

    }

    fun lambdaManejoErrores(context: Context, statusCode: Int) {}

}