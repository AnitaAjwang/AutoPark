package com.example.smartparking;

import android.app.ProgressDialog;
import android.support.v4.widget.SwipeRefreshLayout;

import org.json.JSONException;

/**
 * Created by vinri on 10/14/2017.
 */

public interface AppController {
    public ProgressDialog getProgressDialog();
    public void retrieveData(String result) throws JSONException;
}
