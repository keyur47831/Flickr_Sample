package com.code4sharing.flickrsample.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.code4sharing.flickrsample.R;
import com.code4sharing.flickrsample.activity.AppController;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

/**
 * Created by keyur on 23-08-2015.
 * This class is our custom implementation
 * of InfoWindow when the user click on Markers.
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    //UI init
    private View myContentsView;
    private ImageView img;
    private LayoutInflater inflater;
    //List of URLs
    public HashMap<String, String> mMarkerImageList = new HashMap<> ();
    private Marker markerShowingInfoWindow;
    //we expose this for activity
    //instead of handling onMarkerClick
    public static LatLng mCurrentMarker;
    //for logs
    public static final String TAG = CustomInfoWindowAdapter.class.getSimpleName ();

    /**
     * Constructor for our class
     * @param mImageList
     * @param HashMap of URL
     */
    public CustomInfoWindowAdapter (HashMap<String, String> mImageList) {
        if (inflater == null)
            inflater = (LayoutInflater) AppController.getInstance ()
                    .getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        myContentsView = inflater.inflate (R.layout.custom_info_window, null);
        this.mMarkerImageList = mImageList;
    }

    @Override
    public View getInfoWindow (Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents (Marker marker) {
        markerShowingInfoWindow = marker;
        ImageLoader imageLoader = AppController.getInstance ().getImageLoader ();
        //store the selected marker
        //in static variable
        mCurrentMarker = marker.getPosition ();
        //Image UI element init
        img = (ImageView) myContentsView.findViewById (R.id.temp_image);
        //query the url from list
        String url = mMarkerImageList.get (marker.getId ());
        if (url == null) {
            //We will display default no image
            img.setImageResource (R.drawable.no_image);
        } else {
            //We load the image async
            //using Volley and add it in
            //LruCache
            imageLoader.get (url, new ImageLoader.ImageListener () {
                @Override
                public void onErrorResponse (VolleyError error) {
                    Log.e (TAG, "Image Load Error: " + error.getMessage ());
                }

                @Override
                public void onResponse (ImageLoader.ImageContainer response, boolean arg1) {
                    if (response.getBitmap () != null) {
                        img.setImageBitmap (response.getBitmap ());
                        //we need to update the UI so that image
                        //is visible to User
                        if (markerShowingInfoWindow != null && markerShowingInfoWindow.isInfoWindowShown ()) {
                            markerShowingInfoWindow.hideInfoWindow ();
                            markerShowingInfoWindow.showInfoWindow ();
                        }
                    }
                }
            });
        }
        return myContentsView;
    }

}
