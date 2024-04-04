package com.example.localizacionymapas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.localizacionymapas.databinding.ActivityLocalizacionBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.math.roundToInt

class Localizacion : AppCompatActivity() {

    private lateinit var bindingMapa: ActivityLocalizacionBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    val RADIUS_OF_EARTH_KM = 6371.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMapa = ActivityLocalizacionBinding.inflate(layoutInflater)
        val vista = bindingMapa.root
        setContentView(vista)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = createLocationRequest()

        /*mFusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            Log.i("LOCATION", "onSuccess location")
            if (location != null) {
                Log.i("LOCATION", "Longitud: " + location.longitude)
                Log.i("LOCATION", "Latitud: " + location.latitude)
                bindingMapa.longitudRespuesta.text = location.longitude.toString()
                bindingMapa.latitudRespuesta.text = location.latitude.toString()
                bindingMapa.elevacionRespuesta.text = location.altitude.toString()
            } else {
                Log.i("LOCATION", "La ubicación es nula")
                Toast.makeText(this, "La ubicación es nula", Toast.LENGTH_SHORT).show()
            }*/

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                Log.i("LOCATION", "Actualización")
                if(location!=null) {
                    bindingMapa.longitudRespuesta.text = location.longitude.toString()
                    bindingMapa.latitudRespuesta.text = location.latitude.toString()
                    bindingMapa.elevacionRespuesta.text = location.altitude.toString()
                    bindingMapa.distanciaRespuesta.text = distance(location.latitude, location.longitude, 57.0397, -3.2293).toString()
                    Log.i("LOCATION", "Longitud: " + location.longitude)
                    Log.i("LOCATION", "Latitud: " + location.latitude)
                    Log.i("LOCATION", "Altitud: " + location.altitude)
                }else {
                    // La ubicación es nula
                    Log.i("LOCATION", "La ubicación es nula")
                }
            }
        }


            //Permisos
        when {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Inicia las actualizaciones de ubicación
                startLocationUpdates()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // Solicita los permisos de ubicación
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    Permisos.LOCATION_PERMISSION_CODE
                )
            }
            else -> {
                // Solicita los permisos de ubicación
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    Permisos.LOCATION_PERMISSION_CODE
                )
            }
        }
    }

    private fun createLocationRequest(): LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000).apply {
            setMinUpdateIntervalMillis(5000)
        }.build()

    private fun startLocationUpdates(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        }
    }

    private fun distance(lati1: Double, longi1: Double, lati2: Double, longi2: Double): Double {
        val latDistance = Math.toRadians(lati1 - lati2)
        val longiDistance = Math.toRadians(longi1 - longi2)

        val a  = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + (Math.cos(Math.toRadians(lati1)) * Math.cos(Math.toRadians(lati2))
                * Math.sin(longiDistance / 2) * Math.sin(longiDistance / 2)))

        val c = 2* Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
        val resultado = RADIUS_OF_EARTH_KM * c

        return (resultado*100.0).roundToInt()/100.0
    }

    override fun onPause() {
        super.onPause()
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Permisos.LOCATION_PERMISSION_CODE -> {
                //If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Invita a tus amigos a tomar cerveza de mantequilla en cualquier parte del mundo", Toast.LENGTH_SHORT).show()
                    startLocationUpdates()
                } else {
                    Toast.makeText(this, "No puedes invitar a tus amigos a tomar cerveza de mantequilla", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}