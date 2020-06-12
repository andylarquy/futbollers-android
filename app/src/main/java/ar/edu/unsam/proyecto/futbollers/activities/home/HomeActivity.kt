package ar.edu.unsam.proyecto.futbollers.activities.home

//import br.com.safety.locationlistenerhelper.core.LocationTracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.drawer.DrawerActivity
import ar.edu.unsam.proyecto.futbollers.activities.home.fragments.ChatFragment
import ar.edu.unsam.proyecto.futbollers.activities.home.fragments.EquipoFragment.EquipoFragment
import ar.edu.unsam.proyecto.futbollers.activities.home.fragments.PartidoFragment.PartidoFragment
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.WorkerGPS
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.util_drawer_header.view.*


class HomeActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener {

    var location: SimpleLocation? = null
    val usuarioLogueado: Usuario = UsuarioLogueado.usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* ESTA COPIADO Y PEGADO EN OnRestart POR OBLIGACION */
        //layoutInflater.inflate(R.layout.activity_home, base_drawer_layout, true)
        setContentView(R.layout.activity_home)

        setInitialFragment()

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null;
            if (item.itemId == R.id.action_partido) {
                fragment = PartidoFragment()
            } else if (item.itemId == R.id.action_equipo) {
                fragment = EquipoFragment()
            } else if (item.itemId == R.id.action_chat) {
                fragment = ChatFragment()
            }

            replaceFragment(fragment!!);

            true
        }
        /* ESTA COPIADO Y PEGADO EN OnRestart POR OBLIGACION */

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

        val data = Data.Builder().putLong("idUsuario", usuarioLogueado.idUsuario!!).build()

        val gpsRequest = OneTimeWorkRequest.Builder(WorkerGPS::class.java)
            .setInputData(data)
            .build()
        WorkManager.getInstance().enqueueUniqueWork("GPS", ExistingWorkPolicy.REPLACE, gpsRequest)

        WorkManager.getInstance().cancelAllWorkByTag("GPS")


        //DRAWER

        val toolbar = base_toolbar

        setSupportActionBar(toolbar)


        val drawerLayout = base_drawer_layout
        val drawer =  nav_drawer
        drawer.setNavigationItemSelectedListener(this)

        val drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer)

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()


        nav_drawer.getHeaderView(0).username.text = usuarioLogueado.nombre
        Picasso.get().load(usuarioLogueado.foto).into(nav_drawer.getHeaderView(0).foto_perfil)

        drawer.bringToFront()

        //DRAWER

    }

override fun onNavigationItemSelected(item: MenuItem): Boolean {
    Log.i("BaseActivity", "ASDHAKSLDJHAJKSd")
    //TODO: Atajar eventos

    return true
}

    private fun setInitialFragment() {
        val fragmentTransaction: FragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.drawer_fragment_container,
            PartidoFragment()
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

    override fun onRestart() {


        val partidoFragment: PartidoFragment = supportFragmentManager.fragments[0] as PartidoFragment

        supportFragmentManager.beginTransaction()
            .detach(partidoFragment)
            .attach(partidoFragment)
            .commitAllowingStateLoss()
            //TODO: Revisar
            //https://medium.com/@elye.project/handling-illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-d4ee8b630066

        super.onRestart()

    }


}

