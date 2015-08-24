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

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    FlickrPresenter mFlickrPresenter;
    CoordinatorLayout mSnackbarLayout;
    private Toolbar mToolbar;
    private GoogleMap mGoogleMap;
    private Location mCurrentLocation;
    private LatLng mCurrentSelection;
    MapFragment mapFragment;
    HashMap<String, String> mMarkerUrl = new HashMap<> ();
    List<FlickrDataModel> mNearByLocation = new ArrayList<> ();
    ArrayList<LatLng> mMarkerByLocation = new ArrayList<> ();
    public static final String TAG = HomeActivity.class.getSimpleName ();

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_home);
        mToolbar = (Toolbar) findViewById (R.id.toolbar);
        setSupportActionBar (mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar ().setDisplayShowHomeEnabled (true);
        mapFragment = (MapFragment) getFragmentManager ()
                .findFragmentById (R.id.map);
        mFlickrPresenter = new FlickrPresenter (mRequestFlickrData, mRequestLocationData);
        mSnackbarLayout =(CoordinatorLayout)findViewById (R.id.snackbar);
        initMap ();
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
            updateMap ();
            return true;
        } else if (id == R.id.show_nearby) {
            mFlickrPresenter.loadData ();
            return true;
        } else if (id == R.id.show_path) {
            displayPath ();
            return true;
        }
        return super.onOptionsItemSelected (item);
    }

    private FlickrPresenter.onFectFlickrDataRequest mRequestFlickrData = new FlickrPresenter.onFectFlickrDataRequest () {

        @Override
        public void onRequestSucess (List<FlickrDataModel> NearByLocation) {
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

    private void initMap () {
        if (mGoogleMap == null) {

            mapFragment.getMapAsync (this);
        }
    }

    @Override
    public void onMapReady (GoogleMap map) {
        mGoogleMap = map;
        if (mCurrentLocation != null) {
            updateMap ();
        } else
            mFlickrPresenter.reconnetService ();
    }

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

    public void updateMap () {
        if (mGoogleMap != null) {
            mGoogleMap.clear ();
            if (mCurrentLocation != null) {
                LatLng mCurrentLatLng = new LatLng (mCurrentLocation.getLatitude (), mCurrentLocation.getLongitude ());
                MarkerOptions marker = new MarkerOptions ().position (mCurrentLatLng).title (getString (R.string.here));
                // adding marker
                marker.icon (BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_ORANGE));
                mGoogleMap.addMarker (marker);
                CameraPosition cameraPosition = new CameraPosition.Builder ().target (mCurrentLatLng).zoom (12).build ();
                mGoogleMap.setMyLocationEnabled (true);
                mGoogleMap.animateCamera (CameraUpdateFactory.newCameraPosition (cameraPosition));
            } else {
                mFlickrPresenter.reconnetService ();
            }
        } else {
            initMap ();
        }
    }

    public void addMarkers (List<FlickrDataModel> data) {
        if (mGoogleMap != null) {
            updateMap ();
            LatLng venueLocation;
            if (mMarkerUrl.size () > 1)
                mMarkerUrl.clear ();
            if (mMarkerByLocation != null)
                mMarkerByLocation.clear ();
            for (FlickrDataModel venues : data) {
                if (venues.getLatitude () != 0 && venues.getLongitude () != 0) {
                    venueLocation = new LatLng (venues.getLatitude (), venues.getLongitude ());

                    Marker marker = mGoogleMap.addMarker (new MarkerOptions ()
                            .position (venueLocation)
                            .title (venues.getTitle ())
                            .icon (BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_YELLOW)));
                    mMarkerUrl.put (marker.getId (), venues.getPhotoUrl ());
                    mMarkerByLocation.add (venueLocation);
                }
            }
            mGoogleMap.setInfoWindowAdapter (new CustomInfoWindowAdapter (mMarkerUrl));
            venueLocation = null;
        }
    }

    public void displayPath () {
        if (mGoogleMap != null) {
            if (mNearByLocation != null) {
                updateMap ();
                addMarkers (mNearByLocation);
                PolylineOptions polylineOptions = new PolylineOptions ();
                polylineOptions.color (Color.BLUE);
                polylineOptions.width (5);
                mCurrentSelection = CustomInfoWindowAdapter.mCurrentMarker;
                if (mCurrentSelection == null) {
                    mSnackbarLayout.setTag (getString(R.string.dismiss));
                    Snackbar.make (findViewById (android.R.id.content), getString (R.string.gps_error), Snackbar.LENGTH_LONG)
                            .setActionTextColor (Color.RED)
                            .setAction (getString(R.string.dismiss), mOnClickListener)
                            .show ();
                    return;
                }
                LatLng current = mCurrentSelection;
                while (mMarkerByLocation.size () != 1) {
                    polylineOptions.add (current);
                    mGoogleMap.addPolyline (polylineOptions);
                    mMarkerByLocation.remove (current);
                    Collections.sort (mMarkerByLocation, FlickrDataModel.createComparator (current));
                    current = mMarkerByLocation.get (0);
                }
                polylineOptions.add (current);
                mGoogleMap.addPolyline (polylineOptions);
            } else {
                mSnackbarLayout.setTag (getString(R.string.connection));
                Snackbar.make (mSnackbarLayout, getString (R.string.connection_error), Snackbar.LENGTH_LONG)
                        .setActionTextColor (Color.RED)
                        .setAction (getString (R.string.setting), mOnClickListener)
                        .show ();
            }

        }
    }
    View.OnClickListener mOnClickListener =new View.OnClickListener () {
        @Override
        public void onClick (View view) {


            if(mSnackbarLayout.getTag ()==getString (R.string.connection)) {
                Intent intent = new Intent (Settings.ACTION_SETTINGS);
                startActivity (intent);
            }
            else if (mSnackbarLayout.getTag ()==getString (R.string.gps)){
                Intent callGPSSettingIntent = new Intent (
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity (callGPSSettingIntent);
            }
        }
    };

}
