package com.example.smartparking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mcontext;

    public CustomWindowAdapter(Context context)
    {
        mcontext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);
    }
    //setting text to the view
    private void WindowText(Marker marker,View view)
    {
        String title = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.map_marker_title);

        if (!title.equals(""))
        {
            tvTitle.setText(title);
        }
        String snippet = marker.getSnippet();
        TextView tvSnippet =  (TextView) view.findViewById(R.id.map_snippet);

        if (!snippet.equals(""))
        {
            tvSnippet.setText(snippet);
        }
    }
    @Override
    public View getInfoWindow(Marker marker) {

        WindowText(marker,mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        WindowText(marker,mWindow);
        return mWindow;
    }
}
