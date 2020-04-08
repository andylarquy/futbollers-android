package ar.edu.unsam.proyecto.futbollers.services

import ar.edu.unsam.proyecto.futbollers.domain.Usuario

import android.content.Context
import android.util.Log
import ar.edu.unsam.proyecto.futbollers.services.Constants.BASE_URL

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


object LoginService {

   fun getUsuarioLogueado(context: Context, usuario: Usuario, callback: () -> Unit) {


      val queue = Volley.newRequestQueue(context)
      val url = "$BASE_URL/usuarioLogueado"

      val stringRequest = StringRequest(
         Request.Method.GET, url,
         Response.Listener<String> { response ->
            Log.i("MainActivity", response.toString().javaClass.name)
            val nuevoUsuario = Usuario().fromJson(response)
             usuario.createCopy(nuevoUsuario)
             Log.i("MainActivity", nuevoUsuario.toString())
             callback()
         },
         Response.ErrorListener { Log.i("MainActivity", "That didn't work!") })

      queue.add(stringRequest)
   }
   }



