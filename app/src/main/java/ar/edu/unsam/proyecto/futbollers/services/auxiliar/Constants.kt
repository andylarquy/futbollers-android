package ar.edu.unsam.proyecto.futbollers.services.auxiliar

import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object Constants {
     const val BASE_URL_MOCK = "https://my-json-server.typicode.com/andylarquy/mock-rest-api-futbollers"

     //IP DE PRODUCCION
     //const val BASE_URL = "https://rest-api-futbollers-heroku.herokuapp.com"

     //DEBUG
     //const val BASE_URL = "http://192.168.0.28:9000"

     //DEBUG 2
     const val BASE_URL = "http://192.168.0.63:9000"

     //IP PRODUCCION CASA
     //const val BASE_URL = "http://190.16.251.41:9000"

     val POSICIONES = arrayOf("Arquero","Defensor","Mediocampista","Delantero")
     val DISTANCIAS = arrayOf("3","5","10","20")
     val SEXO = arrayOf("Masculino", "Femenino", "Mixto")
     const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm"

     const val INTERVALO_GPS: Long = 5000

     val simpleDateFormatter =  SimpleDateFormat(DATE_FORMAT)


     val defaultPolicy = DefaultRetryPolicy(300, 3, 1F)
     val mediumPolicy = DefaultRetryPolicy(400, 3, 1F)
     val longPolicy = DefaultRetryPolicy(500, 3, 1F)
     val partidoPolicy = DefaultRetryPolicy(8000, 0, 1F)


}