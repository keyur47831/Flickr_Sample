package com.code4sharing.flickrsample.activity;

import android.app.FragmentTransaction;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.code4sharing.flickrsample.R;
import com.code4sharing.flickrsample.model.FlickrDataModel;
import com.code4sharing.flickrsample.presenter.FlickrPresenter;
import com.code4sharing.flickrsample.presenter.MapPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {


    FlickrPresenter mFlickrPresenter;
    MapPresenter mMapPresenter;
    private Toolbar mToolbar;
    private GoogleMap mGoogleMap;
    private Location mCurrentLocation;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_home);
        mToolbar = (Toolbar) findViewById (R.id.toolbar);
        setSupportActionBar (mToolbar);
        getSupportActionBar ().setDisplayShowHomeEnabled (true);
        mFlickrPresenter = new FlickrPresenter (mRequestFlickrData);
        mMapPresenter = new MapPresenter (mRequestLocationData);
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
            mFlickrPresenter.loadData ();
            return true;
        }

        return super.onOptionsItemSelected (item);
    }

    private FlickrPresenter.onFectFlickrDataRequest mRequestFlickrData = new FlickrPresenter.onFectFlickrDataRequest () {

        @Override
        public void onRequestSucess (List<FlickrDataModel> NearByLocation) {
            Toast.makeText (getApplicationContext (), "DataSize : " + NearByLocation.size (), Toast.LENGTH_SHORT).show ();
        }

        @Override
        public void onRequestError () {
            Toast.makeText (getApplicationContext (), R.string.connection_error, Toast.LENGTH_SHORT).show ();

        }
    };

    private void initMap () {
        if (mGoogleMap == null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager ()
                    .findFragmentById (R.id.map);
            mapFragment.getMapAsync (this);
        }
    }

    @Override
    public void onMapReady (GoogleMap map) {
        mGoogleMap = map;

        if(mCurrentLocation!=null)
        {
            updateMap ();
        }
    }

    private MapPresenter.MapRequestCallBacks mRequestLocationData = new MapPresenter.MapRequestCallBacks () {
        @Override
        public void onCurrentLocationUpdated (Location data) {
           mCurrentLocation=data;
            updateMap ();

        }
    };

    public void updateMap () {
        mGoogleMap.clear ();
        LatLng mCurrentLatLng=new LatLng(mCurrentLocation.getLatitude (),mCurrentLocation.getLongitude ());
        MarkerOptions marker = new MarkerOptions ().position(mCurrentLatLng).title(getString (R.string.here));
        // adding marker
        marker.icon(BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_YELLOW));
        mGoogleMap.addMarker (marker);
        CameraPosition cameraPosition = new CameraPosition.Builder().target (mCurrentLatLng).zoom (12).build ();
        mGoogleMap.setMyLocationEnabled (true);
        mGoogleMap.animateCamera (CameraUpdateFactory.newCameraPosition (cameraPosition));
    }

}
