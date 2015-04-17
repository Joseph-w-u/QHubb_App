package com.qhubb.qhubb;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qhubb.qhubb.network.ActivityTask;
import com.qhubb.qhubb.util.Service;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RegisterActivity extends Activity {

    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtEmail;
    private EditText txtPassword;

    private Button btnRegister;
    private Button btnRegisterBack;
    private TextView textRegisterMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegisterBack = (Button) findViewById(R.id.btnRegisterBack);
        textRegisterMessage = (TextView) findViewById(R.id.textRegisterMessage);

        btnRegisterBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), LoginActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( ( !txtPassword.getText().toString().equals("")) && ( !txtFirstName.getText().toString().equals("")) &&
                        ( !txtLastName.getText().toString().equals("")) && ( !txtEmail.getText().toString().equals("")) )
                {
                     execute(view);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "One or more fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

        String email,password,fname,lname;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fname = txtFirstName.getText().toString();
            lname = txtLastName.getText().toString();
            email = txtEmail.getText().toString();
            password = txtPassword.getText().toString();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setTitle("QHubb");
            pDialog.setMessage("...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            Service userFunction = new Service();
            JSONObject json = userFunction.registerUser(fname, lname, email, password);

            return json;


        }
        @Override
        protected void onPostExecute(JSONObject json) {
            /**
             * Checks for success message.
             **/
            try {
                if (json.getString("success") != null) {
                    textRegisterMessage.setText("");
                    String res = json.getString("success");

                    String red = json.getString("error");

                    if(Integer.parseInt(res) == 1){
                        pDialog.setTitle("Getting Data");
                        pDialog.setMessage("Loading Info");

                        textRegisterMessage.setText("Successfully Registered");
                        Toast.makeText(getApplicationContext(),
                                "Successfully Registered", Toast.LENGTH_SHORT).show();

                        Intent registered = new Intent(getApplicationContext(), LoginActivity.class);

                        /**
                         * Close all views before launching Registered screen
                         **/
                        registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pDialog.dismiss();
                        startActivity(registered);

                        finish();
                    }

                    else if (Integer.parseInt(red) ==2){
                        pDialog.dismiss();
                        textRegisterMessage.setText("User already exists");
                    }
                    else if (Integer.parseInt(red) ==3){
                        pDialog.dismiss();
                        textRegisterMessage.setText("Invalid Email");
                    }

                }


                else{
                    pDialog.dismiss();

                    textRegisterMessage.setText("Error in registration");
                }

            } catch (JSONException e) {
                e.printStackTrace();


            }
        }
    }
    public void execute(View view){
        new ActivityTask(this, (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE),
                textRegisterMessage, new ProcessRegister()).execute();
    }
}
