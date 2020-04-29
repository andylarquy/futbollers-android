package ar.edu.unsam.proyecto.futbollers.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.fragments.ChatFragment
import ar.edu.unsam.proyecto.futbollers.fragments.EquipoFragment.EquipoFragment
import ar.edu.unsam.proyecto.futbollers.fragments.PartidoFragment.PartidoFragment
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setInitialFragment()

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null;
            if (item.itemId == R.id.action_partido) {
                fragment =
                    PartidoFragment()
            } else if (item.itemId == R.id.action_equipo) {
                fragment = EquipoFragment()
            } else if (item.itemId == R.id.action_chat) {
                fragment = ChatFragment()
            }

            replaceFragment(fragment!!);

            true
        }

    }


    private fun setInitialFragment() {
        val fragmentTransaction: FragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.main_fragment_placeholder,
            PartidoFragment()
        )
        fragmentTransaction.commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment_placeholder, fragment)
        fragmentTransaction.commit()
    }



}