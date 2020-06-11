package ar.edu.unsam.proyecto.futbollers.activities.drawer

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import ar.edu.unsam.proyecto.futbollers.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.util_drawer.*



open class ToolbarActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.util_toolbar)

        val toolbar = base_toolbar

        setSupportActionBar(toolbar)
    }
}

open class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.util_drawer)

        val toolbar = base_toolbar

        setSupportActionBar(toolbar)


    val drawerLayout = base_drawer_layout
    val drawer = nav_drawer

    val drawerToggle = ActionBarDrawerToggle(
        this,
        drawerLayout,
        toolbar,
        R.string.open_drawer,
        R.string.close_drawer)

    drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        drawer.setNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //TODO: Atajar eventos
        return false
    }

}
