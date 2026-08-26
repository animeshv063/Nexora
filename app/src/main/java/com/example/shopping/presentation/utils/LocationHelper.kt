package com.example.shopping.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale

object LocationHelper {

    /**
     * Checks if GPS or Network location providers are enabled on the device.
     */
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Opens Android Location Settings so the user can turn on GPS.
     */
    fun openLocationSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Fetches current device location using FusedLocationProviderClient with LocationManager fallbacks.
     */
    @SuppressLint("MissingPermission")
    fun fetchCurrentAddress(
        context: Context,
        onAddressFound: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isLocationEnabled(context)) {
            openLocationSettings(context)
            onError("GPS is turned OFF. Please turn ON Location in settings and try again.")
            return
        }

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    getAddressFromCoordinates(context, location.latitude, location.longitude, onAddressFound, onError)
                } else {
                    // Fallback 1: Fused lastLocation
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc: Location? ->
                        if (lastLoc != null) {
                            getAddressFromCoordinates(context, lastLoc.latitude, lastLoc.longitude, onAddressFound, onError)
                        } else {
                            // Fallback 2: System LocationManager (GPS or Network provider)
                            val systemLocation = getSystemLocation(context)
                            if (systemLocation != null) {
                                getAddressFromCoordinates(context, systemLocation.latitude, systemLocation.longitude, onAddressFound, onError)
                            } else {
                                onError("Acquiring GPS fix... Please ensure location is enabled and tap again.")
                            }
                        }
                    }.addOnFailureListener {
                        val systemLocation = getSystemLocation(context)
                        if (systemLocation != null) {
                            getAddressFromCoordinates(context, systemLocation.latitude, systemLocation.longitude, onAddressFound, onError)
                        } else {
                            onError("Failed to obtain device location: ${it.localizedMessage}")
                        }
                    }
                }
            }.addOnFailureListener {
                val systemLocation = getSystemLocation(context)
                if (systemLocation != null) {
                    getAddressFromCoordinates(context, systemLocation.latitude, systemLocation.longitude, onAddressFound, onError)
                } else {
                    onError("Location fetch failed: ${it.localizedMessage}")
                }
            }
        } catch (e: Exception) {
            val systemLocation = getSystemLocation(context)
            if (systemLocation != null) {
                getAddressFromCoordinates(context, systemLocation.latitude, systemLocation.longitude, onAddressFound, onError)
            } else {
                onError("Location error: ${e.localizedMessage}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getSystemLocation(context: Context): Location? {
        return try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
            val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
            var bestLoc: Location? = null
            for (provider in providers) {
                if (lm.isProviderEnabled(provider)) {
                    val loc = lm.getLastKnownLocation(provider) ?: continue
                    if (bestLoc == null || loc.accuracy < bestLoc.accuracy) {
                        bestLoc = loc
                    }
                }
            }
            bestLoc
        } catch (e: Exception) {
            null
        }
    }

    private fun getAddressFromCoordinates(
        context: Context,
        latitude: Double,
        longitude: Double,
        onAddressFound: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val formatted = formatAddress(addresses[0])
                        onAddressFound(formatted)
                    } else {
                        onAddressFound("GPS Location (${String.format(Locale.US, "%.4f", latitude)}, ${String.format(Locale.US, "%.4f", longitude)})")
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val formatted = formatAddress(addresses[0])
                    onAddressFound(formatted)
                } else {
                    onAddressFound("GPS Location (${String.format(Locale.US, "%.4f", latitude)}, ${String.format(Locale.US, "%.4f", longitude)})")
                }
            }
        } catch (e: Exception) {
            onAddressFound("GPS Location (${String.format(Locale.US, "%.4f", latitude)}, ${String.format(Locale.US, "%.4f", longitude)})")
        }
    }

    private fun formatAddress(address: Address): String {
        val parts = mutableListOf<String>()
        val feature = address.featureName
        val thoroughfare = address.thoroughfare
        val subLocality = address.subLocality
        val locality = address.locality
        val admin = address.adminArea
        val postal = address.postalCode

        if (!feature.isNullOrBlank() && feature != thoroughfare) parts.add(feature)
        if (!thoroughfare.isNullOrBlank()) parts.add(thoroughfare)
        if (!subLocality.isNullOrBlank()) parts.add(subLocality)
        if (!locality.isNullOrBlank()) parts.add(locality)
        if (!admin.isNullOrBlank()) parts.add(admin)
        if (!postal.isNullOrBlank()) parts.add(postal)

        return if (parts.isNotEmpty()) parts.joinToString(", ") else address.getAddressLine(0) ?: "Current Location"
    }
}
