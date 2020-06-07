package ar.edu.unsam.proyecto.futbollers.services.auxiliar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.*
import ar.edu.unsam.proyecto.futbollers.activities.home.HomeActivity
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.services.EquipoService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.INTERVALO_GPS
import im.delight.android.location.SimpleLocation


class WorkerGPS(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    val context = context

    override fun doWork(): Result {

        Log.d("PWLOG", "Let me sleep a moment...");
        //Thread.sleep(60000 * 5);//5 minutes cycle
        Thread.sleep(5000)
        doTheActualProcessingWork()

        val gpsRequest = OneTimeWorkRequest.Builder(WorkerGPS::class.java).build()
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
        EquipoService.getEquiposDelUsuario(context, UsuarioLogueado.usuario, ::callbackFalopa)
        Log.i("HomeActivity",location.latitude.toString())

    }

    fun callbackFalopa(equiposFalopa: MutableList<Equipo>) {

    }


}