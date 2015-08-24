package com.code4sharing.flickrsample.presenter;

import android.location.Location;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.code4sharing.flickrsample.activity.AppController;
import com.code4sharing.flickrsample.model.FlickrDataModel;
import com.code4sharing.flickrsample.parser.FlickrJsonParser;
import com.code4sharing.flickrsample.service.LocationTrackingService;
import com.code4sharing.flickrsample.util.Constants;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by keyur on 22-08-2015.
 * This is our presenter class
 * This class acts as single communication
 * channel with our Activity
 */
public class FlickrPresenter {
    //list of nearby markers
    List<FlickrDataModel> mNearByLocation;
    //Vollery request queue
    RequestQueue mRequestQueue;
    //callback for flickr fetch reqest
    onFectFlickrDataRequest mFlickrRequestListener;
    //for logs
    public static final String TAG = FlickrPresenter.class.getSimpleName ();
    //We take callback from service
    //and pass data to our Activity
    private LocationTrackingService mLocationService;
    private MapRequestCallBacks onUserLocationUpdated;

    /**
     * Constructor of our class
     * @param ActivityCallback callback for flickr fetch request
     * @param MainActivityCallback callback for location data
     */
    public FlickrPresenter (onFectFlickrDataRequest ActivityCallback, MapRequestCallBacks MainActivityCallback) {
        //init volley request queue
        mRequestQueue = AppController.getInstance ().getRequestQueue ();
        //flickr fetch request callback
        this.mFlickrRequestListener = ActivityCallback;
        //init arraylists
        mNearByLocation = new ArrayList<> ();
        //init our locaiton service
        mLocationService = new LocationTrackingService (mLocationListener);
        //build googleapi client for locaiton
        //request
        mLocationService.buildGoogleApiClient ();
        //location update callback
        this.onUserLocationUpdated = MainActivityCallback;
    }

    /**
     * This function is used to re-connect service in case of
     * error
     */
    public void reconnetService () {
        if (!mLocationService.isServiceRunning ()) {
            mLocationService.connectFusedLocationProviderApi ();
        }
    }
    /**
     * This function is used to share service status
     */
    public boolean isServiceRunning() {
        return mLocationService.isServiceRunning ();
    }
    /**
     * This function is used to disconnect service status
     */
    public void disconnectService() {
        mLocationService.stopLocationUpdates();
    }


    /**
     * Download data from network by adding request in volley request queue
     * and populate data list
     */
    public void loadData () {
        final FlickrJsonParser parser = new FlickrJsonParser ();
        // We first check for cached request
        Cache cache = mRequestQueue.getCache ();
        Cache.Entry entry = cache.get (Constants.FLICKR_URL);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String (entry.data, "UTF-8");
                parser.parseFactJsonData (data, mNearByLocation);

            } catch (IOException e) {
                Log.d (TAG, "Error parsing data", e);
            }
        } else {
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject> () {
                @Override
                public void onResponse (JSONObject response) {
                    Log.d (TAG, "Volley Response: " + response.toString ());
                    //send the data for parsing
                    parser.parseFactJsonData (response.toString (), mNearByLocation);
                    //notify to Activity
                    if (mNearByLocation.isEmpty ())
                        mFlickrRequestListener.onRequestError ();
                    else
                        mFlickrRequestListener.onRequestSuccess (mNearByLocation);
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener () {
                @Override
                public void onErrorResponse (VolleyError error) {
                    mFlickrRequestListener.onRequestError ();
                }
            };
            //we add the request to volley
            JsonObjectRequest jsonReq = new JsonObjectRequest (Request.Method.GET,
                    Constants.FLICKR_URL, responseListener, errorListener);

            //Setting TAG if in future request needs to be cancelled.
            jsonReq.setTag (TAG);

            // Adding request to volley request queue
            mRequestQueue.add (jsonReq);
        }
    }

    /**
     * Interface for Flickr data request
     */
    public interface onFectFlickrDataRequest {
        void onRequestSuccess (List<FlickrDataModel> NearByLocation);

        void onRequestError ();
    }

    /**
     * Interface for Location Request
     */
    public interface MapRequestCallBacks {
        void onCurrentLocationUpdated (Location data);
        void onCurrenLocationFailed ();
    }

    /**
     * We receive callback from Location Service
     */
    private LocationTrackingService.NotifyCurrentLocationChanged mLocationListener = new LocationTrackingService.NotifyCurrentLocationChanged () {
        @Override
        public void onLatLngUpdated (Location data) {
            onUserLocationUpdated.onCurrentLocationUpdated (data);
        }

        public void onLocationFail () {
            onUserLocationUpdated.onCurrenLocationFailed ();
        }
    };


}
