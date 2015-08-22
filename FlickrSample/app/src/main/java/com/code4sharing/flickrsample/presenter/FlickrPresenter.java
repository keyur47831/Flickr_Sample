package com.code4sharing.flickrsample.presenter;

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
import com.code4sharing.flickrsample.util.Constants;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by keyur on 22-08-2015.
 */
public class FlickrPresenter {
    List<FlickrDataModel> mNearByLocation;
    RequestQueue mRequestQueue;
    onFectFlickrDataRequest mFlickrRequestListener;
    public static final String TAG=FlickrPresenter.class.getSimpleName ();

    public FlickrPresenter(onFectFlickrDataRequest ActivityCallback)
    {
        mRequestQueue=AppController.getInstance ().getRequestQueue ();
        this.mFlickrRequestListener =ActivityCallback;
         mNearByLocation=new ArrayList<> ();

    }
    /**
     * Download data from network by adding request in volley request queue
     * and populate data list
     */
    public void loadData()
    {

        final FlickrJsonParser parser = new FlickrJsonParser();

        // We first check for cached request
        Cache cache = mRequestQueue.getCache();
        Cache.Entry entry = cache.get(Constants.FLICKR_URL);
        if (entry != null)
        {
            // fetch the data from cache
            try
            {
                String data = new String(entry.data,"UTF-8");
                parser.parseFactJsonData (data, mNearByLocation);

            }
            catch (IOException e)
            {
                Log.d (TAG, "Error parsing data", e);
            }
        }
        else
        {
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    Log.d(TAG, "Volley Response: " + response.toString());
                    if (response != null)
                    {
                        parser.parseFactJsonData (response.toString (), mNearByLocation);
                        if(mNearByLocation.isEmpty ())
                        mFlickrRequestListener.onRequestError ();
                        else
                         mFlickrRequestListener.onRequestSucess (mNearByLocation);
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    mFlickrRequestListener.onRequestError ();
                }
            };

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                    Constants.FLICKR_URL, responseListener, errorListener);

            //Setting TAG if in future request needs to be cancelled.
            jsonReq.setTag(TAG);

            // Adding request to volley request queue
            mRequestQueue.add(jsonReq);
        }
    }
     public interface onFectFlickrDataRequest{
         void onRequestSucess(List<FlickrDataModel> NearByLocation);
         void onRequestError();
     }


}
