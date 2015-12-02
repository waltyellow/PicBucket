package edu.cwru.eecs.t6.picbucket;

import android.app.Activity;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Walter Huang on 12/2/2015.
 */
public class Core {

    static Activity activity;
    static ImageView imageView;

    final static String[] projection = new String[]{
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.DATA
    };
    static Cursor allPicsCursor;// = MediaStore.Images.Media.query(activity.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection);

    public static void scanPhotos(){
        allPicsCursor = MediaStore.Images.Media.query(activity.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection);
    }

    public static List<Uri> listOfPhotos(long eventStart, long eventEnd, String location){
        ArrayList<Uri> photoURI = new ArrayList<Uri>();

        for (int i = 0; i < allPicsCursor.getCount(); i++) {
            allPicsCursor.moveToPosition(i);
            if(inTime(allPicsCursor.getLong(1), eventStart, eventEnd)){ //&& inLocation(location, allPicsCursor.getDouble(3), allPicsCursor.getDouble(2))){
                photoURI.add(Uri.parse(allPicsCursor.getString(4)));
                //System.out.println("Searching"+allPicsCursor.getString(0));
            }
        }
        return photoURI;
    }

    public static boolean inTime(long photoTimestamp, long eventStart, long eventEnd){
        if(eventStart < photoTimestamp && eventEnd > photoTimestamp){
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean inLocation(String location, double latitude, double longitude){
        Geocoder g = new Geocoder(activity.getApplicationContext());
        try{
            Address address = g.getFromLocationName(location, 1).get(0);
            if(((address.getLongitude() < longitude + 0.001) && (address.getLongitude() > longitude - 0.001)) &&
            ((address.getLatitude() < latitude + 0.001) && (address.getLatitude() > latitude - 0.001))){
                return true;
            }
            else {
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
