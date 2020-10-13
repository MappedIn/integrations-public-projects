package com.example.indooratlasdemo

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mappedin.Mappedin
import com.mappedin.MiGestureType
import com.mappedin.MiMapViewListener
import com.mappedin.enums.MiMapStatus
import com.mappedin.interfaces.MiMapViewCallback
import com.mappedin.interfaces.VenueCallback
import com.mappedin.models.MiLevel
import com.mappedin.models.MiOverlay
import com.mappedin.models.MiSpace
import kotlinx.android.synthetic.main.activity_main.*

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.mapbox.mapboxsdk.location.modes.CameraMode

class MainActivity : AppCompatActivity() , IALocationListener {
    /**
     * The Indoor Atlas Location Manager:
     *
     * NOTE: you'll need to add an Indoor Atlas key and secret to the AndroidManifest.xml to use this
     * and you'll need to be physically at the venue to see your position updates.
     *
     * See https://www.indooratlas.com/ for details on how to set things up with Indoor Atlas.
    */
    lateinit var mIALocationManager: IALocationManager

    // A flag to show the blue dot moving when you are not at the venue
    var fakeUserPositionWithTap = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize the Mappedin singleton with the application and credentials
        Mappedin.init(application) // <- add a Mapbox token here as the second parameter if you have one
        Mappedin.setCredentials(
                "5f4e59bb91b055001a68e9d9",
                "gmwQbwuNv7cvDYggcYl4cMa5c7n0vh4vqNQEkoyLRuJ4vU42") // <- sample keys with access to a few venues

        setContentView(R.layout.activity_main)
        mIALocationManager = IALocationManager.create(this)
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), this)

        Mappedin.getVenue("mappedin-demo-office", VenueCallback { miVenue ->
            mapView.loadMap(miVenue, MiMapViewCallback { miMapStatus ->
                if (miMapStatus == MiMapStatus.LOADED) {
                    Log.i("MiMapView", "Map has loaded")
                    mapView.startTrackingUserPosition(false) // <- set to true if using default Android Location Service (GPS)
                    mapView.mapboxMap.locationComponent.cameraMode = CameraMode.TRACKING
                } else {
                    Log.e("MiMapView", "Map failed to load")
                }
            }, null, "" ) // <- add a Mapbox styleUrl here if you have one to
        })

        //Set an MiMapViewListener to run custom code on certain map events
        mapView.setListener(object : MiMapViewListener {
            override fun onTapNothing() {
                //Called when a point on the map is tapped that isn't a MiSpace or MiOverlay
            }

            override fun didTapSpace(miSpace: MiSpace?): Boolean {
                //Called when an MiSpace is tapped, return false to be called again if multiple MiSpaces were tapped
                return false
            }

            override fun onTapCoordinates(latLng: LatLng) {
                //Called when any point is tapped on the map with the LatLng coordinates

                // Moves the blue dot to the coordinates that were tapped
                if (fakeUserPositionWithTap) {
                    val location: Location = Location("")
                    location.latitude = latLng.latitude
                    location.longitude = latLng.longitude
                    mapView.updateUserPosition(location, mapView.currentLevel!!.elevation)
                    mapView.mapboxMap.locationComponent.cameraMode = CameraMode.TRACKING_COMPASS
                }
            }

            override fun didTapOverlay(miOverlay: MiOverlay): Boolean {
                //Called when an MiOverlay is tapped, return false to be called again if multiple MiOverlays were tapped
                return false
            }

            override fun onLevelChange(miLevel: MiLevel) {
                //Called when the level changes
            }

            override fun onManipulateCamera(miGestureType: MiGestureType) {
                //Called when the user pinches or pans the map
            }
        })
    }

    // Handle Indoor Atlas location events here:
    override fun onLocationChanged(IALocation: IALocation?) {
        IALocation.let { iaLocation ->
            val location = iaLocation!!.toLocation()
            // Moves the blue dot to the new location
            mapView.updateUserPosition(location, iaLocation.floorLevel)
        }
    }

    // Handle Indoor Altas status changes here:
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        when (status) {
            IALocationManager.STATUS_AVAILABLE -> Log.d("onStatusChanged", "Available");
            IALocationManager.STATUS_LIMITED -> Log.d("onStatusChanged", "Limited");
            IALocationManager.STATUS_OUT_OF_SERVICE -> Log.d("onStatusChanged", "Out of service");
            IALocationManager.STATUS_TEMPORARILY_UNAVAILABLE -> Log.d("onStatusChanged", "Temporarily unavailable");
        }
    }
}