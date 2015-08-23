package com.code4sharing.flickrsample.parser;

import android.support.annotation.NonNull;
import android.util.Log;

import com.code4sharing.flickrsample.model.FlickrDataModel;
import com.code4sharing.flickrsample.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by keyur on 22-08-2015.
 */
public class FlickrJsonParser {
    private static final String TAG = FlickrJsonParser.class.getName();
    /**
     * Method to parse json raw data in to list and returns the Title
     *
     * @param jsonString The raw json data
     * @param dataList the List on which the data will be stored
     * @return Title of the data
     */
    public void parseFactJsonData(@NonNull String jsonString, @NonNull List<FlickrDataModel> dataList)
    {


        if(jsonString == null || jsonString.isEmpty() || dataList == null)
        {
            Log.e (TAG, "Json Data or the list to be populated should not be null");
            return ;
        }

        try
        {
            JSONObject jsonObject = new JSONObject(jsonString);

            if (jsonObject.has(Constants.PHOTOS_TAG))
            {
                jsonObject  = jsonObject.getJSONObject (Constants.PHOTOS_TAG);
                if (jsonObject.has(Constants.ROW_TAG))
                {
                    JSONArray rows=jsonObject.getJSONArray (Constants.ROW_TAG);
                    parseJsonRows(rows, dataList);
                }
            }



        }
        catch (JSONException e)
        {
            Log.e(TAG, "JSON Exception is caught ### ", e);
        }

    }

    /**
     * Method to parse individual rows
     * </p>
     * @param rows JSONArray object of rows
     * @param dataList the list where the rows will be populated
     */
    private void parseJsonRows(@NonNull JSONArray rows, @NonNull List<FlickrDataModel> dataList)
    {
        if(dataList == null || rows == null)
        {
            Log.e(TAG, "Input params should not be null");
            return ;
        }

        //clear the data list
        dataList.clear();
        int lenght=rows.length();
        for (int i = 0; i < lenght; i++)
        {
            try
            {
                JSONObject rowJSON = rows.getJSONObject (i);
                String photoID = getStringFromObject (rowJSON, Constants.PHOTOS_ID);
                String photoTitle = getStringFromObject (rowJSON, Constants.PHOTO_TITLE);
                double latitude=0;
                String tempLatLng=getStringFromObject (rowJSON, Constants.PHOTO_LATITUDE);
                if(!tempLatLng.isEmpty ()) {
                    latitude = Double.parseDouble (tempLatLng);
                }
                double longitude=0;
                tempLatLng=getStringFromObject (rowJSON, Constants.PHOTO_LONGITUDE);
                if(!tempLatLng.isEmpty ()) {
                    longitude = Double.parseDouble (tempLatLng);
                }
                //free the object for GC
                tempLatLng=null;
                String rowImageHref = getStringFromObject(rowJSON, Constants.PHOTO_URL);
                FlickrDataModel flickrModel = new FlickrDataModel();
                flickrModel.setTitle (photoTitle);
                flickrModel.setPhotoId (photoID);
                flickrModel.setPhotoUrl (rowImageHref);
                flickrModel.setLatitute (latitude);
                flickrModel.setLongitude (longitude);

                dataList.add (flickrModel);

            }
            catch (JSONException e)
            {
                Log.e(TAG, "JSON Exception is caught ####", e);
            }
        }

    }

    private String getStringFromObject(JSONObject rowJSON, String element)
    {
        try
        {
            if (rowJSON.has(element))
            {
                return rowJSON.getString(element);
            }

        }
        catch (JSONException e)
        {
            Log.e(TAG, "JSON Exception is caught ####", e);
        }
        return Constants.BLANK_STR;
    }

}
