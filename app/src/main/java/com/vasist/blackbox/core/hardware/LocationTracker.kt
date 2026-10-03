package com.vasist.blackbox.core.hardware

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationTracker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @SuppressLint("MissingPermission")
    fun observeDistanceMoved(): Flow<Float> = callbackFlow {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            trySend(0f)
            close()
            return@callbackFlow
        }

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var startLocation: Location? = null
        var totalDistance = 0f

        val listener = LocationListener { location ->
            val start = startLocation
            if (start == null) {
                startLocation = location
            } else {
                totalDistance = start.distanceTo(location)
            }
            trySend(totalDistance)
        }

        val provider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> null
        }

        if (provider != null) {
            try {
                locationManager.requestLocationUpdates(
                    provider,
                    1000L,
                    1f,
                    listener,
                    Looper.getMainLooper()
                )
            } catch (_: Exception) {
                trySend(0f)
            }
        } else {
            trySend(0f)
        }

        awaitClose {
            try {
                locationManager.removeUpdates(listener)
            } catch (_: Exception) {}
        }
    }
}
