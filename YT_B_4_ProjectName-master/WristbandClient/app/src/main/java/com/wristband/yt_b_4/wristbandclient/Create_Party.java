package com.wristband.yt_b_4.wristbandclient;

/**
 * Created by Mike on 10/7/2017.
 */

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.wristband.yt_b_4.wristbandclient.app.AppController;
import com.wristband.yt_b_4.wristbandclient.models.Party;
import com.wristband.yt_b_4.wristbandclient.models.User;
import com.wristband.yt_b_4.wristbandclient.utils.Const;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.common.api.Status;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Switch;
import android.widget.CompoundButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.util.Log;

import java.util.Calendar;

import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;

import android.telephony.SmsManager;


/**
 * Created by Jackguzzetta on 9/26/17.
 */

public class Create_Party extends AppCompatActivity {
    private static final String TAG = "Date";
    Button create;
    private TextView mDisplayDate;
    private TextView mDisplayTime;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private PlaceAutocompleteFragment autocompleteFragment;
    String name;
    String location;
    String date;
    String time;
    String locate;
    String prev_class;
    Switch swit;
    String user_id;
    String party_id;
    String created;
    String loc;
    LatLng fromadd;
    int m, d, y;
    double lat;
    double lng;
    Intent finalIntent;
    boolean s;
    public static int RESULT_LOAD_IMAGE = 1;
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";
    private ProgressDialog pDialog;

