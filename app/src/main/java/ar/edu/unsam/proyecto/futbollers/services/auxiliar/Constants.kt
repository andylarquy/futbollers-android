package ar.edu.unsam.proyecto.futbollers.services.auxiliar

import com.android.volley.DefaultRetryPolicy
import java.text.SimpleDateFormat
import java.util.*

object Constants {
     //const val BASE_URL_MOCK = "https://my-json-server.typicode.com/andylarquy/mock-rest-api-futbollers"

     //IP DE PRODUCCION
     //const val BASE_URL = "https://rest-api-futbollers-heroku.herokuapp.com"

     //DEBUG
     const val BASE_URL = "http://192.168.0.28:9000"

     //DEBUG 2
     //const val BASE_URL = "http://192.168.100.5:9000"

     //IP PRODUCCION CASA
     // const val BASE_URL = "http://190.16.251.41:9000"

     val POSICIONES = arrayOf("Arquero", "Defensor", "Mediocampista", "Delantero", "Cualquiera")
     val DISTANCIAS = arrayOf("3","5","10","20")
     val SEXO = arrayOf("Masculino", "Femenino", "Mixto")
     const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm"
     const val OUTPUT_DATE_FORMAT = "dd/MM/yyyy - HH:mm"

     const val INTERVALO_GPS: Long = 5000

     val simpleDateFormatter =  SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
     val outputDateFormatter = SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.getDefault())

     fun toCalendar(date: Date): Calendar {
          val cal = Calendar.getInstance()
          cal.time = date
          return cal
     }

     val defaultPolicy = DefaultRetryPolicy(300, 3, 1F)
     val mediumPolicy = DefaultRetryPolicy(400, 3, 1F)
     val longPolicy = DefaultRetryPolicy(500, 3, 1F)
     val partidoPolicy = DefaultRetryPolicy(8000, 0, 1F)

     const val IMAGE_SERVER_KEY = "73f024c274f96f1e4c2ea701650fa1d4"
     const val IMAGE_SERVER_BASE_URL = "https://api.imgbb.com/1/upload?key="

     fun dateTransformer(fecha: String): String{
          val fechaAsDate: Date = simpleDateFormatter.parse(fecha)!!
          return outputDateFormatter.format(fechaAsDate)
     }

     fun chatIdBuilder(idUno: Long, idDos: Long): String{
          if(idUno < idDos){
               return "$idUno-$idDos"
          }else{
               return "$idDos-$idUno"
          }
     }

}