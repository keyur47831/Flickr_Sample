package com.code4sharing.flickrsample.presenter;

import android.location.Location;

import com.code4sharing.flickrsample.service.LocationTrackingService;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by keyur on 22-08-2015.
 */
public class MapPresenter {

    private LocationTrackingService mLocationService;
    private MapRequestCallBacks onUserLocationUpdated;




    public MapPresenter(MapRequestCallBacks ActivityCallback)
    {
        mLocationService=new LocationTrackingService (mLocationListener);
        mLocationService.buildGoogleApiClient();

        this.onUserLocationUpdated=ActivityCallback;
    }
   public interface MapRequestCallBacks
   {
       void onCurrentLocationUpdated(Location data);
   }
    private  LocationTrackingService.NotifyCurrentLocationChanged mLocationListener=new LocationTrackingService.NotifyCurrentLocationChanged()
    {
        @Override
        public void onLatLngUpdated(Location data)
        {
            onUserLocationUpdated.onCurrentLocationUpdated (data);
        }
    };


}
