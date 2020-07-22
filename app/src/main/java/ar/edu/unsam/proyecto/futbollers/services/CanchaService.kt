package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Cancha
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.defaultPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.mediumPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.simpleDateFormatter
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONObject

object CanchaService {

    fun getCanchas(context: Context, callback: (MutableList<Cancha>) -> Unit) {
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
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)

    }

    fun getCanchasDeLaEmpresa(
        context: Context,
        idEmpresa: Long,
        callback: (MutableList<Cancha>) -> Unit
    ) {

        Log.i("ArmarPartidoActivity",  idEmpresa.toString())

        val queue = Volley.newRequestQueue(context)
        val url = "${Constants.BASE_URL}/empresas-canchas/"

        val request = JsonArrayRequest(
            Request.Method.GET, url + idEmpresa, null,
            Response.Listener<JSONArray> { response ->
                val canchas = Gson().fromJson(response.toString(), Array<Cancha>::class.java)
                Log.i(
                    "ArmarPartidoActivity", canchas.map { cancha -> cancha.foto }.toString()
                )
                callback(canchas.toMutableList())
            },
            Response.ErrorListener {
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)
    }

    fun lambdaManejoErrores(context: Context, statusCode: Int) {}


    fun validarFechaDeReserva(context: Context, fecha: Long, canchaSeleccionada: Cancha,callback: (Boolean) -> Unit) {
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/validar-fecha"

        val fechaParseada = JSONObject("{\"fecha\":\"" + simpleDateFormatter.format(fecha) + "\", " + "\"idCanchaReservada\":"+canchaSeleccionada.idCancha+"}")

        Log.i("ArmarPartidoActivity", fechaParseada.toString())

        val request = JsonObjectRequest(
            Request.Method.POST, url, fechaParseada,

            Response.Listener<JSONObject> { response ->

                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Suceeded")

                if (response.getString("status") == "200") {
                    callback(true)
                }

            },
            Response.ErrorListener {
                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Failed")
                handleErrorFecha(context, it, callback)
            })
        request.retryPolicy = mediumPolicy

        queue.add(request)
    }


    fun handleErrorFecha(context: Context, error: VolleyError, callback: (Boolean) -> Unit) {
        Log.i("ArmarPartidoActivity", "[DEBUG]: API Rest Error: +" + error)
        if (error is AuthFailureError) {
            Toast.makeText(context, "Las credenciales son invalidas", Toast.LENGTH_SHORT).show()
        } else if (error is NoConnectionError) {
            Toast.makeText(context, "Revise su conexion a internet", Toast.LENGTH_SHORT).show()
        } else if( error is TimeoutError){
            Toast.makeText(context, "El servidor tardo en responder, vuelva a reintentar mas tarde.", Toast.LENGTH_SHORT).show()
        } else if (error is ClientError) {
            val networkResponse = error.networkResponse
            if (networkResponse.data != null) {

                    if (networkResponse.statusCode == 400) {
                        callback(false)
                        Toasty.error(context!!, "Esta fecha de reserva ya está ocupada.", Toast.LENGTH_SHORT, true).show()
                    } else {
                        callback(false)
                        Toast.makeText(context, "Error inesperado al comunicarse con el servidor", Toast.LENGTH_SHORT).show()
                        Log.i("ArmarPartidoActivity", "No te asustes, creo que este error inesperado deberia no ser dificil de arreglar")
                    }
                }

        }
    }

}