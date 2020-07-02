package ar.edu.unsam.proyecto.futbollers.activities.drawer


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.home.HomeActivity
import ar.edu.unsam.proyecto.futbollers.activities.inicio.SignUpActivity
import ar.edu.unsam.proyecto.futbollers.activities.periferico.AmigosActivity
import ar.edu.unsam.proyecto.futbollers.activities.periferico.CandidatosActivity
import ar.edu.unsam.proyecto.futbollers.activities.periferico.EncuestasActivity
import ar.edu.unsam.proyecto.futbollers.activities.periferico.InvitacionesActivity
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.util_drawer_header.view.*

//DRAWER

class SetupDrawer : NavigationView.OnNavigationItemSelectedListener {

    val usuarioLogueado = UsuarioLogueado.usuario
    lateinit var context: Context
    lateinit var activity: Activity

    fun startSetup(
        appContext: Context,
        appActivity: Activity,
        toolbar: Toolbar,
        drawerLayout: DrawerLayout,
        nav_drawer: NavigationView
    ) {

        context = appContext
        activity = appActivity

        nav_drawer.setNavigationItemSelectedListener(this)

        val drawerToggle = ActionBarDrawerToggle(
            appActivity,
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

        //TODO: No redigir al usuario si hace click en la pantalla en la q esta
        when (item.title) {
            "Perfil" -> Toast.makeText(activity, "TODO: Intent Perfil", Toast.LENGTH_SHORT).show()

            "Home" -> {
                val intent = Intent(context, HomeActivity::class.java).apply{}
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                activity.finish()
            }

            "Amigos" -> {
                val intent = Intent(context, AmigosActivity::class.java).apply{}
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                activity.finish()
            }

            "Encuestas" -> {
                val intent = Intent(context, EncuestasActivity::class.java).apply{}
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                activity.finish()
            }

            "Invitaciones" -> {
                val intent = Intent(context, InvitacionesActivity::class.java).apply{}
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                activity.finish()
            }

            "Configuracion" -> Toast.makeText(activity, "TODO: Intent Configuracion", Toast.LENGTH_SHORT).show()
        }

        return true
    }

}