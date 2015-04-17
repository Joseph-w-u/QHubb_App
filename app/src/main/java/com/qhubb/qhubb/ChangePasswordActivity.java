package com.qhubb.qhubb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import android.util.Log;
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
import java.util.HashMap;

public class ChangePasswordActivity extends Activity {

    private EditText txtPassword;
    private Button btnChangePassword;
    private Button btnChangePasswordBack;
    private TextView textChangePasswordMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        btnChangePasswordBack = (Button) findViewById(R.id.btnChangePasswordBack);
        textChangePasswordMessage = (TextView) findViewById(R.id.textChangePasswordMessage);

        btnChangePasswordBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtPassword.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),
                            "Email empty", Toast.LENGTH_SHORT).show();
                }else {
                    execute(view);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
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
        String newpas, email;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            newpas = txtPassword.getText().toString();
            email = (String) getIntent().getSerializableExtra("email");

            pDialog = new ProgressDialog(ChangePasswordActivity.this);
            pDialog.setTitle("QHubb");
            pDialog.setMessage("...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            Service userFunction = new Service();
            JSONObject json = userFunction.chgPass(newpas, email);
            Log.d("Button", "Register");
            return json;
        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.getString("success") != null) {
                    textChangePasswordMessage.setText("");
                    String res = json.getString("success");
                    String red = json.getString("error");

                    if (Integer.parseInt(res) == 1) {
                        /**
                         * Dismiss the process dialog
                         **/
                        pDialog.dismiss();
                        textChangePasswordMessage.setText("Your Password is successfully changed.");


                    } else if (Integer.parseInt(red) == 2) {
                        pDialog.dismiss();
                        textChangePasswordMessage.setText("Invalid old Password.");
                    } else {
                        pDialog.dismiss();
                        textChangePasswordMessage.setText("Error occured in changing Password.");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void execute(View view){
        new ActivityTask(this, (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE),
                textChangePasswordMessage, new ProcessRegister()).execute();
    }
}
