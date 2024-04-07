package com.example.localizacionymapas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.localizacionymapas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bindingMenu: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMenu = ActivityMainBinding.inflate(layoutInflater)
        val vista = bindingMenu.root
        setContentView(vista)

        bindingMenu.botonUbicacion.setOnClickListener {
            val intentUbicacion = Intent(this, Localizacion::class.java)
            startActivity(intentUbicacion)
        }

        bindingMenu.botonMapa.setOnClickListener {
            val intentMapa = Intent(this, MapaActivity::class.java)
            startActivity(intentMapa)
        }
    }
}