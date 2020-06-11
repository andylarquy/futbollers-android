package ar.edu.unsam.proyecto.futbollers.activities.drawer

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.util_drawer.*
import kotlinx.android.synthetic.main.util_drawer_header.view.*


open class ToolbarActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.util_toolbar)

        val toolbar = base_toolbar

        setSupportActionBar(toolbar)
    }
}

open class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val usuarioLogueado: Usuario = UsuarioLogueado.usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.util_drawer)

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

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.i("BaseActivity", "ASDHAKSLDJHAJKSd")
        //TODO: Atajar eventos

        return true
    }


}

