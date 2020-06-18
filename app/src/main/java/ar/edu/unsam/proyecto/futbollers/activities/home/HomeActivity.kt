package ar.edu.unsam.proyecto.futbollers.activities.home

//import br.com.safety.locationlistenerhelper.core.LocationTracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation.findNavController
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.drawer.SetupDrawer
import ar.edu.unsam.proyecto.futbollers.activities.home.fragments.ChatFragment
import ar.edu.unsam.proyecto.futbollers.activities.home.fragments.EquipoFragment.EquipoFragment
import ar.edu.unsam.proyecto.futbollers.activities.home.fragments.PartidoFragment.PartidoFragment
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.WorkerGPS
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    var location: SimpleLocation? = null
    val usuarioLogueado: Usuario = UsuarioLogueado.usuario
    //Parche para atajar los eventos del boton en la activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* ESTA COPIADO Y PEGADO EN OnRestart POR OBLIGACION */
        //layoutInflater.inflate(R.layout.activity_home, base_drawer_layout, true)
        setContentView(R.layout.activity_home)

        setInitialFragment()

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null
            if (item.itemId == R.id.action_partido) {
                fragment =
                    PartidoFragment(floating_action_button.findViewById(R.id.floating_action_button))
            } else if (item.itemId == R.id.action_equipo) {
                fragment =
                    EquipoFragment(floating_action_button.findViewById(R.id.floating_action_button))
            } else if (item.itemId == R.id.action_chat) {
                fragment = ChatFragment()
            }

            replaceFragment(fragment!!)

            true
        }
        /* ESTA COPIADO Y PEGADO EN OnRestart POR OBLIGACION */

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {


            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            )

            // Permission is not granted
        }

        // construct a new instance of SimpleLocation
        location = SimpleLocation(this, false, false, 5000)

        // if we can't access the location yet
        if (!location!!.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this)
        }

        val data = Data.Builder().putLong("idUsuario", usuarioLogueado.idUsuario!!).build()

        val gpsRequest = OneTimeWorkRequest.Builder(WorkerGPS::class.java)
            .setInputData(data)
            .build()
        WorkManager.getInstance().enqueueUniqueWork("GPS", ExistingWorkPolicy.REPLACE, gpsRequest)
        WorkManager.getInstance().cancelAllWorkByTag("GPS")

        val setupDrawer = SetupDrawer()

        val toolbar = base_toolbar
        val drawerLayout = base_drawer_layout

        setSupportActionBar(toolbar)
        setupDrawer.startSetup(applicationContext, this, toolbar, drawerLayout, nav_drawer)

    }


    fun setInitialFragment() {
        val fragmentTransaction: FragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.add(
            R.id.drawer_fragment_container,
            PartidoFragment(floating_action_button.findViewById(R.id.floating_action_button))
        )

        //TODO: Revisar
        //https://medium.com/@elye.project/handling-illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-d4ee8b630066
        fragmentTransaction.commitAllowingStateLoss()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.drawer_fragment_container, fragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {

        Log.i("HomeActivity", supportFragmentManager.fragments[0].javaClass.simpleName)
        val fragment =
            PartidoFragment(floating_action_button.findViewById(R.id.floating_action_button))

        when (supportFragmentManager.fragments[0].javaClass.simpleName) {
            "EquipoFragment" -> {
                bottom_navigation.selectedItemId = R.id.action_partido
                onRestart()
            }

            "ChatFragment" -> {
                bottom_navigation.selectedItemId = R.id.action_partido
                onRestart()
            }

            else -> finish()
        }
    }

    override fun onRestart() {
        var currentFragment: Fragment
        try {
            currentFragment = supportFragmentManager.fragments[0] as PartidoFragment
            super.onRestart()
        } catch (error: Throwable) {
            try {
                currentFragment = supportFragmentManager.fragments[0] as EquipoFragment
                super.onRestart()
            } catch (error: Throwable) {
                currentFragment = supportFragmentManager.fragments[0] as ChatFragment
                supportFragmentManager.beginTransaction()
                    .detach(currentFragment)
                    .attach(currentFragment)
                    .commitAllowingStateLoss()
                //TODO: Revisar
                //https://medium.com/@elye.project/handling-illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-d4ee8b630066

                super.onRestart()
            }
        }


    }


}

