package ar.edu.unsam.proyecto.futbollers.services

import android.R
import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Partido
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject


object PartidoService {

    fun getPartidosDelUsuario(context: Context, usuario: Usuario, callback: (MutableList<Partido>) -> Unit) {

        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/partidos/"
        Log.i("HomeActivity", url + usuario.id)

        val request = JsonArrayRequest(
            Request.Method.GET, url + usuario.id, null,

            Response.Listener<JSONArray> { response ->

                val partidos = Gson().fromJson(response.toString(), Array<Partido>::class.java)

                Log.i("HomeActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("HomeActivity", response.toString())
                callback(partidos.toMutableList())
            },
            Response.ErrorListener {
                Log.i("HomeActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it)
            })
        request.retryPolicy = DefaultRetryPolicy(250, 3, 1F)

        queue.add(request)

    }

    fun handleError(context: Context, error: VolleyError) {
        Log.i("HomeActivity", "[DEBUG]: API Rest Error: +" + error)
        if (error is AuthFailureError) {
            Toast.makeText(context, "Las credenciales son invalidas", Toast.LENGTH_SHORT).show()
        } else if (error is NoConnectionError) {
            Toast.makeText(context, "Revise su conexion a internet", Toast.LENGTH_SHORT).show()
        } else if (error is ClientError) {
            val networkResponse = error.networkResponse
            if (networkResponse.data != null) {
                Log.i("HomeActivity", "[DEBUG]: Server Response: +" + String(networkResponse.data))
                val jsonError = JSONObject(String(networkResponse.data))

                //TODO: Corregir esto que salio de patron copypaste
                if (jsonError.get("status") == 422) {
                    Toast.makeText(
                        context,
                        "Ese mail ya pertenece a un usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            Toast.makeText(
                context,
                "Error inesperado al comunicarse con el servidor",
                Toast.LENGTH_SHORT
            ).show()

        }

    }

}