    /**
     * The screen is set with different information to fill out.  You'll have to fill out party name and location.
     * When you click on the date, a calender will be brought up for you to select the date.  When time is pressed,
     * a clock will be shown for you to simply select the time.  The time will be in military time.  A switch
     * will be available for the user to decide if it's a public party or a private party (invite only).  You are unable
     * to create a party before todays date and all fields must be filled out.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_party);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.create = (Button) findViewById(R.id.create);
        EditText eventname = (EditText) findViewById(R.id.eventName);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                fromadd = place.getLatLng();
                loc = (String) place.getAddress();
                lat = fromadd.latitude;
                lng = fromadd.longitude;
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getBaseContext(), "failure", Toast.LENGTH_LONG).show();

            }
        });
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        user_id = settings.getString("id", "default");

        swit = (Switch) findViewById(R.id.swittch);


        swit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                s = isChecked;
            }
        });
        Intent intent = getIntent();
        name = intent.getStringExtra("eventname");
        date = intent.getStringExtra("Date");
        time = intent.getStringExtra("Time");
        locate = intent.getStringExtra("loc");
        eventname.setText(name);
        mDisplayDate = (TextView) findViewById(R.id.date);
        mDisplayTime = (TextView) findViewById(R.id.time);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Create_Party.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                m = month;
                y = year;
                d = day;

                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = year + "-" + month + "-" + day;
                mDisplayDate.setText(date);
            }
        };

        mDisplayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(
                        Create_Party.this,
                        mTimeSetListener,
                        hour, minute, true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                Log.d(TAG, "onDateSet: hh:mm: " + hour + ":" + minute);

                String date = hour + ":" + minute + ":00";
                mDisplayTime.setText(date);
            }
        };

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                month += 1;
                EditText eventname = (EditText) findViewById(R.id.eventName);
                TextView Date = (TextView) findViewById(R.id.date);
                TextView Time = (TextView) findViewById(R.id.time);
                name = eventname.getText().toString();
                date = Date.getText().toString();
                time = Time.getText().toString();
                location = loc + "*" + lat + "/" + lng;
                ;

                if (name.length() == 0 || name == null || date.length() == 0 ||
                        date == null || time.length() == 0 || time == null || location.length() == 0 || location == null) {
                    Toast blank = Toast.makeText(getApplicationContext(), "Invalid! One or more fields has been left blank", Toast.LENGTH_LONG);
                    blank.show();

                }

                else if ((y<year) || (y==year && m < month) || (y==year && m == month && d<day)) {
                    Toast blank = Toast.makeText(getApplicationContext(), "Invalid! Can't create party in the past!", Toast.LENGTH_LONG);
                    blank.show();
                }

                else {
                    SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
                    String host = settings.getString("username", "default");
                    Party p = new Party(name, date, time, 0, 200, 0, host, location);
                    created = "1";
                    finalIntent = new Intent(Create_Party.this, HomeScreen.class);
                    finalIntent.putExtra("eventname", eventname.getText().toString());
                    finalIntent.putExtra("Date", Date.getText().toString());
                    finalIntent.putExtra("Time", Time.getText().toString());
                    finalIntent.putExtra("loc", location);
                    finalIntent.putExtra("activity", created);
                    finalIntent.putExtra("prev", "party");
                    //  sendSms("5554", "Hi You got a message!");
                    if (s) {
                        p.makePartyPublic();
                        sendDataToServer(p);
                        //getDataFromServer(p.getPartyName());
                        Toast blank = Toast.makeText(getApplicationContext(), "Public Party Created!", Toast.LENGTH_LONG);
                        blank.show();
                        create.setVisibility(View.GONE);
                        //startActivity(finalIntent);
                    } else {
                        p.MakePartyPrivate();
                        sendDataToServer(p);
                        //getDataFromServer(p.getPartyName());
                        Toast blank = Toast.makeText(getApplicationContext(), "Private Party Created", Toast.LENGTH_LONG);
                        blank.show();
                        create.setVisibility(View.GONE);
                        //startActivity(finalIntent);
                    }
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri SelectedImage = data.getData();
            String[] FilePathColumn = {
                    MediaStore.Images.Media.DATA
            };

            Cursor SelectedCursor = getContentResolver().query(SelectedImage, FilePathColumn, null, null, null);
            SelectedCursor.moveToFirst();

            int columnIndex = SelectedCursor.getColumnIndex(FilePathColumn[0]);
            String picturePath = SelectedCursor.getString(columnIndex);
            SelectedCursor.close();

            Toast.makeText(getApplicationContext(), picturePath, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Creates a menu in the action bar that gives you options to logout, and view your profile
     *
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_info, menu);
        return true;
    }

    /**
     * These are the cases in the menu when selected. If home is selected, it calls the function onBackPressed(),
     * if about is pressed, it will take you to the About activity, and if logout is pressed the user will be logged out
     * and returned to the login screen.
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When the back button is pressed, an alert is brought up on the screen that says "Are you sure you want
     * to leave?", "Changes will be discarded".  If yes is pressed, you will intent back to the homescreen.
     * If no the alert will closed.
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Are you sure you want to leave");
        alert.setMessage("Changes will be discarded");

        // Set an EditText view to get user input

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(Create_Party.this, HomeScreen.class);
                finish();
                startActivity(intent);

            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }


    /**
     * A JsonArrayRequest with the party name is sent to the database to get the party id.  The party id is then sent back to the server
     * to set the host relation to the party when it is created.  This creates the user/party relation.
     *
     * @param party_name
     */
    private void getDataFromServer(final String party_name) {
        new Thread(new Runnable() {
            public void run() {
                JsonArrayRequest req = new JsonArrayRequest(Const.URL_PARTY_BY_NAME + party_name,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    party_id = response.getJSONObject(0).getString("id");
                                    sendRelationToServer(user_id, party_id, "1");
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
        }).start();
    }

    /**
     * The party information that is filled out is sent to the database using a JsonObjectRequest.
     *
     * @param party
     */
    private void sendDataToServer(final Party party) {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        Const.URL_PARTY, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                getDataFromServer(party.getPartyName());
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
                    /**
                     * Passing some request headers
                     */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("party_name", party.getPartyName());
                        headers.put("date", party.getDate());
                        headers.put("time", party.getTime());
                        headers.put("privacy", Integer.toString(party.getPrivacy()));
                        headers.put("max_people", Integer.toString(party.getMaxPeople()));
                        headers.put("alerts", Integer.toString(party.getAlerts()));
                        headers.put("host", party.getHost());
                        headers.put("location", party.getLocation());
                        return headers;
                    }
                };
                AppController.getInstance().addToRequestQueue(jsonObjReq,
                        tag_json_obj);
            }
        }).start();
    }

    /**
     * The parameters of the user id, party id, and the number relation is sent to the database.  It will send a
     * 1 since the user who creates the party is the host. A 2 would be sent if they are a cohost or a 3 if guest, but
     * those will not be used in this screen.
     *
     * @param user
     * @param party_id
     * @param relation
     */
    private void sendRelationToServer(final String user, final String party_id, final String relation) {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        Const.URL_RELATION, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                startActivity(finalIntent);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
                    /**
                     * Passing some request headers
                     */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("user_id", user);
                        headers.put("party_id", party_id);
                        headers.put("relation", relation);
                        return headers;
                    }
                };
                AppController.getInstance().addToRequestQueue(jsonObjReq,
                        tag_json_obj);
                //showProgressDialog();
            }

        }).start();
    }
}
