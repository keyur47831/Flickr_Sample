package com.code4sharing.flickrsample.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

/**
 * Created by keyur on 22-08-2015.
 * The data model required for our project
 * Currently we are not displaying
 * the title on infowindow but we can
 * customised it on requirement
 */
public class FlickrDataModel {
    double mLatitude;
    double mLongitude;
    String mPhotoUrl;
    String mTitle;
    String mPhotoId;

    /*
     * getter and setter methods for
     * each items
     */
    /*
       Latitude
     */
    public void setLatitute (Double value) {
        this.mLatitude = value;
    }

    public double getLatitude () {
        return this.mLatitude;
    }

    /*
     Longitude
     */
    public void setLongitude (Double value) {
        this.mLongitude = value;
    }

    public double getLongitude () {
        return this.mLongitude;
    }

    /*
     PhotoUrl
     */
    public void setPhotoUrl (String value) {
        this.mPhotoUrl = value;
    }

    public String getPhotoUrl () {
        return this.mPhotoUrl;
    }

    /*
     PlaceId
     */
    public void setPhotoId (String value) {
        this.mPhotoId = value;
    }

    public String getPhotoId () {
        return this.mPhotoId;
    }

    /*
     Title
     */
    public void setTitle (String value) {
        this.mTitle = value;
    }

    public String getTitle () {
        return this.mTitle;
    }

    /*
       * We create a Comparator function to compare the near most point.
       * We need to find the near most point from given point
       * in order to draw line on map
       */
    public static Comparator<LatLng> createComparator (LatLng p) {
        final LatLng finalP = new LatLng (p.latitude, p.longitude);
        return new Comparator<LatLng> () {
            @Override
            public int compare (LatLng p0, LatLng p1) {
                float[] result1 = new float[1];
                float[] result2 = new float[1];
                //User android standard function to
                //find the nearmost in given
                //collection of LatLng
                android.location.Location.distanceBetween (finalP.latitude, finalP.longitude, p0.latitude, p0.longitude, result1);
                android.location.Location.distanceBetween (finalP.latitude, finalP.longitude, p1.latitude, p1.longitude, result2);
                return Double.compare (result1[0], result2[0]);
            }

        };
    }

}
