package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.IMAGE_SERVER_BASE_URL
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.IMAGE_SERVER_KEY
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.defaultPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import uk.me.hardill.volley.multipart.MultipartRequest
import java.io.ByteArrayOutputStream


object AuxiliarService {

    fun uploadImage(context: Context, image: Bitmap, callback: (String) -> Unit) {

        val queue = Volley.newRequestQueue(context)

        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        val fotoEnBase64: String = encodeToString(byteArray, DEFAULT)

        // Ahora tenemos el Bitmap en Base64 dentro de la variable fotoEnBase64

        val url = IMAGE_SERVER_BASE_URL + IMAGE_SERVER_KEY

        val request = MultipartRequest(url, null,
            Response.Listener<NetworkResponse>() {response ->

                Log.i("NuevoEquipoActivity", "[DEBUG]:Communication with API Rest Suceeded")

                val jsonResponse = JSONObject(String(response.data))
                val imageUrl = jsonResponse.getJSONObject("data").getString("url")

                Log.i("NuevoEquipoActivity", jsonResponse.toString())

                callback(imageUrl)

            },
            Response.ErrorListener() {
                Log.i("NuevoEquipoActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })

        request.addPart(MultipartRequest.FormPart("image",fotoEnBase64))

        request.retryPolicy = defaultPolicy

        queue.add(request)

    }

    fun lambdaManejoErrores(context: Context, statusCode: Int){

    }

}


