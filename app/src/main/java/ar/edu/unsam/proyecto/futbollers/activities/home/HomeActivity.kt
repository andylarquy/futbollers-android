package ar.edu.unsam.proyecto.futbollers.activities.home

//import br.com.safety.locationlistenerhelper.core.LocationTracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.work.*
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.home.fragments.ChatFragment
import ar.edu.unsam.proyecto.futbollers.activities.home.fragments.EquipoFragment.EquipoFragment
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.WorkerGPS
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    var location: SimpleLocation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        setupActivity()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION ), 1)

            // Permission is not granted
        }

        // construct a new instance of SimpleLocation
        location = SimpleLocation(this, false, false, 5000)

        // if we can't access the location yet
        if (!location!!.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this)
        }



        val data = Data.Builder().putLong("idUsuario", UsuarioLogueado.usuario.idUsuario!!).build()

        val gpsRequest = OneTimeWorkRequest.Builder(WorkerGPS::class.java)
            .setInputData(data)
            .build()
        WorkManager.getInstance().enqueueUniqueWork("GPS", ExistingWorkPolicy.REPLACE, gpsRequest)

        WorkManager.getInstance().cancelAllWorkByTag("GPS")

    }


    override fun onRestart() {
        super.onRestart()
        setupActivity()
    }


    private fun setInitialFragment() {
        val fragmentTransaction: FragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.add(
            R.id.main_fragment_placeholder,
            ar.edu.unsam.proyecto.futbollers.activities.home.fragments.PartidoFragment.EquipoFragment()
        )

        //TODO: Revisar
        //https://medium.com/@elye.project/handling-illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-d4ee8b630066
        fragmentTransaction.commitAllowingStateLoss()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment_placeholder, fragment)
        fragmentTransaction.commit()
    }

    fun setupActivity() {
        setInitialFragment()

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null;
            if (item.itemId == R.id.action_partido) {
                fragment =
                    ar.edu.unsam.proyecto.futbollers.activities.home.fragments.PartidoFragment.EquipoFragment()
            } else if (item.itemId == R.id.action_equipo) {
                fragment = EquipoFragment()
            } else if (item.itemId == R.id.action_chat) {
                fragment = ChatFragment()
            }

            replaceFragment(fragment!!);

            true
        }
    }




    override fun onResume() {
        super.onResume()
        setupActivity()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION ), 1)

            // Permission is not granted
        }

        // make the device update its location
        location!!.beginUpdates()

        // ...
    }




/*
    fun getLocation(): LatLng? {
        // Get the location manager
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val bestProvider = locationManager.getBestProvider(criteria, false)
        val location: Location = locationManager.getLastKnownLocation(bestProvider)
        val lat: Double
        val lon: Double
        return try {
            lat = location.getLatitude()
            lon = location.getLongitude()
            Log.i("HomeActivity", "Lat: $lat Lon: $lon")
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
*/
}

