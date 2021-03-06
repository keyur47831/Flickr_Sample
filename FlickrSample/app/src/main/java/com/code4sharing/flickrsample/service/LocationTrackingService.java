package com.code4sharing.flickrsample.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.code4sharing.flickrsample.activity.AppController;
import com.code4sharing.flickrsample.util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by keyur on 22-08-2015.
 * This class creates our Location Service.
 * This service implements the FusedLocationAPI
 */
public class LocationTrackingService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;


    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    //Interface for callback
    NotifyCurrentLocationChanged mNotifyCurrentLocationChanged;
    //for logs
    private static final String TAG = LocationTrackingService.class.getSimpleName ();

    /**
     * Constructur for location service
     * @param PresenterCallBack callback interface for notification
     */
    public LocationTrackingService (NotifyCurrentLocationChanged PresenterCallBack) {
        this.mNotifyCurrentLocationChanged = PresenterCallBack;
    }

    /**
     * This function will build
     * Googleapi client
     */
    public synchronized void buildGoogleApiClient () {
        mGoogleApiClient = new GoogleApiClient.Builder (AppController.getInstance ())
                .addConnectionCallbacks (this)
                .addOnConnectionFailedListener (this)
                .addApi (LocationServices.API).build ();
        createLocationRequest ();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest () {
        mLocationRequest = new LocationRequest ();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval (Constants.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval (Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority (LocationRequest.PRIORITY_HIGH_ACCURACY);
        connectFusedLocationProviderApi ();

    }

    /**
     * Connect method wrapper
     */
    public void connectFusedLocationProviderApi () {
        if (!mGoogleApiClient.isConnected ()) {
            mGoogleApiClient.connect ();
            return;
        }
        if (mCurrentLocation == null)
            //if current location is null
            // start listening for locaiton
           //changes
            startLocationUpdates ();

    }

    /**
     * This function returns the service status
     * @return status of service
     */
    public boolean isServiceRunning () {
        return mCurrentLocation == null ? false : true;
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates () {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        LocationServices.FusedLocationApi.requestLocationUpdates (
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    public  void stopLocationUpdates () {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates (mGoogleApiClient, this);
        mGoogleApiClient.disconnect ();
        //Ensure we always get lastest location
        mCurrentLocation=null;
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed (ConnectionResult result) {
        Log.e (TAG, "Connection failed: Error ="
                + result.getErrorCode ());
        mNotifyCurrentLocationChanged.onLocationFail ();
    }

    @Override
    public void onConnected (Bundle arg0) {

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation (mGoogleApiClient);
            if (mCurrentLocation != null) {
                mNotifyCurrentLocationChanged.onLatLngUpdated (mCurrentLocation);
                startLocationUpdates ();
                Log.e (TAG, "Connection success ="
                        + mCurrentLocation.getLongitude () + ":" + mCurrentLocation.getLatitude ());
            } else
                mNotifyCurrentLocationChanged.onLocationFail ();
        }

    }

    @Override
    public void onConnectionSuspended (int arg0) {
        mGoogleApiClient.connect ();
    }

    @Override
    public IBinder onBind (Intent arg0) {
        return null;
    }

    @Override
    public void onLocationChanged (Location location) {
        mCurrentLocation = location;
        mNotifyCurrentLocationChanged.onLatLngUpdated (mCurrentLocation);

    }

    /**\
     * Interface to notify updates
     */
    public interface NotifyCurrentLocationChanged {
         void onLatLngUpdated (Location newLocation);
         void onLocationFail ();
    }
}
