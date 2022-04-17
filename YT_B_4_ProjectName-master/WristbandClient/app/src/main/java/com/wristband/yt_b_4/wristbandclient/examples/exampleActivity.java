package com.wristband.yt_b_4.wristbandclient.examples;

/**
 * Created by Mike on 10/6/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wristband.yt_b_4.wristbandclient.R;
import com.wristband.yt_b_4.wristbandclient.app.AppController;
import com.wristband.yt_b_4.wristbandclient.models.User;
import com.wristband.yt_b_4.wristbandclient.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class exampleActivity extends AppCompatActivity {
    private TextView msgStatus, getUserStatus;
    private EditText f_name, l_name, username, password, email, getUser;
    private Button createAccountBtn, getUserBtn;
    private User user;
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        initializeControls();
        //write login key
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("key", "123");
        editor.commit();

//        SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
//        String myString = settings.getString("username", "default");
        //read login key
        String highScore = sharedPref.getString("key", "default");
        msgStatus.setText(highScore);
    }

    private void initializeControls() {
        //initialize variables
        f_name = (EditText) findViewById(R.id.edit_firstName);
        l_name = (EditText) findViewById(R.id.edit_lastName);
        username = (EditText) findViewById(R.id.edit_Username);
        password = (EditText) findViewById(R.id.edit_Password);
        email = (EditText) findViewById(R.id.edit_Email);
        createAccountBtn = (Button) findViewById(R.id.button_createAccount);
        msgStatus = (TextView) findViewById(R.id.textView);

        getUser = (EditText) findViewById(R.id.edit_getUser);
        getUserBtn = (Button) findViewById(R.id.button_getUser);
        getUserStatus = (TextView) findViewById(R.id.getUserText);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        //wait for button to be clicked
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new user with values from the EditTexts
                user = new User(f_name.getText().toString(), l_name.getText().toString(), username.getText().toString(), password.getText().toString(), email.getText().toString());
                sendDataToServer(user);
            }
        });

        getUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new user with values from the EditTexts
                int id;
                try {
                    id = Integer.parseInt(getUser.getText().toString());
                    getDataFromServer(id);
                } catch (NumberFormatException e) {
                    getUserStatus.setText("Invalid ID, not a string");
                }
            }
        });
    }

    private void sendDataToServer(final User user) {
        showProgressDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Const.URL_USERS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            msgStatus.setText("Account : " + response.getString("users") + " created.");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                msgStatus.setText("Error creating account: " + error);
                hideProgressDialog();
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

    private void getDataFromServer(final int id) {
        showProgressDialog();
        JsonArrayRequest req = new JsonArrayRequest(Const.URL_USERS + "/" + id,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String newString = "User extracted from Json\n";
                            newString += response.getJSONObject(0).getString("id");
                            newString += "\n";
                            newString += response.getJSONObject(0).getString("f_name");
                            newString += "\n";
                            newString += response.getJSONObject(0).getString("l_name");
                            newString += "\n";
                            newString += response.getJSONObject(0).getString("username");
                            newString += "\n";
                            newString += response.getJSONObject(0).getString("password");
                            newString += "\n";
                            newString += response.getJSONObject(0).getString("email");
                            getUserStatus.setText(newString);
                        } catch (JSONException e) {
                            getUserStatus.setText("Error: " + e);
                        }

                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getUserStatus.setText("error: " + error.getMessage());
                hideProgressDialog();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req,
                tag_json_arry);
        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_arry);
    }

    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }
}