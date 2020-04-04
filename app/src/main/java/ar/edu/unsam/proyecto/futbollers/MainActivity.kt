package ar.edu.unsam.proyecto.futbollers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_login.setOnClickListener({
            Toast.makeText(this@MainActivity, "Hiciste Click! :)", Toast.LENGTH_SHORT).show()
        })
    }
}
