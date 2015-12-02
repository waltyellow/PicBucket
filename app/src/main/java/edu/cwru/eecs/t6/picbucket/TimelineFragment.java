package edu.cwru.eecs.t6.picbucket;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The timeline fragment
 */
public class TimelineFragment extends Fragment {

    Button cameraButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    // This is the Adapter being used to display the list's data
    public ArrayAdapter<EventInfo> mAdapter;

    // ArrayList of Strings of each Event information
    private ArrayList<EventInfo> mEventInfo = new ArrayList<EventInfo>();

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
        String eventString = "";
        // Cursor with data from Content Provider query
        Cursor cursor =  getActivity().getContentResolver().query(CalendarContract.Events.CONTENT_URI, PROJECTION, SELECTION, null, null);
        // Combining the query data into a single String to put in a TextView
        EventInfo eventInfo;
        if(cursor.moveToFirst()){
            eventInfo = new EventInfo(cursor);
            mEventInfo.add(eventInfo);
            while(cursor.moveToNext()){
                eventInfo = new EventInfo(cursor);
                mEventInfo.add(eventInfo);
            }
        }

        mEventInfo = nonEmptyEvents(mEventInfo);

        // Set objects into ListView
        mAdapter = new ArrayAdapter<EventInfo>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, mEventInfo);
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
                SelectedEventFragment selectedEvent = new SelectedEventFragment();
                Bundle arguments = new Bundle();
                EventInfo clickedEventInfo = ((EventInfo)parent.getItemAtPosition(position));
                arguments.putString(SelectedEventFragment.START_TIME,split[1]);
                arguments.putString(SelectedEventFragment.END_TIME,split[2]);
                arguments.putString(SelectedEventFragment.EVENT_TITLE,split[0]);
                arguments.putString(SelectedEventFragment.EVENT_ID,clickedEventInfo.eventID);
                arguments.putString(SelectedEventFragment.LOCATION, clickedEventInfo.location);
                selectedEvent.eventInfo = clickedEventInfo;
                selectedEvent.setArguments(arguments);

                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, selectedEvent);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        return timelineView;
    }

    private ArrayList<EventInfo> nonEmptyEvents(ArrayList<EventInfo> originalList){
        ArrayList<EventInfo> output = new ArrayList<>();
        for(EventInfo unfiltered: originalList){
            unfiltered.photoList = Core.listOfPhotos(unfiltered.startTime, unfiltered.endTime, unfiltered.location);
            unfiltered.estimatedCount = unfiltered.photoList.size();
            if (unfiltered.estimatedCount > 0){
                output.add(unfiltered);
            }
        }
        return output;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(),  imageFileName);
        return photo;
    }

    public class EventInfo {
        String[] split;
        String eventString = "";
        String eventID;
        Long startTime;
        Long endTime;
        int estimatedCount = 0;
        String location ="";
        List<Uri> photoList;

        public EventInfo(Cursor cursor){
            eventID = cursor.getString(0);
            for(int i = 1; i < 5; i++){
                // Chane the date/time format
                if(i == 2 || i == 3){
                    eventString = eventString + getDate(cursor.getLong(i));
                }
                else {
                    eventString = eventString + cursor.getString(i);
                }
                if (i == 4){
                    location = cursor.getString(i);
                }
                if(i != 4) {
                    eventString = eventString + "\n";
                }
            }
            split = eventString.split("\n");
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
                Date startTime = sdf.parse(split[1]);
                Date endTime = sdf.parse(split[2]);
                System.out.println(startTime.getTime());
                System.out.println(endTime.getTime());
            }catch(Exception e){
                e.printStackTrace();
            }
            startTime = Long.parseLong(split[1]);
            endTime = Long.parseLong(split[2]);
        }

        @Override
        public String toString() {
            return eventString + "\n(Estimated Photos:" + estimatedCount +")";
        }
    }
}
