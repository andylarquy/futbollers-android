package ar.edu.unsam.proyecto.futbollers.services.auxiliar

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.*

fun handleError(context: Context, error: VolleyError, lambdaManejoErrores: (Context, Int) -> Unit) {

    Log.i("HomeActivity", "[DEBUG]: API Rest Error: +" + error)
    if (error is AuthFailureError) {
        Toast.makeText(context, "Las credenciales son invalidas", Toast.LENGTH_SHORT).show()
    } else if (error is NoConnectionError) {
        Toast.makeText(context, "Revise su conexion a internet", Toast.LENGTH_SHORT).show()
    } else if (error is ClientError) {
        val networkResponse = error.networkResponse
        if (networkResponse.data != null) {
            lambdaManejoErrores(context, networkResponse.statusCode)
        }
    }
}