package com.qhubb.qhubb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qhubb.qhubb.network.ActivityTask;
import com.qhubb.qhubb.util.Service;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ForgotPasswordActivity extends Activity {

    private EditText txtEmail;

    private Button btnSendPassword;
    private Button btnForgotPasswordBack;
    private TextView textForgotPasswordMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        txtEmail = (EditText) findViewById(R.id.txtEmail);
        btnSendPassword = (Button) findViewById(R.id.btnSendPassword);
        btnForgotPasswordBack = (Button) findViewById(R.id.btnForgotPasswordBack);
        textForgotPasswordMessage = (TextView) findViewById(R.id.textForgotPasswordMessage);

        btnForgotPasswordBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), LoginActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }
        });

        btnSendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execute(view);
            }
        });
}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ProcessRegister extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String email;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            email = txtEmail.getText().toString();

            pDialog = new ProgressDialog(ForgotPasswordActivity.this);
            pDialog.setTitle("QHubb");
            pDialog.setMessage("...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            Service userFunction = new Service();
            JSONObject json = userFunction.forPass(email);
            return json;


        }


        @Override
        protected void onPostExecute(JSONObject json) {
            /**
             * Checks if the Password Change Process is sucesss
             **/
            try {
                if (json.getString("success") != null) {
                    textForgotPasswordMessage.setText("");
                    String res = json.getString("success");
                    Log.e("res", res);

                    int resValue = -1;
                    try{
                        resValue = Integer.parseInt(res);
                    }catch(Exception e){

                    }

                    if (resValue > 0){
                         pDialog.dismiss();
                         textForgotPasswordMessage.setText("An email is sent to you.");
                    }else {
                        pDialog.dismiss();
                        textForgotPasswordMessage.setText("Error in forgot password activity");
                    }
                }else{
                    String red = json.getString("error");
                    Log.e("res", red);
                    int redValue = -1;
                    try{
                        redValue = Integer.parseInt(red);
                    }catch(Exception e){

                    }
                    if (redValue == 2)
                    {    pDialog.dismiss();
                        textForgotPasswordMessage.setText("The email does not exist in the database.");
                    }
                    else {
                        pDialog.dismiss();
                        textForgotPasswordMessage.setText("Error in forgot password activity");
                    }
                }
            }
            catch (JSONException e) {
                Log.e("forgot password", e.getMessage());
            }
        }
    }
    public void execute(View view){
        new ActivityTask(this, (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE),
                textForgotPasswordMessage, new ProcessRegister()).execute();
    }
}
