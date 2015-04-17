package com.qhubb.qhubb.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ActivityTask extends AsyncTask<String,String,Boolean>
{
    private ProgressDialog nDialog;

    private Activity activity;
    private ConnectivityManager cm;
    private TextView textMessage;
    private AsyncTask<String, String, JSONObject> task;

    public ActivityTask(Activity activity, ConnectivityManager cm, TextView textMessage,
                        AsyncTask<String, String, JSONObject> task){
        this.activity = activity;
        this.cm = cm;
        this.textMessage = textMessage;
        this.task = task;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        nDialog = new ProgressDialog(activity);
        nDialog.setTitle("QHubb");
        nDialog.setMessage("...");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... args){
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL("http://www.google.com");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    return true;
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;

    }
    @Override
    protected void onPostExecute(Boolean th){

        if(th == true){
            nDialog.dismiss();
            task.execute();
        }
        else{
            nDialog.dismiss();
            textMessage.setText("Error in connection");
        }
    }
}