package com.wristband.yt_b_4.wristbandclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.facebook.login.LoginManager;
import com.wristband.yt_b_4.wristbandclient.R;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wristband.yt_b_4.wristbandclient.app.AppController;
import com.wristband.yt_b_4.wristbandclient.models.User;
import com.wristband.yt_b_4.wristbandclient.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Create_Profile extends AppCompatActivity {
    private String TAG = Create_Profile.class.getSimpleName();
    private User user;
    private EditText first_name, last_name, user_name, user_password, user_email, user_reenter;
    private Button btnCreate;
    private TextView msgStatus;
    private ProgressDialog pDialog;
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__profile);
        initializeControls();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.loginmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.about:
                //startActivity(new Intent(this, About.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeControls() {
        first_name = (EditText) findViewById(R.id.fname);
        last_name = (EditText) findViewById(R.id.lname);
        user_email = (EditText) findViewById(R.id.email);
        user_name = (EditText) findViewById(R.id.username);
        user_password = (EditText) findViewById(R.id.password);
        user_reenter = (EditText) findViewById(R.id.reenter);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        msgStatus = (TextView) findViewById(R.id.msgResponse);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new user with values from the EditTexts
                createProfile(view);
            }
        });

    }

    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    private void goBack(View view) {
        Intent intent = new Intent(Create_Profile.this, Login.class);
        startActivity(intent);
    }

    private void createProfile(View view) {

        //text in first name box
        String f_name = first_name.getText().toString();
        //text in last name box
        String l_name = last_name.getText().toString();
        //text in email box
        String email = user_email.getText().toString();
        //text in username box
        String username = user_name.getText().toString();
        //text in password box
        String password = user_password.getText().toString();
        //text in reenter box
        String reenter = user_reenter.getText().toString();
        user = new User(first_name.getText().toString(), last_name.getText().toString(), user_name.getText().toString(), user_password.getText().toString(), user_email.getText().toString(), user_reenter.getText().toString());

        /*check to see that the user has entered text in all boxes,
        give toast error message if text is missing
        if all boxes contain valid answers, add the user to the Users table and take user to
        the home screen (perhaps add toast "profile created" on home screen
         */
        //check for missing answers
        if (f_name.isEmpty() || l_name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || reenter.isEmpty()) {
            Toast fail = Toast.makeText(getApplicationContext(), "Required information missing", Toast.LENGTH_LONG);
            fail.show();
        } else if (password.equals(reenter) == false) {
            Toast fail = Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG);
            fail.show();
        } else {
            //First check if user already exists in db or not
            checkIfUserExists(user);
        }

    }

    private void finishProfile(final User user) {

    }

    private void checkIfUserExists(final User user) {
        new Thread(new Runnable() {
            public void run() {
                JsonArrayRequest req = new JsonArrayRequest(Const.URL_USER_BY_NAME + user.getUsername(),
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    String db_username = response.getJSONObject(0).getString("users");
                                    if ("exists".equals(db_username)) {
                                        Toast fail = Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_LONG);
                                        fail.show();
                                    } else {
                                        sendDataToServer(user);
                                    }
                                } catch (JSONException e) {
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendDataToServer(user);
                    }
                });
                AppController.getInstance().addToRequestQueue(req,
                        tag_json_arry);
            }
        }).start();
    }

    private void sendDataToServer(final User user) {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        Const.URL_USERS, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String username = null;
                                try {
                                    username = response.getString("user");
                                    String id = response.getString("id");
                                    SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    //editor.putString("token", responseToken);
                                    editor.putString("username", username);
                                    editor.putString("id", id);
                                    editor.commit();
                                    Toast pass = Toast.makeText(getApplicationContext(), "Welcome: " + user.getUsername(), Toast.LENGTH_LONG);
                                    pass.show();
                                    Intent intent = new Intent(Create_Profile.this, HomeScreen.class);
                                    startActivity(intent);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        msgStatus.setText("Error creating account: " + error);
                    }
                }) {
                    /**
                     * Passing some request headers
                     * */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("f_name", user.getFirstName());
                        headers.put("l_name", user.getLastName());
                        headers.put("username", user.getUsername());
                        headers.put("password", user.getPassword());
                        headers.put("email", user.getEmail());
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


    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
        Intent intent = new Intent(Create_Profile.this, Login.class);
        startActivity(intent);
    }
}