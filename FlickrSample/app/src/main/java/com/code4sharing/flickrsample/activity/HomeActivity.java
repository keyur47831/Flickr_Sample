package com.code4sharing.flickrsample.activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.code4sharing.flickrsample.R;
import com.code4sharing.flickrsample.adapter.CustomInfoWindowAdapter;
import com.code4sharing.flickrsample.model.FlickrDataModel;
import com.code4sharing.flickrsample.presenter.FlickrPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by keyur on 22-08-2015.
 * This class will act as a single UI unit for our project
 * <p>It contains Mapfragment to disply map</p>
 */
public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    //Instance of Present Class
    //any request for Activity will be
    //fulfilled by Presenter class
    FlickrPresenter mFlickrPresenter;
    //Snackbar UI element for feedback
    //to the user
    CoordinatorLayout mSnackbarLayout;
    //Other UI elements
    private Toolbar mToolbar;
    private GoogleMap mGoogleMap;
    //Current location of user
    private Location mCurrentLocation;
    //Location/marker selected on map
    private LatLng mCurrentSelection;
    //Instance of MapFragment
    MapFragment mapFragment;
    //Hashmap for all the InfoWindow
    //images
    HashMap<String, String> mMarkerUrl = new HashMap<> ();
    //List of all nearby object
    //Currently we are not using any data items
    //from the model
    List<FlickrDataModel> mNearByLocation = new ArrayList<> ();
    //Latitude and Longitude of Markers
    ArrayList<LatLng> mMarkerByLocation = new ArrayList<> ();
    //for Logging
    public static final String TAG = HomeActivity.class.getSimpleName ();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_home);
        //set the toolbar
        mToolbar = (Toolbar) findViewById (R.id.toolbar);
        setSupportActionBar (mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar ().setDisplayShowHomeEnabled (true);
        //set the map fragment
        mapFragment = (MapFragment) getFragmentManager ()
                .findFragmentById (R.id.map);
        //init presenter object
        mFlickrPresenter = new FlickrPresenter (mRequestFlickrData, mRequestLocationData);
        //UI init
        mSnackbarLayout =(CoordinatorLayout)findViewById (R.id.snackbar);
        //init the map
       // initMap ();
    }
    @Override
    public void onResume()
    {
        super.onResume ();
        if(mGoogleMap==null)
            initMap ();
    }
    @Override
    public void onPause()
    {
        super.onPause ();
        if(mFlickrPresenter.isServiceRunning ())
        {
            mFlickrPresenter.disconnectService();
        }
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();
        //noinspection SimplifiableIfStatement
        if (id == R.id.current_location) {
            //display current location of user
            updateMap ();
            return true;
        } else if (id == R.id.show_nearby) {
            //display markers of nearby location
            mFlickrPresenter.loadData ();
            return true;
        } else if (id == R.id.show_path) {
            //show poly line path
            displayPath ();
            return true;
        }
        return super.onOptionsItemSelected (item);
    }

    /**
     * This is a callback interface for FlickrData
     * Using this interface, the presenter class
     * shares the request status
     * It has two callback methods
     * 1)OnRequestSuccess : return data
     * 2)OnRequestError : for error notification
     */
    private FlickrPresenter.onFectFlickrDataRequest mRequestFlickrData = new FlickrPresenter.onFectFlickrDataRequest () {

        @Override
        public void onRequestSuccess (List<FlickrDataModel> NearByLocation) {
            mNearByLocation = NearByLocation;
            addMarkers (mNearByLocation);
        }

        @Override
        public void onRequestError () {
            //Toast.makeText (getApplicationContext (), R.string.connection_error, Toast.LENGTH_SHORT).show ();
            mSnackbarLayout.setTag(getString(R.string.connection));
            Snackbar.make (mSnackbarLayout, getString (R.string.connection_error), Snackbar.LENGTH_LONG)
                    .setActionTextColor (Color.RED)
                    .setAction(getString(R.string.setting), mOnClickListener)
                    .show();

        }
    };

    /**
     * This method will init our Map
     * We call the Async method to load map
     * This will ensure that UI thread is not blocked.
     */
    private void initMap () {
        if (mGoogleMap == null) {
          //we check if mGoogleMap
            //is already initialized or not
            mapFragment.getMapAsync (this);
        }
    }

    @Override
    public void onMapReady (GoogleMap map) {
        //on this callback our map loading is
        //finished
        mGoogleMap = map;
        if (mCurrentLocation != null) {
            //Update the current location on map
            updateMap ();
        } else
        //we will try to reconnect the service
            mFlickrPresenter.reconnetService ();
    }

    /**
     * This is callback for Map related requests.
     * This will get current location of device
     * 1) OnCurrentLocationUpdated : new location
     * 2) OnCurrentLocationFailed : request failed
     */
    private FlickrPresenter.MapRequestCallBacks mRequestLocationData = new FlickrPresenter.MapRequestCallBacks () {
        @Override
        public void onCurrentLocationUpdated (Location data) {
            mCurrentLocation = data;
            if (mNearByLocation == null)
                updateMap ();
        }

        public void onCurrenLocationFailed () {
           // Toast.makeText (getApplicationContext (), R.string.gps_error, Toast.LENGTH_SHORT).show ();
            mSnackbarLayout.setTag (getString(R.string.gps));
            Snackbar.make (mSnackbarLayout, getString (R.string.gps_error), Snackbar.LENGTH_LONG)
                    .setActionTextColor (Color.RED)
                    .setAction(getString(R.string.setting), mOnClickListener)
                    .show();
        }
    };

    /**
     * This method will update the current location of device.
     * It will place a marker for current location on map
     */
    public void updateMap () {
        //If map has any markers
        //clear it
        if (mGoogleMap != null) {
            mGoogleMap.clear ();
            //for error handling
            if (mCurrentLocation != null) {
                //we take the current position
                //and place a marker on map
                LatLng mCurrentLatLng = new LatLng (mCurrentLocation.getLatitude (), mCurrentLocation.getLongitude ());
                MarkerOptions marker = new MarkerOptions ().position (mCurrentLatLng).title (getString (R.string.here));
                // adding marker
                marker.icon (BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_ORANGE));
                mGoogleMap.addMarker (marker);
                CameraPosition cameraPosition = new CameraPosition.Builder ().target (mCurrentLatLng).zoom (12).build ();
                mGoogleMap.setMyLocationEnabled (true);
                mGoogleMap.animateCamera (CameraUpdateFactory.newCameraPosition (cameraPosition));
            } else {
                //current locaiton is not available
                //try to re-connect service
                mFlickrPresenter.reconnetService ();
            }
        } else {
            //Map is not initialized
            //try to init map again
            initMap ();
        }
    }

    /**
     * This method will add markers of
     * nearby location on map
     * @param List<FlickrDataModel>
     */
    public void addMarkers (List<FlickrDataModel> data) {
        //error checking
        if (mGoogleMap != null) {
            //add current location first
            updateMap ();
            LatLng venueLocation;
            //clear old data
            if (mMarkerUrl.size () > 1)
                mMarkerUrl.clear ();
            if (mMarkerByLocation != null)
                mMarkerByLocation.clear ();
            //we loop through all the contents
            //
            for (FlickrDataModel venues : data) {
                //If lat. & long are 0, we can just
                //ignore those places
                if (venues.getLatitude () != 0 && venues.getLongitude () != 0) {
                    venueLocation = new LatLng (venues.getLatitude (), venues.getLongitude ());
                    //Add Marker on Map
                    Marker marker = mGoogleMap.addMarker (new MarkerOptions ()
                            .position (venueLocation)
                            .title (venues.getTitle ())
                            .icon (BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_YELLOW)));
                    //Add the Image URL in HashMap
                    mMarkerUrl.put (marker.getId (), venues.getPhotoUrl ());
                    //Add location into List
                    mMarkerByLocation.add (venueLocation);
                }
            }
            //Add our custom Adapter for InfoWindow
            mGoogleMap.setInfoWindowAdapter (new CustomInfoWindowAdapter (mMarkerUrl));
            //free the object for GC
            venueLocation = null;
        }
    }

    /**
     * This method will draw poly lines
     * on the map.
     */
    public void displayPath () {
        //error handling
        if (mGoogleMap != null) {
            if (mNearByLocation != null) {
                //clear old data
                updateMap ();
                addMarkers (mNearByLocation);
                //Polyline init
                PolylineOptions polylineOptions = new PolylineOptions ();
                polylineOptions.color (Color.BLUE);
                polylineOptions.width (5);
                //Get the Last displayed infoWindow position.
                mCurrentSelection = CustomInfoWindowAdapter.mCurrentMarker;
                //User must select any start point
                if (mCurrentSelection == null) {
                    //Snackbar to display condition
                    mSnackbarLayout.setTag (getString(R.string.dismiss));
                    Snackbar.make (findViewById (android.R.id.content), getString (R.string.gps_error), Snackbar.LENGTH_LONG)
                            .setActionTextColor (Color.RED)
                            .setAction (getString(R.string.dismiss), mOnClickListener)
                            .show ();
                    return;
                }
                //take reference of starting point
                LatLng current = mCurrentSelection;
                //We loop through the contents
                //to add markers
                while (mMarkerByLocation.size () != 1) {
                    //add each point
                    polylineOptions.add (current);
                    mGoogleMap.addPolyline (polylineOptions);
                    mMarkerByLocation.remove (current);
                    //remove the already added
                    //and find the next
                    //near most point
                    Collections.sort (mMarkerByLocation, FlickrDataModel.createComparator (current));
                    current = mMarkerByLocation.get (0);
                }
                //Connect the last point
                polylineOptions.add (current);
                mGoogleMap.addPolyline (polylineOptions);
            } else {
                //We do not have nearby location data
                //this could be due to data fetch error
                //from network
                mSnackbarLayout.setTag (getString(R.string.connection));
                Snackbar.make (mSnackbarLayout, getString (R.string.connection_error), Snackbar.LENGTH_LONG)
                        .setActionTextColor (Color.RED)
                        .setAction (getString (R.string.setting), mOnClickListener)
                        .show ();
            }

        }
    }

    /**
     * We handle the snackbar click here.
     * Based on the tag of view,
     * we fire the intents
     */
    View.OnClickListener mOnClickListener =new View.OnClickListener () {
        @Override
        public void onClick (View view) {
             //is it connection error?
            if(mSnackbarLayout.getTag ()==getString (R.string.connection)) {
                Intent intent = new Intent (Settings.ACTION_SETTINGS);
                startActivity (intent);
            }
            //is it GPS problem ?
            else if (mSnackbarLayout.getTag ()==getString (R.string.gps)){
                Intent callGPSSettingIntent = new Intent (
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity (callGPSSettingIntent);
            }
            //We do not handle the dismiss case
        }
    };

}
