package com.qhubb.qhubb;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.TextView;

import com.qhubb.qhubb.R;
import com.qhubb.qhubb.network.ActivityTask;
import com.qhubb.qhubb.util.Service;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.content.DialogInterface;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button btnLogout;
    private Button btnChangePassword;
    private String email;
    private String deactive;

    private Button btnDeactivate;
    private TextView textDeactivateMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        btnDeactivate = (Button) findViewById(R.id.btnDeactivate);
        textDeactivateMessage = (TextView) findViewById(R.id.textDeactivateMessage);

        email = (String) getIntent().getSerializableExtra("email");
        deactive = (String) getIntent().getSerializableExtra("deactive");

        if ("1".equals(deactive)){
            btnDeactivate.setText("Activate");
            btnChangePassword.setEnabled(false);
        }

        /**
         * Change Password Activity Started
         **/
        btnChangePassword.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                Intent chgpass = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                chgpass.putExtra("email", email);
                startActivity(chgpass);
            }

        });

        btnDeactivate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                final View view1 = view;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);

                String prompt = "";
//                Toast.makeText(getApplicationContext(),
//                        "deactive: " + deactive, Toast.LENGTH_SHORT).show();
                if ("1".equals(deactive)){
                    // set title
                    alertDialogBuilder.setTitle("Activation");
                    prompt = "Do you want to activate the account?";
                }else{
                    // set title
                    alertDialogBuilder.setTitle("Deactivation");
                    prompt = "Do you want to deactivate the account?";
                }


                // set dialog message
                alertDialogBuilder
                        .setMessage(prompt)
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                execute(view1);
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


            }

        });

        /**
         *Logout from the User Panel which clears the data in Sqlite database
         **/
        btnLogout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Service logout = new Service();
                logout.logoutUser(getApplicationContext());
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);

                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void execute(View view){
        new ActivityTask(this, (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE),
                textDeactivateMessage, new ProcessDeactivate()).execute();
    }

    private class ProcessDeactivate extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("QHubb");
            pDialog.setMessage("...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            Service userFunction = new Service();
            if ("1".equals(deactive)){
                JSONObject json = userFunction.activateAccount(email);
                return json;
            }else {
                JSONObject json = userFunction.deactivateAccount(email);
                return json;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString("success") != null) {

                    String res = json.getString("success");

                    if(Integer.parseInt(res) == 1){
                        if ("1".equals(deactive)){
                            pDialog.setMessage("Activating...");
                            pDialog.setTitle("Activate");
                            btnChangePassword.setEnabled(true);

                            deactive = "0";
                            btnDeactivate.setText("DeActivate");
                        }else {
                            pDialog.setMessage("Deactivating...");
                            pDialog.setTitle("Deactivate");
                            btnChangePassword.setEnabled(false);
                            deactive = "1";
                            btnDeactivate.setText("Activate");
                        }
                        /**
                         *If JSON array details are stored in SQlite it launches the User Panel.
                         *
                        Intent upanel = new Intent(getApplicationContext(), LoginActivity.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pDialog.dismiss();
                        startActivity(upanel);*/
                        /**
                         * Close Login Screen
                         *
                        finish();*/
                        pDialog.dismiss();
                    }else{

                        pDialog.dismiss();
                        if ("1".equals(deactive)){
                            textDeactivateMessage.setText("Cannot activate account");
                        }else{
                            textDeactivateMessage.setText("Cannot deactivate account");
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
