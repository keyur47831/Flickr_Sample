package com.code4sharing.flickrsample.activity;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.code4sharing.flickrsample.util.LruBitmapCache;

/**
 * Created by keyur on 22-08-2015.
 */
public class AppController extends Application {
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController mInstance;
    LruBitmapCache mLruBitmapCache;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance()
    {
        return mInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue (getApplicationContext ());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader()
    {
        getRequestQueue();
        if (mImageLoader == null)
        {

            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }
    public LruBitmapCache getLruBitmapCache()
    {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    /**
     * This method checks if data connection is available or not. If available
     * returns true, otherwise returns false.
     *
     * @return boolean True if data connection is available otherwise false
     */
    public boolean isDataConnAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() == null || !connectivityManager.getActiveNetworkInfo().isConnected())
        {
            return false;
        }
        return true;
    }
    /**
     * This method checks if data connection is available or not. If available
     * returns true, otherwise returns false.
     *
     * @return boolean True if data connection is available otherwise false
     */
    public boolean isGPSConnAvailable()
    {
        LocationManager service = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
