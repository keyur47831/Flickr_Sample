package com.code4sharing.flickrsample.activity;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.code4sharing.flickrsample.util.LruBitmapCache;

/**
 * Created by keyur on 22-08-2015.
 * This class will act as a singleton
 * instance to hold all global references
 */
public class AppController extends Application {
    //Volley request queue
    private RequestQueue mRequestQueue;
    //Volley imageloader
    private ImageLoader mImageLoader;
    //singleton pattern
    private static AppController mInstance;
    //Cache for bitmaps
    LruBitmapCache mLruBitmapCache;

    @Override
    public void onCreate () {
        super.onCreate ();
        mInstance = this;
    }

    /**
     * Singleton implementation
     * @return AppController
     */
    public static synchronized AppController getInstance () {
        return mInstance;
    }

    /**
     * Method to get Volley RequestQueue
     * @return RequestQueue
     */
    public RequestQueue getRequestQueue () {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue (getApplicationContext ());
        }
        return mRequestQueue;
    }

    /**
     * Method to get Volley ImageLoader
     * @return ImageLoader
     */
    public ImageLoader getImageLoader () {
        getRequestQueue ();
        if (mImageLoader == null) {

            mImageLoader = new ImageLoader (this.mRequestQueue, getLruBitmapCache ());
        }

        return this.mImageLoader;
    }

    /**
     * Method to get LruBitMapCache instance
     * @return LruBitmapCache
     */
    public LruBitmapCache getLruBitmapCache () {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache ();
        return this.mLruBitmapCache;
    }

}
