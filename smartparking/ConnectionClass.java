package com.example.smartparking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by vinri on 10/16/2017.
 */

public class ConnectionClass extends AsyncTask<String, Void, String> {
    private Context mContext;
    private ProgressDialog dialog;
    private String typeofActivity;
    private AppController controller;
    private AlertDialog alert;

    ConnectionClass(Context otx, String typeofActivity) {
        this.mContext = otx;
        this.typeofActivity = typeofActivity;
        controller = (AppController) mContext;
        dialog = controller.getProgressDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getResources().getString(R.string.connection_not_established))
                .setCancelable(true)
                .setPositiveButton(mContext.getResources().getString(R.string.close_dialog),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        alert = builder.create();
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String fetch_data_url = params[0];
            String postedJson = params[1];
            URL url = new URL(fetch_data_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream OS = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

            String data = URLEncoder.encode("postedData", "UTF-8") + "=" + URLEncoder.encode(postedJson, "UTF-8") + "&" +
                    URLEncoder.encode("typeOfActivity", "UTF-8") + "=" + URLEncoder.encode(typeofActivity, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            OS.close();
            InputStream IS = httpURLConnection.getInputStream();
            String result = "";
            String line = "";

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            IS.close();
            httpURLConnection.disconnect();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
            Activity activity = (Activity) mContext;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    alert.show();
                }
            });
        }

        dialog.dismiss();
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... params) {
        super.onProgressUpdate();
    }

    @Override
    protected void onPostExecute(String result) {
        switch (typeofActivity) {
            case "sendCash":
                try {
                    controller.retrieveData(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                break;

            case "confirmPayment":
                try {
                    controller.retrieveData(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                break;
            default:
                dialog.dismiss();
                break;
        }
    }
}
