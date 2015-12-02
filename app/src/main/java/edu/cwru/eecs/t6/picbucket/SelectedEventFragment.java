package edu.cwru.eecs.t6.picbucket;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SelectedEventFragment extends Fragment {

    TextView startTimeView;
    TextView endTimeView;
    TextView eventTitle;
    Button editEvent;
    String eventIDString;
    String eventLocation;
    Bundle argument;

    static final String START_TIME = "start";
    static final String END_TIME = "end";
    static final String EVENT_TITLE = "title";
    static final String EVENT_ID = "id";
    static final String LOCATION = "location";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View eventView = inflater.inflate(R.layout.fragment_event, container, false);
        startTimeView = (TextView)eventView.findViewById(R.id.startTime);
        endTimeView = (TextView)eventView.findViewById(R.id.endTime);
        eventTitle = (TextView)eventView.findViewById(R.id.eventTitle);

        //edit event button
        editEvent = (Button)eventView.findViewById(R.id.editEvent);
        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long eventID = Long.parseLong(eventIDString);
                //System.out.println(eventID);
                Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(uri);
                System.out.println(uri);
                startActivity(intent);
            }
        });

        startTimeView.setText((String)argument.get(START_TIME));
        endTimeView.setText((String)argument.get(END_TIME));
        eventTitle.setText((String)argument.get(EVENT_TITLE));
        eventIDString = ((String)argument.get(EVENT_ID));
        eventLocation = ((String)argument.get(LOCATION));
        return eventView;
    }

    public void setArguments(Bundle argument){
        this.argument = argument;
    }
}
