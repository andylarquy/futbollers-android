package ar.edu.unsam.proyecto.futbollers.activities.drawer


import android.app.Activity
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.util_drawer_header.view.*

//DRAWER

class SetupDrawer : NavigationView.OnNavigationItemSelectedListener {

val usuarioLogueado = UsuarioLogueado.usuario

    fun startSetup(activity: Activity, toolbar: Toolbar, drawerLayout: DrawerLayout, nav_drawer: NavigationView) {



        nav_drawer.setNavigationItemSelectedListener(this)

        val drawerToggle = ActionBarDrawerToggle(
            activity,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()


        nav_drawer.getHeaderView(0).username.text = usuarioLogueado.nombre
        Picasso.get().load(usuarioLogueado.foto).into(nav_drawer.getHeaderView(0).foto_perfil)

        nav_drawer.bringToFront()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.i("BaseActivity", "Mira, ataje un evento. Tragalacteo")
        //TODO: Atajar eventos

        return true
    }

}