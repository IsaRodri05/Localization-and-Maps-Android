package com.example.localizacionymapas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.localizacionymapas.databinding.ActivityMapaBinding
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import java.io.IOException

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap ?= null
    private lateinit var bindingMapa: ActivityMapaBinding
    private lateinit var sensorManejador: SensorManager
    private lateinit var sensorLuz: Sensor
    private lateinit var sensorLuzListener: SensorEventListener
    private lateinit var dirGeocoder: Geocoder

    // Limits for the geocoder search (United Kingdom
    companion object {
        const val lowerLeftLatitude = 49.9167
        const val lowerLeftLongitude = -8.4789
        const val upperRightLatitude = 60.860699
        const val upperRightLongitude = 1.76334
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingMapa = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(bindingMapa.root)

        sensorManejador = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorLuz = sensorManejador.getDefaultSensor(Sensor.TYPE_LIGHT)!!

        sensorLuzListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (mMap != null) {
                    if (event!!.values[0] < 5000) {
                        Log.i("MAPS", "DARK MAP" + event.values[0])
                        mMap!!.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                baseContext,
                                R.raw.style_maps_aubergine
                            )
                        )
                    } else {
                        Log.i("MAPS", "LIGHT MAP" + event.values[0])
                        mMap!!.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                baseContext,
                                R.raw.style_maps_retro
                            )
                        )
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Do something here if sensor accuracy changes.
            }
        }

        dirGeocoder = Geocoder(baseContext)

        bindingMapa.edTDireccion.setOnEditorActionListener { v, actionID, event ->
            if (actionID == EditorInfo.IME_ACTION_SEND) {
                val direccionTexto = bindingMapa.edTDireccion.text.toString()
                if (!!direccionTexto.isEmpty()) {
                    try {
                        val direccion = dirGeocoder.getFromLocationName(direccionTexto, 2)
                        if (direccion != null) {
                            if (direccion.isNotEmpty() && direccion != null) {
                                val dirResultado = direccion[0]
                                val posicion = LatLng(dirResultado.latitude, dirResultado.longitude)
                                if (mMap != null) {
                                    mMap!!.addMarker(
                                        MarkerOptions().position(posicion).title(direccionTexto)
                                    )
                                    mMap!!.moveCamera(CameraUpdateFactory.newLatLng(posicion))
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Faiamos, no encontramos otro castillo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "Faiamos, la direccion está vacía", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //processing file SVG
    private fun bitmapDescriptorFromVector(context: Context, vectorResID: Int): BitmapDescriptor?{
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResID)
        if (vectorDrawable == null) {
            Log.e("MAPS", "Resource not found: $vectorResID")
            return null
        }
        vectorDrawable?.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas =  Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onResume() {
        super.onResume()
        sensorManejador.registerListener(sensorLuzListener, sensorLuz, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManejador.unregisterListener(sensorLuzListener)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val castilloBalmoral = LatLng(57.0397, -3.2293)
        val castilloWindsor = LatLng(51.4839, -0.6044)
        val palacioBuckingham = LatLng(51.5014, -0.1419)
        val castilloEdinburgo = LatLng(55.9486, -3.1999)
        val palacioWestMinster = LatLng(51.4994, -0.1248)

        mMap = googleMap

        // Add a marker in Sydney and move the camera
        mMap!!.moveCamera(CameraUpdateFactory.zoomTo(10f))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(castilloBalmoral))

        mMap!!.addMarker(MarkerOptions().position(castilloBalmoral).title("Marker in Balmoral Castle"))
        val visualBalmoral = mMap!!.addMarker(MarkerOptions().position(castilloBalmoral).icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)))

        mMap!!.addMarker(MarkerOptions().position(castilloWindsor).title("Marker in Windsor Castle"))
        val visualWindsor = mMap!!.addMarker(MarkerOptions().position(castilloWindsor).icon(
            bitmapDescriptorFromVector(this, R.drawable.crown)))

        mMap!!.addMarker(MarkerOptions().position(palacioBuckingham).title("Marker in Buckingham Palace"))
        val visualBuckingham = mMap!!.addMarker(MarkerOptions().position(palacioBuckingham).icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))

        mMap!!.addMarker(MarkerOptions().position(castilloEdinburgo).title("Marker in Edinburgh Castle"))
        val visualEdinburgo = mMap!!.addMarker(MarkerOptions().position(castilloEdinburgo).icon(
            bitmapDescriptorFromVector(this, R.drawable.castle)))

        mMap!!.addMarker(MarkerOptions().position(palacioWestMinster).title("Marker in Westminster Palace"))
        val visualWestminster = mMap!!.addMarker(MarkerOptions().position(palacioWestMinster).icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)))

        mMap!!.uiSettings.isZoomGesturesEnabled = true
        mMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_maps_retro))

        visualBalmoral?.isVisible = true
        visualWindsor?.isVisible = true
        visualBuckingham?.isVisible = true
        visualEdinburgo?.isVisible = true
        visualWestminster?.isVisible = true

        //this is making when we have a lot of information
        //visualBalmoral?.remove()
        //visualWindsor?.remove()

    }
}