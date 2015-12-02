package edu.cwru.eecs.t6.picbucket;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * The timeline fragment
 */
public class TimelineFragment extends Fragment {

    Button cameraButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    // This is the Adapter being used to display the list's data
    public ArrayAdapter<String> mAdapter;

    // ArrayList of Strings of each Event information
    private ArrayList<String> mEventInfo = new ArrayList<String>();

    // These are the Events rows that we will retrieve
    static final String[] PROJECTION = new String[] {
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.EVENT_LOCATION
    };

    // August 1, 2015 in Millis since Epoch
    static final long start = 1438646400000L;

    // This is the select criteria
    static final String SELECTION = "(" + CalendarContract.Events.DTSTART + ">" + start + ")";

    public TimelineFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View timelineView = inflater.inflate(R.layout.fragment_timeline, container, false);
        cameraButton = (Button) (timelineView.findViewById(R.id.camera));
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // String to store in TextView for an Event
        String eventInfo = "";
        // Cursor with data from Content Provider query
        Cursor cursor =  getActivity().getContentResolver().query(CalendarContract.Events.CONTENT_URI, PROJECTION, SELECTION, null, null);
        // Combining the query data into a single String to put in a TextView
        if(cursor.moveToFirst()){
            for(int i = 1; i < 5; i++){
                // Chane the date/time format
                if(i == 2 || i == 3){
                    eventInfo = eventInfo + getDate(cursor.getLong(i));
                }
                else {
                    eventInfo = eventInfo + cursor.getString(i);
                }
                if(i != 4) {
                    eventInfo = eventInfo + "\n";
                }
            }
            mEventInfo.add(eventInfo);
            eventInfo = "";
            while(cursor.moveToNext()){
                for(int i = 1; i < 5; i++){
                    if(i == 2 || i == 3){
                        eventInfo = eventInfo + getDate(cursor.getLong(i));
                    }
                    else {
                        eventInfo = eventInfo + cursor.getString(i);
                    }
                    if(i != 4) {
                        eventInfo = eventInfo + "\n";
                    }
                }
                mEventInfo.add(eventInfo);
                eventInfo = "";
            }
        }

        // Set objects into ListView
        mAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, mEventInfo);
        ListView timeline = (ListView) timelineView.findViewById(R.id.timeline);
        timeline.setAdapter(mAdapter);

        // Start new Fragment on Item click
        timeline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String eventData = ((TextView) view).getText().toString();
                String[] split = eventData.split("\n");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
                try {
                    Date startTime = sdf.parse(split[1]);
                    Date endTime = sdf.parse(split[2]);
                    System.out.println(startTime.getTime());
                    System.out.println(endTime.getTime());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        return timelineView;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
