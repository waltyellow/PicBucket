package edu.cwru.eecs.t6.picbucket;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.ImageView;

/**
 * Created by Walter Huang on 12/2/2015.
 */
public class Core {

    static Activity activity;
    static ImageView imageView;


    public static boolean test() {
        System.out.println(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null,null);
        String[] projection = new String[]{
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.DATA
        };
        final Cursor cursor = MediaStore.Images.Media.query(activity.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection);
        System.out.println(cursor.getCount());
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            //imageView.setImageURI(Uri.parse(cursor.getString(4)));
            System.out.print(cursor.getString(0) + ",");
            System.out.print(cursor.getString(1) + ",");
            System.out.print(cursor.getString(2)+",");
            System.out.println(cursor.getString(3));
            System.out.println(cursor.getString(4));
        }
        return false;
    }

    public static boolean isPhotoInTimeFrame(Cursor cursor, int id, String DTSTART, String DTEND){
        cursor.moveToPosition(id);

        return false;
    }

}
