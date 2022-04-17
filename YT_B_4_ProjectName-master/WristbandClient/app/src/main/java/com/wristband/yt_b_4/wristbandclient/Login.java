package com.wristband.yt_b_4.wristbandclient;

/**
 * Created by Mike on 9/23/2017.
 */

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.wristband.yt_b_4.wristbandclient.app.AppController;
import com.wristband.yt_b_4.wristbandclient.examples.exampleActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.wristband.yt_b_4.wristbandclient.models.User;
import com.wristband.yt_b_4.wristbandclient.utils.Const;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Login extends AppCompatActivity {
    TextView txtStatus;
    LoginButton FacebookLoginButton;
    Button LoginButton;
    Button RegisterButton;
    private EditText user_name, user_password;
    private User user;
    private ProgressDialog pDialog;
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    CallbackManager callbackManager;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        context = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
        initializeControls();
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        if (isLoggedIn()) {
            Intent intent = new Intent(Login.this, HomeScreen.class);
            startActivity(intent);
        }
        loginWithFB();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.loginmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                //startActivity(new Intent(this, About.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeControls() {
        callbackManager = CallbackManager.Factory.create();
        txtStatus = (TextView) findViewById(R.id.txtstatus);
        txtStatus.setText("Please Log In");
        FacebookLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        LoginButton = (Button) findViewById(R.id.btnLogin);
        user_name = (EditText) findViewById(R.id.username);
        user_password = (EditText) findViewById(R.id.password);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        RegisterButton = (Button) findViewById(R.id.register_button);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                loginProfile();
            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                createProfile();
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

    /**
     * Using Facebook api, user can login with facebook.  If successful, the client will save the first, last name, user id,
     * and email of the user for further use.
     */
    private void loginWithFB() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String f_name = object.getString("first_name");
                                    String l_name = object.getString("last_name");
                                    String fb_id = object.getString("id");
                                    String email = null;
                                    if (object.has("email"))
                                        email = object.getString("email");
                                    user = new User(f_name, l_name, fb_id, null, email);
                                    checkIfUserExists(user);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast toast = Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email");
                request.setParameters(parameters);
                request.executeAsync();

                //Intent intent = new Intent(Login.this, HomeScreen.class);
                //startActivity(intent);
            }

            @Override
            public void onCancel() {
                txtStatus.setText("Login Cancelled\n");
            }

            @Override
            public void onError(FacebookException error) {
                txtStatus.setText("Error: " + error.getMessage());
            }
        });
    }

    /**
     * Shared Preferences will save user data for further use.  Method will return that username and
     * Access Token from logging in are not null.
     *
     * @return
     */
    public boolean isLoggedIn() {
        SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        String username = settings.getString("username", null);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return ((accessToken != null) || (username != null));
    }

    /**
     * User will be taken to create profile screen.
     */
    private void createProfile() {
        Intent intent = new Intent(Login.this, Create_Profile.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Used to get hash key for facebook API (only needed if creating a new application or releasing publicly)
     */
    private void PrintFBHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.wristband.yt_b_4.wristbandclient", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                //msgResponse.setText("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * If all fields are filled out and successful, the data will be sent to server, if false, a toast will come up
     * displaying an error.
     */
    private void loginProfile() {
        Toast fail;
        //text in username box
        String username = user_name.getText().toString();
        //text in password box
        String password = user_password.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            fail = Toast.makeText(getApplicationContext(), "Required information missing", Toast.LENGTH_LONG);
            fail.show();
        } else {
            sendDataToServer(username, password);
        }

    }

    /**
     * If user creates a new profile, that data will be sent to the server using a JsonObjectRequest.
     * The user will then be taken to the HomeScreen.
     *
     * @param username
     * @param password
     */
    private void sendDataToServer(final String username, final String password) {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        Const.URL_USERS + "/login", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast toast;
                                try {
                                    String responseToken = response.getString("token");
                                    String username = response.getString("user");
                                    String id = response.getString("id");
                                    String f_name = response.getString("f_name");
                                    String l_name = response.getString("l_name");

                                    toast = Toast.makeText(getApplicationContext(), "Welcome: " + username, Toast.LENGTH_LONG);
                                    toast.show();
                                    //stores user and token into encrypted storage accessible across activities
                                    SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("token", responseToken);
                                    editor.putString("username", username);
                                    editor.putString("id", id);
                                    editor.putString("f_name", f_name);
                                    editor.putString("l_name", l_name);

                                    editor.commit();

                                    //to get the stored token and username
                                    //SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
                                    //String myString = settings.getString("username", "default");

                                    Intent intent = new Intent(Login.this, HomeScreen.class);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    toast = Toast.makeText(getApplicationContext(), "Invalid Login Credentials", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Invalid Login Credentials", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }) {
                    /**
                     * Passing some request headers
                     * */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("username", username);
                        headers.put("password", password);
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

    /**
     * Username is sent to server to check if it already exists.  If username is not found, it will be create a new profile.
     * If username is exists, user will be logged in and sent to home screen.
     *
     * @param user
     */
    private void checkIfUserExists(final User user) {
        new Thread(new Runnable() {
            public void run() {
                JsonArrayRequest req = new JsonArrayRequest(Const.URL_USER_BY_NAME + user.getUsername(),
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    String retVal = response.getJSONObject(0).getString("users");
                                    if (!retVal.equals("Not found")) {
                                        String username = response.getJSONObject(0).getString("username");
                                        String id = response.getJSONObject(0).getString("id");
                                        String responseToken = response.getJSONObject(0).getString("token");
                                        SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("token", responseToken);
                                        editor.putString("username", username);
                                        editor.putString("id", id);
                                        editor.commit();
                                        Intent intent = new Intent(Login.this, HomeScreen.class);
                                        startActivity(intent);
                                    } else {
                                        makeProfile(user);
                                    }
                                } catch (JSONException e) {
                                    txtStatus.setText("Error " + e);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txtStatus.setText("Error " + error);

                    }
                });
                AppController.getInstance().addToRequestQueue(req,
                        tag_json_arry);
            }
        }).start();
    }

    /**
     * After all fields are filled out and user creates profile, that information will be sent to server
     * to be stored in the database.
     *
     * @param user
     */
    private void makeProfile(final User user) {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        Const.URL_USERS, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast pass = Toast.makeText(getApplicationContext(), "Welcome: " + user.getFirstName(), Toast.LENGTH_LONG);
                                //pass.show();

                                String responseToken = null;
                                try {
                                    responseToken = response.getString("token");
                                    String username = response.getString("user");
                                    String id = response.getString("id");
                                    SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("token", responseToken);
                                    editor.putString("username", username);
                                    editor.putString("id", id);
                                    editor.commit();
                                    pass = Toast.makeText(getApplicationContext(), "Welcome: " + username, Toast.LENGTH_LONG);
                                    pass.show();
                                } catch (JSONException e) {
                                    pass = Toast.makeText(getApplicationContext(), "error: ", Toast.LENGTH_LONG);
                                    pass.show();
                                }
                                //stores user and token into encrypted storage accessible across activities

                                editor.commit();
                                Intent intent = new Intent(Login.this, HomeScreen.class);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //msgStatus.setText("Error creating account: " + error);
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
}
