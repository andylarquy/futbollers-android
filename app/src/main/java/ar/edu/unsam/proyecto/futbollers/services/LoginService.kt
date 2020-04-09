package ar.edu.unsam.proyecto.futbollers.services

import ar.edu.unsam.proyecto.futbollers.domain.Usuario

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.services.Constants.BASE_URL
import com.android.volley.*

import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


object LoginService {

   fun getUsuarioLogueado(context: Context, usuario: Usuario, callback: (Usuario) -> Unit) {


      val queue = Volley.newRequestQueue(context)
      val url = "$BASE_URL/usuarioLogueado"

      val request = JsonObjectRequest(
         Request.Method.POST, url, Usuario().toJson(usuario),
         Response.Listener<JSONObject> { response ->
            val nuevoUsuario = Usuario().fromJson(response.toString())
            Log.i("LoginActivity", "Respuesta de la API Rest: "+response.toString())
            callback(nuevoUsuario)
         },
         Response.ErrorListener {
            handleError(context, it)
         })
      request.retryPolicy = DefaultRetryPolicy(250,3,1F)

      queue.add(request)
   }

   fun handleError(context: Context ){
      Toast.makeText(context, "Hubo un error al conectar con el servidor, revise su conexion a internet",Toast.LENGTH_SHORT).show()
      Log.i("LoginActivity","Error al pegarle a la API REST")

   }

   fun handleError(context: Context, error: VolleyError?){
      handleError(context)
      if(error!!.message !== null) {
         Log.i("LoginActivity", error?.message)
      }
   }


   }



