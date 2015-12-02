package edu.cwru.eecs.t6.picbucket;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoViewerFragment extends Fragment {

    Uri image;
    ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View displayView = inflater.inflate(R.layout.fragment_viewer, container, false);
        imageView = (ImageView)displayView.findViewById(R.id.displayImage);
        imageView.setImageURI(image);
        return displayView;
    }
}
