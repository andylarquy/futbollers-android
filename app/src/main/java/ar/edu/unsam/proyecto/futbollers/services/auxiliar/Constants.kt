package ar.edu.unsam.proyecto.futbollers.services.auxiliar

import java.text.SimpleDateFormat

object Constants {
     const val BASE_URL_MOCK = "https://my-json-server.typicode.com/andylarquy/mock-rest-api-futbollers"

     //IP DE PRODUCCION
     const val BASE_URL = "https://rest-api-futbollers-heroku.herokuapp.com"

     val POSICIONES = arrayOf("Arquero","Defensor","Mediocampista","Delantero")

     val simpleDateFormatter =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
}