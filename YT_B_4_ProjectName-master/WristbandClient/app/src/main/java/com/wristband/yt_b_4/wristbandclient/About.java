package com.wristband.yt_b_4.wristbandclient;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.wristband.yt_b_4.wristbandclient.app.AppController;
import com.wristband.yt_b_4.wristbandclient.app.QRGenerator;
import com.wristband.yt_b_4.wristbandclient.utils.Const;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class About extends AppCompatActivity {
    private TextView txtuser, txtid, txtfull;
    private String user_id, user_name, fname, lname, prev_class;
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";
    private ImageView code;
    private Dialog DeleteDialog;
    private Button yes, no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        txtuser = (TextView) findViewById(R.id.usertxt);
        code = (ImageView) findViewById(R.id.qr);
        txtfull = (TextView) findViewById(R.id.fullname);
        txtid = (TextView) findViewById(R.id.idtxt);
        SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        user_id = settings.getString("id", "default");
        user_name = settings.getString("username", "default");
        fname = settings.getString("f_name", "default");
        lname = settings.getString("l_name", "default");
        prev_class = getIntent().getStringExtra("prev");
        txtuser.setText("Username: " + user_name);
        txtfull.setText(fname + " " + lname);
        txtid.setText("User ID: " + user_id);
        addqr(fname, lname, user_id);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_info, menu);
        return true;
    }

    /**
     * Method initiates a drop down menu that contains different items.
     * The first case is a home button that calls the onBackPressed() that
     * sends a user to the home screen.
     * The second case is a logout button that is designed to log a user out.
     *
     * @param item
     * @return returns true if the user gets no errors
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.logout:
                LoginManager.getInstance().logOut();
                SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(this, Login.class));
                return true;
            case R.id.delete:
                deletionDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Starts a new intent that sends a user to the home screen
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(About.this, HomeScreen.class);
        startActivity(intent);
    }

    /**
     * Uses the QRGenerator app to create a qr code based off of a users
     * first name, last name, and user id. The code is generated as a bitmap
     * that can then be used in an image view.
     *
     * @param f_name
     * @param l_name
     * @param user_id
     */
    private void addqr(String f_name, String l_name, String user_id) {
        QRGenerator x = new QRGenerator(f_name + "-" + l_name + "...");
        code.setImageBitmap(x.createQR());
    }
    /**
     * A dialog that is brought up when trying to delete their account.  If yes is pressed, deleteAccount() will
     * be called and the window will close.  If no is pressed, the dialog will just close and nothing will happen.
     */
    public void deletionDialog() {
        final SharedPreferences[] settings = new SharedPreferences[1];
        final SharedPreferences.Editor[] editor = new SharedPreferences.Editor[1];
        DeleteDialog = new Dialog(About.this);
        DeleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DeleteDialog.setContentView(R.layout.delete_dialog);
        DeleteDialog.setTitle("Are you sure?");

        yes = (Button) DeleteDialog.findViewById(R.id.yes);
        no = (Button) DeleteDialog.findViewById(R.id.no);

        yes.setEnabled(true);
        no.setEnabled(true);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount(user_id);
                LoginManager.getInstance().logOut();
                settings[0] = getSharedPreferences("account", Context.MODE_PRIVATE);
                editor[0] = settings[0].edit();
                editor[0].clear();
                editor[0].commit();
                startActivity(new Intent(About.this, Login.class));
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteDialog.cancel();
            }
        });

        DeleteDialog.show();
    }
    /**
     * If user decides to delete their account, a JsonObjectRequest containing user id will be sent to
     * server to perform that.
     *
     * @param user_id
     */
    private void deleteAccount(final String user_id) {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                        Const.URL_USERS + "/" + user_id, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
                    /**
                     * Passing some request headers
                     * */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };
                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(jsonObjReq,
                        tag_json_obj);
                // Cancelling request
                // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
            }
        }).start();
    }

}
