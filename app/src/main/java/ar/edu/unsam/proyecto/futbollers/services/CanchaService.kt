package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Cancha
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

object CanchaService {

    fun getCanchas(context: Context, callback: (MutableList<Cancha>) -> Unit){
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/canchas"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,

            Response.Listener<JSONArray> { response ->

                val canchas = Gson().fromJson(response.toString(), Array<Cancha>::class.java)

                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("ArmarPartidoActivity", response.toString())
                callback(canchas.toMutableList())
            },
            Response.ErrorListener {
                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it)
            })
        request.retryPolicy = DefaultRetryPolicy(250, 3, 1F)

        queue.add(request)

    }

    fun handleError(context: Context, error: VolleyError) {
        Log.i("ArmarPartidoActivity", "[DEBUG]: API Rest Error: +" + error)
        if (error is AuthFailureError) {
            Toast.makeText(context, "Las credenciales son invalidas", Toast.LENGTH_SHORT).show()
        } else if (error is NoConnectionError) {
            Toast.makeText(context, "Revise su conexion a internet", Toast.LENGTH_SHORT).show()
        } else if (error is ClientError) {
            val networkResponse = error.networkResponse
            if (networkResponse.data != null) {
                Log.i("ArmarPartidoActivity", "[DEBUG]: Server Response: +" + String(networkResponse.data))
                val jsonError = JSONObject(String(networkResponse.data))

                //TODO: Corregir esto que salio de patron copypaste
            }
            Toast.makeText(
                context,
                "Error inesperado al comunicarse con el servidor",
                Toast.LENGTH_SHORT
            ).show()

        }

    }

}