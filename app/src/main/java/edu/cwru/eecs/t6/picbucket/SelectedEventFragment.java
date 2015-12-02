package edu.cwru.eecs.t6.picbucket;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SelectedEventFragment extends Fragment {

    TextView startTimeView;
    TextView endTimeView;
    TextView eventTitle;
    Button editEvent;
    String eventIDString;
    String eventLocation;
    Bundle argument;
    TimelineFragment.EventInfo eventInfo;
    GridView gridView;

    static final String START_TIME = "start";
    static final String END_TIME = "end";
    static final String EVENT_TITLE = "title";
    static final String EVENT_ID = "id";
    static final String LOCATION = "location";
    static final String EVENTINFO = "eventInfo";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View eventView = inflater.inflate(R.layout.fragment_event, container, false);
        startTimeView = (TextView)eventView.findViewById(R.id.startTime);
        endTimeView = (TextView)eventView.findViewById(R.id.endTime);
        eventTitle = (TextView)eventView.findViewById(R.id.eventTitle);
        gridView = (GridView)eventView.findViewById(R.id.gridView);


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

        startTimeView.setText((String) argument.get(START_TIME));
        endTimeView.setText((String)argument.get(END_TIME));
        eventTitle.setText((String) argument.get(EVENT_TITLE));
        eventIDString = ((String)argument.get(EVENT_ID));
        eventLocation = ((String)argument.get(LOCATION));

        gridView.setAdapter(new ImageAdapter(getActivity(), eventInfo.photoList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                ImageAdapter ia = (ImageAdapter)parent.getAdapter();
               /* Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                System.out.println(ia.getItem(position));
                intent.setDataAndType(ia.getItem(position),"image*//*");
                startActivity(intent);*/

                PhotoViewerFragment photoViewer = new PhotoViewerFragment();
                photoViewer.image = ia.getItem(position);

                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, photoViewer);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });



        return eventView;
    }

    public void setArguments(Bundle argument){
        this.argument = argument;
    }


    /**
     * From the android api
     * */
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        List<Uri> uris;

        public ImageAdapter(Context c, List<Uri> data) {
            mContext = c;
            uris = data;
        }

        public int getCount() {
            return uris.size();
        }

        public Uri getItem(int position) {
            return uris.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(500, 500));
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageURI((Uri)this.getItem(position));
            return imageView;
        }
    }
}
