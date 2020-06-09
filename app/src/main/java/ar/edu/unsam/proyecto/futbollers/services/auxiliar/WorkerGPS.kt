package ar.edu.unsam.proyecto.futbollers.services.auxiliar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.*
import ar.edu.unsam.proyecto.futbollers.domain.Cancha
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.services.CanchaService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.INTERVALO_GPS
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import im.delight.android.location.SimpleLocation
import org.json.JSONArray
import android.content.SharedPreferences

import org.json.JSONObject
import java.util.concurrent.TimeUnit


class WorkerGPS(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    val context = context
    var idUsuario: Long? = null

    override fun doWork(): Result {
        idUsuario = inputData.getLong("idUsuario", 0)
        Log.d("PWLOG", "Let me sleep a moment...");
        Thread.sleep(60000 * 2);//2 minutes cycle
        doTheActualProcessingWork()

        val data = Data.Builder().putLong("idUsuario", idUsuario!!).build()

        val gpsRequest = OneTimeWorkRequest.Builder(WorkerGPS::class.java).setInputData(data).build()
        WorkManager.getInstance().enqueueUniqueWork("GPS", ExistingWorkPolicy.REPLACE, gpsRequest)

        return Result.success()

    }

    fun doTheActualProcessingWork() {
        Log.d("PWLOG", "Processing work...");
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
        }

        val location = SimpleLocation(context, false, false, INTERVALO_GPS)

        postUbicacion(location)

    }

    fun callbackFalopa(equiposFalopa: MutableList<Equipo>) {

    }

    fun postUbicacion(location: SimpleLocation) {

        Log.i("HomeActivity", location.latitude.toString())

        val future = RequestFuture.newFuture<JSONObject>()
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/ubicacion"

        Log.i("HomeActivity", "Ping")

        //El viejo y poco confiable parseo manual
        val body = JSONObject("{ \"idUsuario\":"+idUsuario+",\"lat\":"+location.latitude+",\"lon\":"+location.longitude+"}")

        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
            Response.Listener<JSONObject> { response ->

                Log.i("HomeActivity", response.toString())
                Log.i(
                    "HomeActivity",
                    "idUs: " + idUsuario + " lon: " + location.longitude + " lat: " + location.latitude
                )

            },
            Response.ErrorListener {})
        request.retryPolicy = Constants.defaultPolicy

        queue.add(request)

    }


}

