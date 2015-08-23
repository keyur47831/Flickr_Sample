package com.code4sharing.flickrsample.util;

/**
 * Created by keyur on 22-08-2015.
 */
public class Constants {
    public static final String FLICKR_URL="https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=d30642e62e21f360e830d9045fac62c5&lat=-37.872160&lon=144.989943&extras=geo%2Curl_s&format=json&nojsoncallback=1&per_page=100&page=1";
    public static final String BLANK_STR = "";
    public static final String ROW_TAG="photo";
    public static final String PHOTOS_TAG="photos";
    public static final String PHOTOS_ID="id";
    public static final String PHOTO_TITLE="title";
    public static final String PHOTO_LATITUDE="latitude";
    public static final String PHOTO_LONGITUDE="longitude";
    public static final String PHOTO_URL="url_s";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
}
