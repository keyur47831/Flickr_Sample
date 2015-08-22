package com.code4sharing.flickrsample.model;

/**
 * Created by keyur on 22-08-2015.
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
    public void setLatitute(Double value)
    {
        this.mLatitude=value;
    }
    public double getLatitude()
    {
        return this.mLatitude;
    }
    /*
     Longitude
     */
    public void setLongitude(Double value)
    {
        this.mLongitude=value;
    }
    public double getLongitude()
    {
        return this.mLongitude;
    }
    /*
     PhotoUrl
     */
    public void setPhotoUrl(String value)
    {
        this.mPhotoUrl=value;
    }
    public String getPhotoUrl()
    {
        return this.mPhotoUrl;
    }
    /*
     PlaceId
     */
    public void setPhotoId(String value)
    {
        this.mPhotoId=value;
    }
    public String getPhotoId()
    {
        return this.mPhotoId;
    }
    /*
     Title
     */
    public void setTitle(String value)
    {
        this.mTitle=value;
    }
    public String getTitle()
    {
        return this.mTitle;
    }



}
