package ar.edu.unsam.proyecto.futbollers.services.auxiliar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.services.EquipoService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.INTERVALO_GPS
import im.delight.android.location.SimpleLocation

class WorkerGPS(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    val context = context

    override fun doWork(): Result {



            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
            }

            val location = SimpleLocation(context, false, false, INTERVALO_GPS)
            EquipoService.getEquiposDelUsuario(context, UsuarioLogueado.usuario, ::callbackFalopa)

            return Result.retry()

    }

    fun callbackFalopa(equiposFalopa: MutableList<Equipo>){

    }



}