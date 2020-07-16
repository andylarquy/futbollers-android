package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado.usuario
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.defaultPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.longPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONObject

object EquipoService {
    fun getEquiposDelUsuario(context: Context, usuarioLogueado: Usuario, callback: (MutableList<Equipo>) -> Unit) {
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/equipos/"
        Log.i("HomeActivity", url + usuario.idUsuario)

        val request = JsonArrayRequest(
            Request.Method.GET, url + usuario.idUsuario, null,

            Response.Listener<JSONArray> { response ->

                val equipos = Gson().fromJson(response.toString(), Array<Equipo>::class.java)

                Log.i("HomeActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("HomeActivity", response.toString())
                callback(equipos.toMutableList())
            },
            Response.ErrorListener {
                Log.i("HomeActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = longPolicy

        queue.add(request)

    }

    fun getEquiposAdministradosPorElUsuario(context: Context, usuarioLogueado: Usuario, callback: (MutableList<Equipo>) -> Unit){
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/equipos-owner/"
        Log.i("HomeActivity", url + usuario.idUsuario)

        val request = JsonArrayRequest(
            Request.Method.GET, url + usuario.idUsuario, null,

            Response.Listener<JSONArray> { response ->

                val equipos = Gson().fromJson(response.toString(), Array<Equipo>::class.java)

                Log.i("HomeActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("HomeActivity", response.toString())
                callback(equipos.toMutableList())
            },
            Response.ErrorListener {
                Log.i("HomeActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = longPolicy

        queue.add(request)
    }

    fun postEquipo(context: Context, equipo: Equipo, callback: () -> Unit, callbackError: (String) -> Unit){
        val queue = Volley.newRequestQueue(context)

        Log.i("NuevoEquipoActivity", Equipo().toJson(equipo).toString())
        Log.i("NuevoEquipoActivity", Gson().toJson(equipo).toString())

        val url = "${Constants.BASE_URL}/equipos"

        val request = JsonObjectRequest(
            Request.Method.POST, url, Equipo().toJson(equipo),

            Response.Listener<JSONObject> { response ->

                Log.i("NuevoEquipoActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("NuevoEquipoActivity", response.toString())
                callback()
            },
            Response.ErrorListener {
                Log.i("NuevoEquipoActivity", "[DEBUG]:Communication with API Rest Failed")
                handleErrorPostEquipo(context, it, callbackError)
            })
        request.retryPolicy = longPolicy

        queue.add(request)
    }
    
    fun handleErrorPostEquipo(context: Context, error: VolleyError,callbackError: (String) -> Unit){

        error.networkResponse.data

        val responseBody = String(error.networkResponse.data,  Charsets.UTF_8)
        val data = JSONObject(responseBody)
        val errorMessage = data.optString("message")
        callbackError(errorMessage)

    }

    fun getEquipoById(context: Context, idEquipoAEditar: Long, callback: (Equipo) -> Unit){
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/equipo/"

        val request = JsonObjectRequest(
            Request.Method.GET, url + idEquipoAEditar, null,

            Response.Listener<JSONObject> { response ->

                val equipo = Gson().fromJson(response.toString(), Equipo::class.java)

                Log.i("NuevoEquipoActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("NuevoEquipoActivity", response.toString())
                callback(equipo)
            },
            Response.ErrorListener {
                Log.i("NuevoEquipoActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = longPolicy

        queue.add(request)
    }

    fun updateEquipo(context: Context, equipo: Equipo, callback: () -> Unit, callbackError: (String) -> Unit){
        val queue = Volley.newRequestQueue(context)

        Log.i("NuevoEquipoActivity", Equipo().toJson(equipo).toString())
        Log.i("NuevoEquipoActivity", Gson().toJson(equipo).toString())

        val url = "${Constants.BASE_URL}/equipos"

        val request = JsonObjectRequest(
            Request.Method.PUT, url, Equipo().toJson(equipo),

            Response.Listener<JSONObject> { response ->

                Log.i("NuevoEquipoActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("NuevoEquipoActivity", response.toString())
                callback()
            },
            Response.ErrorListener {
                Log.i("NuevoEquipoActivity", "[DEBUG]:Communication with API Rest Failed")
                handleErrorPostEquipo(context, it, callbackError)
            })
        request.retryPolicy = longPolicy

        queue.add(request)
    }

    fun eliminarEquipo(context: Context, equipo: Equipo, callback: () -> Unit){
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/equipos/"

        val request = JsonObjectRequest(
            Request.Method.DELETE, url + equipo.idEquipo, Equipo().toJson(equipo),

            Response.Listener<JSONObject> { response ->

                Log.i("EquipoFragment", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("EquipoFragment", response.toString())
                callback()
            },
            Response.ErrorListener {
                Log.i("EquipoFragment", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErroresDelete)
            })
        request.retryPolicy = longPolicy

        queue.add(request)
    }

    fun abandonarEquipo(context: Context, equipo: Equipo, usuario: Usuario, callback: () -> Unit){
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/equipo/${equipo.idEquipo}/usuario/${usuario.idUsuario}"

        Log.i("EquipoFragment", url)
        val request = JsonObjectRequest(
            Request.Method.DELETE, url, null,

            Response.Listener<JSONObject> { response ->

                Log.i("EquipoFragment", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("EquipoFragment", response.toString())
                callback()
            },
            Response.ErrorListener {
                Log.i("EquipoFragment", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)
    }

    fun lambdaManejoErrores(context: Context, statusCode: Int) {
        Log.i("EquipoFragment", statusCode.toString())
        if (statusCode == 404){
            Toasty.error(context, "No podes abandonar un equipo con partidos pendientes", Toast.LENGTH_SHORT).show()
        }
    }

    fun lambdaManejoErroresDelete(context: Context, statusCode: Int){
        Log.i("EquipoFragment", statusCode.toString())
        if (statusCode == 404){
            Toasty.error(context, "No podes eliminar un equipo con partidos pendientes", Toast.LENGTH_SHORT).show()
        }
    }


}
