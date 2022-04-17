package com.wristband.yt_b_4.wristbandclient;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.wristband.yt_b_4.wristbandclient.app.AppController;
import com.wristband.yt_b_4.wristbandclient.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.wristband.yt_b_4.wristbandclient.utils.VolleyHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuestScreen extends AppCompatActivity {
    private Button btnCohost, btnBack, btnLocation, btnPhotos, btnComments;
    private TextView dateText, partyText, locationTxt, timeTxt;
    private ProgressDialog pDialog;
    private ArrayList<String> usernames = new ArrayList<String>();
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";
    private String party_id, user_id;
    private String prevScreen, mnus;
    private String party_name, user_name, relation, prev_class, loc, priv, dat, time, maxp, alert, hosts;
    final Context context = this;
    ListView listView;
    List list = new ArrayList();
    List idList = new ArrayList();
    ArrayAdapter adapter;
    List relationList = new ArrayList();

    /**
     * sets onClickListeners for all buttons.  Changes list view items' colors depending on their relation to the
     * party.  If a user is clicked in the list view, you will be taken to the user info screen of that user that displays
     * their information.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_screen);
        btnLocation = (Button) findViewById(R.id.button7);
        btnComments = (Button) findViewById(R.id.button6);
        partyText = (TextView) findViewById(R.id.partyTxt);
        locationTxt = (TextView) findViewById(R.id.location);
        dateText = (TextView) findViewById(R.id.dateTxt);
        SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        user_id = settings.getString("id", "default");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        party_name = getIntent().getStringExtra("party_name");
        relation = getIntent().getStringExtra("relation");
        party_id = getIntent().getStringExtra("party_id");
        prevScreen = getIntent().getStringExtra("previous");
        mnus = getIntent().getStringExtra("menu");

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new user with values from the EditTexts
                goLocation(view);
            }

        });

        

        btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new user with values from the EditTexts
                goComments(view);
            }
        });
        Intent intent = getIntent();
        prev_class = intent.getStringExtra("prev");

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter(GuestScreen.this, android.R.layout.simple_list_item_1, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position, convertView, parent);
                if (relationList.get(position).equals("1")) {
                    // Set a background color for ListView regular row/item
                    view.setBackgroundColor(Color.parseColor("#19c482"));
                } else if (relationList.get(position).equals("2")) {
                    // Set the background color for alternate row/item
                    view.setBackgroundColor(Color.parseColor("#a6abae"));
                } else if (relationList.get(position).equals("3")) {
                    view.setBackgroundColor(Color.parseColor("#326f93"));
                } else {
                    view.setBackgroundColor(Color.RED);
                }
                return view;
            }
        };

        listView.setAdapter(adapter);
        getDataFromServer();
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                Intent intent = new Intent(GuestScreen.this, User_Info.class);
                user_name = (listView.getItemAtPosition(i)).toString();
                intent.putExtra("user_rel", relation);
                relation = relationList.get(i).toString();
                intent.putExtra("prev", "guest");
                intent.putExtra("menu", mnus);
                intent.putExtra("user_id", idList.get(i).toString());
                intent.putExtra("user_name", user_name);
                intent.putExtra("party_name", party_name);
                intent.putExtra("party_id", party_id);
                intent.putExtra("relation", relation);
                startActivity(intent);
            }
        });

    }

    /**
     * Creates a menu in the action bar that gives you options to logout, delete the party or remove yourself
     * from party, and view your profile
     *
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (mnus.equals("user_info")) {
            inflater.inflate(R.menu.user_info, menu);
        } else {
            inflater.inflate(R.menu.publicmenu, menu);
        }
        return true;
    }

    /**
     * These are the cases in the menu when selected. If home is selected, it calls the function onBackPressed(),
     * if about is pressed, it will take you to the About activity, and if logout is pressed the user will be logged out
     * and returned to the login screen.  If delete is selected you remove yourself from that party.
     *
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.about:
                startActivity(new Intent(this, About.class));
                return true;
            case R.id.logout:
                LoginManager.getInstance().logOut();
                SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(this, Login.class));
                return true;
            case R.id.join:
                VolleyHandler.inviteUser(party_id, user_id, "2");
                finish();
                startActivity(getIntent());
                getDataFromServer();
                return true;
            case R.id.delete:
                deleteUser();
                startActivity(new Intent(GuestScreen.this, HomeScreen.class));
                return true;
            case R.id.leave:
                goRemove();
                startActivity(new Intent(GuestScreen.this, HomeScreen.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * The user will be brought back to the Home Screen
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GuestScreen.this, HomeScreen.class);
        startActivity(intent);
    }

    /**
     * User will leave the current party and will have to be invited (if private) to get back in.
     */
    private void deleteUser() {
        if (relation.equals("1")) {
            deleteParty(party_id);
        } else if (relation.equals("2") || relation.equals("3")) {
            deleteRelation(user_id, relation);
        } else {
            //error
        }
    }

    /**
     * A JsonObjectRequest is sent to the server and will delete the relation between a user and
     * party.  This simply removes a user from a party.
     *
     * @param user_id
     * @param party_id
     */
    private void deleteRelation(final String user_id, final String party_id) {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                        Const.URL_RELATION, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
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
                        headers.put("user_id", user_id);
                        headers.put("party_id", party_id);
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
     * A JsonObjectRequest with the party id is sent to the server and deletes the current party.  This can only be done if
     * the host decides to delete party.
     *
     * @param party_id
     */
    private void deleteParty(final String party_id) {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                        Const.URL_PARTY + party_id, null,
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
                        //headers.put("party_id", party_id);
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
     * A JsonArrayRequest with the party name is sent to the server and grabs all of the party information
     * and is displayed on the guest screen.  The date and time will be parsed into standard for, e.g. "November 29, 2017
     * at 5:00 pm".
     */
    private void getDataFromServer() {
        new Thread(new Runnable() {
            public void run() {
                JsonArrayRequest req = new JsonArrayRequest(Const.URL_PARTY_BY_NAME + party_name,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    String address = "";
                                    String name = response.getJSONObject(0).getString("party_name");
                                    dat = response.getJSONObject(0).getString("date");
                                    String host = response.getJSONObject(0).getString("host");
                                    time = response.getJSONObject(0).getString("time");
                                    String location = response.getJSONObject(0).getString("location");
                                    party_id = response.getJSONObject(0).getString("id");
                                    priv = response.getJSONObject(0).getString("privacy");
                                    loc = location;
                                    maxp = response.getJSONObject(0).getString("max_people");
                                    alert = response.getJSONObject(0).getString("alerts");
                                    hosts = host;
                                    String[] dates = dat.split("-");
                                    String month = "";
                                    if (dates[1].equals("1")) {
                                        month = "January";
                                    } else if (dates[1].equals("2")) {
                                        month = "Febuary";
                                    } else if (dates[1].equals("3")) {
                                        month = "March";
                                    } else if (dates[1].equals("4")) {
                                        month = "April";
                                    } else if (dates[1].equals("5")) {
                                        month = "May";
                                    } else if (dates[1].equals("6")) {
                                        month = "June";
                                    } else if (dates[1].equals("7")) {
                                        month = "July";
                                    } else if (dates[1].equals("8")) {
                                        month = "August";
                                    } else if (dates[1].equals("9")) {
                                        month = "September";
                                    } else if (dates[1].equals("10")) {
                                        month = "October";
                                    } else if (dates[1].equals("11")) {
                                        month = "November";
                                    } else if (dates[1].equals("12")) {
                                        month = "December";
                                    }
                                    String day = "am";
                                    String[] times = time.split(":");
                                    String newTime = times[0];
                                    if (times[0].equals("0")) {
                                        newTime = "12";
                                        day = "pm";
                                    } else if (times[0].equals("12")) {
                                        day = "pm";
                                    } else if (times[0].equals("13")) {
                                        newTime = "1";
                                        day = "pm";
                                    } else if (times[0].equals("14")) {
                                        newTime = "2";
                                        day = "pm";
                                    } else if (times[0].equals("15")) {
                                        newTime = "3";
                                        day = "pm";
                                    } else if (times[0].equals("16")) {
                                        newTime = "4";
                                        day = "pm";
                                    } else if (times[0].equals("17")) {
                                        newTime = "5";
                                        day = "pm";
                                    } else if (times[0].equals("18")) {
                                        newTime = "6";
                                        day = "pm";
                                    } else if (times[0].equals("19")) {
                                        newTime = "7";
                                        day = "pm";
                                    } else if (times[0].equals("20")) {
                                        newTime = "8";
                                        day = "pm";
                                    } else if (times[0].equals("21")) {
                                        newTime = "9";
                                        day = "pm";
                                    } else if (times[0].equals("22")) {
                                        newTime = "10";
                                        day = "pm";
                                    } else if (times[0].equals("23")) {
                                        newTime = "11";
                                        day = "pm";
                                    }
                                    for (char c : location.toCharArray()) {
                                        if (c != '*') {
                                            address += c;
                                        } else {
                                            break;
                                        }

                                    }
                                    partyText.setText(name);
                                    dateText.setText(month + " " + dates[2] + ", " + dates[0] + " at " + newTime + ":" + times[1] + " " + day);
                                    locationTxt.setText("Location: " + address);
                                    getAllUsers();
                                } catch (JSONException e) {
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                AppController.getInstance().addToRequestQueue(req,
                        tag_json_arry);
                // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_arry);

            }
        }).start();
    }

    /**
     * When "View in maps" button is clicked, this will take you to the MapsActivity screen which
     * shows the party location in Google Maps.
     *
     * @param view
     */
    private void goLocation(View view) {
        Intent intent = new Intent(GuestScreen.this, MapsActivity.class);
        intent.putExtra("party_location", loc);
        intent.putExtra("party_id", party_id);
        intent.putExtra("username", user_name);
        intent.putExtra("relation", relation);
        intent.putExtra("party_name", party_name);
        intent.putExtra("menu", mnus);
        intent.putExtra("prev", "guest");
        finish();
        startActivity(intent);
    }



    /**
     * User will be taken to the Comments screen when the comments button is pressed.
     *
     * @param view
     */
    private void goComments(View view) {
        Intent intent = new Intent(GuestScreen.this, Comments.class);
        intent.putExtra("party_id", party_id);
        intent.putExtra("username", user_name);
        intent.putExtra("relation", relation);
        intent.putExtra("party_name", party_name);
        intent.putExtra("menu", mnus);
        intent.putExtra("prev", "guest");
        startActivity(intent);
    }


    /**
     * A JsonArrayRequest with the party id is sent to the server that grabs all of the users invited to the party and
     * displayed in the list view.
     */
    private void getAllUsers() {
        JsonArrayRequest req = new JsonArrayRequest(Const.URL_JOIN_Party + party_id,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                String Relation = response.getJSONObject(i).getString("party_user_relation");
                                relationList.add(Relation);
                                String name = response.getJSONObject(i).getString("f_name");
                                name += " ";
                                name += response.getJSONObject(i).getString("l_name");
                                list.add(name);
                                adapter.notifyDataSetChanged();
                                usernames.add(response.getJSONObject(i).getString("user_id"));
                                idList.add(response.getJSONObject(i).getString("id"));

                            }
                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        AppController.getInstance().addToRequestQueue(req,
                tag_json_arry);
    }

    private void goRemove() {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                        Const.URL_RELATION, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

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
                        headers.put("user_id", user_id);
                        headers.put("party_id", party_id);
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