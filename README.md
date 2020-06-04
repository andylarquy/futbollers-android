# Futbollers Android


## API REST de prueba
https://my-json-server.typicode.com/andylarquy/mock-rest-api-futbollers

## CheatSheets felices de Kotlin
![alt-text](https://miro.medium.com/max/2000/1*6g2SlQkOWnR1JSekh5NCFQ.png)

![alt-text](https://miro.medium.com/max/2000/1*1dgYmYdEZ61jYW0VdLrXRA.png)

![alt-text](https://miro.medium.com/max/2000/1*QSy_gNHyhHNpcGDmR1II-A.png)

https://devhints.io/kotlin

## Indio que explica Android
https://www.youtube.com/watch?v=CsxpHOQKk8c&list=PLlxmoA0rQ-Lw5k_QCqVl3rsoJOnb_00UV&index=3&t=0s

## Data Binding (libreria con la que se puede bindear)

https://medium.com/@Estequiel/c%C3%B3mo-utilizar-data-binding-en-android-bb06e644bea7

## Para two-way databinding:
Two-way es que si lo cambias en la vista cambia el dominio y si cambias el dominio cambia la vista.
Por defecto el bindeo es one-way, cambia en la vista y cambia en el dominio.
Para que sea two way chusmea esto (O copialo de la clase de dominio Usuario):

https://developer.android.com/topic/libraries/data-binding/observability

Si la clase de dominio que tiene two-way ademas tiene que parsearse a JSON usar la segunda opcion de ese link (@get:Bindable)


## Equivalente a println
```
Fachero:
Toast.makeText(this@LoginActivity, "Mensaje", Toast.LENGTH_SHORT).show()

No tan fachero:
Log.i("LoginActivity","Mensaje")
```

## Hilo de stackoverflow sobre GPS para ir mirando
https://stackoverflow.com/questions/17290246/get-users-current-location-using-gps

## Biblioteca espectacular para ventanas modales
https://github.com/afollestad/material-dialogs

## Atajar un evento de un spinner en Material
(Recomiendo no usar spinners de material puro, son malisimos)

```
miSpinner.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,after: Int) {}
            override fun afterTextChanged(s: Editable) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                datoSeleccionado = s.toString()
                //Y de string castealo a lo que haga falta
            }
        })
```