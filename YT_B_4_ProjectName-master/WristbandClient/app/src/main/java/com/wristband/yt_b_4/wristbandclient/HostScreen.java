package com.wristband.yt_b_4.wristbandclient;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.google.zxing.qrcode.QRCodeReader;
import com.wristband.yt_b_4.wristbandclient.app.AppController;
import com.wristband.yt_b_4.wristbandclient.utils.Const;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.Result;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.graphics.drawable.ColorDrawable;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Calendar;

import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.content.DialogInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Paint;
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
import android.util.Log;
//import me.dm7.barcodescanner.zxing.ZXingScannerView;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.wristband.yt_b_4.wristbandclient.utils.VolleyHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostScreen extends AppCompatActivity {
    private static final String TAG = "Date";
    private Button scanbtn, btnCohost, btnPhotos, btnComments;
    private TextView dateText, partyText, locationTxt, timeTxt, btnLocation;
    private ArrayList<String> usernames = new ArrayList<String>();
    private ArrayList<String> unames = new ArrayList<String>();

    int lad;
    int lng;

    private ProgressDialog pDialog;
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";
    private String party_id, user_id, uname;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private static final int REQUEST_CAMERA = 1;
    // private ZXingScannerView mScannerView;


    String party_name, user_name, relation, prev_class, priv, dat, time, loc, maxp, alert, hosts;
    final Context context = this;
    ListView listView;
    List list = new ArrayList();
    List idList = new ArrayList();
    List scannedList = new ArrayList();

    ArrayAdapter adapter;
    List relationList = new ArrayList();

    /**
     * sets onClickListeners for all buttons.  Changes list view items' colors depending on their relation to the
     * party.  If a user is clicked in the list view, you will be taken to the user info screen of that user that displays
     * their information.  Time and date selectors are also introduced if the host decides to edit the party.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_screen);
        btnLocation = (TextView) findViewById(R.id.button7);
        btnComments = (Button) findViewById(R.id.button6);
        scanbtn = (Button) findViewById(R.id.scan_button);
        partyText = (TextView) findViewById(R.id.partyTxt);
        locationTxt = (TextView) findViewById(R.id.location);
        dateText = (TextView) findViewById(R.id.dateTxt);

        //mScannerView = new ZXingScannerView(this);
        SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        user_id = settings.getString("id", "default");
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        party_name = getIntent().getStringExtra("party_name");
        relation = getIntent().getStringExtra("relation");
        //Toast.makeText(getApplicationContext(), unames.get(0), Toast.LENGTH_LONG).show();


//                QRGenerator(party_id,user_id);


        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new user with values from the EditTexts
                scanNow(view);
            }

        });


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
                goComments(view);
                //create a new user with values from the EditTexts
            }
        });
        Intent intent = getIntent();
        prev_class = intent.getStringExtra("prev");


        listView = (ListView) findViewById(R.id.list_view);
        getDataFromServer();


        adapter = new ArrayAdapter(HostScreen.this, android.R.layout.simple_list_item_1, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position, convertView, parent);
                    if (relationList.get(position).equals("1")) {
                        if (scannedList.get(position).equals("1")) {
                            view.setBackgroundColor(Color.RED);
                        }
                        else {
                            // Set a background color for ListView regular row/item
                            view.setBackgroundColor(Color.parseColor("#19c482"));
                        }
                    } else if (relationList.get(position).equals("2")) {
                        if (scannedList.get(position).equals("1")) {
                            view.setBackgroundColor(Color.RED);
                        }
                        else {
                            // Set the background color for alternate row/item
                            view.setBackgroundColor(Color.parseColor("#a6abae"));
                        }
                    } else if (relationList.get(position).equals("3")) {
                        if (scannedList.get(position).equals("1")) {
                            view.setBackgroundColor(Color.RED);
                        }
                        else {
                            view.setBackgroundColor(Color.parseColor("#326f93"));
                        }
                    }
                    else {
                        view.setBackgroundColor(Color.CYAN);
                    }
                return view;
            }
        };
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                Intent intent = new Intent(HostScreen.this, User_Info.class);
                user_name = (listView.getItemAtPosition(i)).toString();
                intent.putExtra("user_rel", relation);
                relation = relationList.get(i).toString();
                intent.putExtra("prev", "host");
                intent.putExtra("user_id", idList.get(i).toString());
                intent.putExtra("party_id", party_id);
                intent.putExtra("party_name", party_name);
                intent.putExtra("user_name", user_name);
                intent.putExtra("username", uname);

                intent.putExtra("relation", relation);
                startActivity(intent);
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = year + "-" + month + "-" + day;
                dat = date;
            }
        };

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                Log.d(TAG, "onDateSet: hh:mm: " + hour + ":" + minute);

                String t = hour + ":" + minute + ":00";
                time = t;
            }
        };

    }

    /**
     * If scan is pressed, the scanner screen will be brought up to scan the guests' QR code.
     *
     * @param view
     */
    public void scanNow(View view) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    /**
     * After QR code is scanned, a toast will be printed that displays whether the user is in the party
     * or not, or if there was an error in the scan, in which "No scan data received!" will display.
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanned[] = new String[2];
            String scanContent = scanningResult.getContents();
            scanned = scanContent.split("-");
            Log.v("die", scanContent);
            if (scanned.length == 2) {
                String f_name = scanned[0];
                String l_name = scanned[1].substring(0, scanned[1].length() - 3);

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Scanned: " + f_name + " " + l_name, Toast.LENGTH_SHORT);
                toast.show();
                getUserIDByFullName(f_name, l_name, party_id);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Invalid Barcode", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
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
        if (relation.toString().equals("3"))
            inflater.inflate(R.menu.cohost, menu);
        else
            inflater.inflate(R.menu.guestmenu, menu);
        return true;
    }

    /**
     * These are the cases in the menu when selected. If home is selected, it calls the function onBackPressed(),
     * if about is pressed, it will take you to the About activity, and if logout is pressed the user will be logged out
     * and returned to the login screen.  If delete is selected, depending on your relation to the party, either the party itself
     * will be deleted.  There are options to edit the name, date or time of the party also.
     *
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if (relation.toString().equals("3")) {
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
                case R.id.delete:
                    deleteUser();
                    startActivity(new Intent(HostScreen.this, HomeScreen.class));
                    return true;
                case R.id.invite:
                    intent = new Intent(HostScreen.this, Add_User.class);
                    intent.putExtra("prev", "guest");
                    intent.putExtra("party_id", party_id);
                    intent.putExtra("party_name", party_name);
                    intent.putExtra("relation", relation);
                    startActivity(intent);
                    return true;
                case R.id.leave:
                    goRemove();
                    startActivity(new Intent(HostScreen.this, HomeScreen.class));
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } else {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    return true;
                case R.id.edit:
                    //TODO
                    return true;
                case R.id.newname:
                    editname();
                    getDataFromServer();
                    return true;
                case R.id.invite:
                    intent = new Intent(HostScreen.this, Add_User.class);
                    intent.putExtra("prev", "guest");
                    intent.putExtra("party_id", party_id);
                    intent.putExtra("party_name", party_name);
                    intent.putExtra("relation", relation);
                    startActivity(intent);
                    return true;
                case R.id.newdate:
                    editdate();
                    getDataFromServer();
                    return true;
                case R.id.newtime:
                    edittime();
                    getDataFromServer();
                    return true;
                case R.id.newlocation:
                    editlocation();
                    getDataFromServer();
                    return true;
                case R.id.delete:
                    deleteUser();
                    intent = new Intent(HostScreen.this, HomeScreen.class);
                    startActivity(intent);
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
    }

    /**
     * The user will be brought back to the Home Screen
     */
    public void onBackPressed() {
        Intent intent = new Intent(HostScreen.this, HomeScreen.class);
        startActivity(intent);
    }

    /**
     * The current party will be deleted.
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
     * @param relation
     */
    private void deleteRelation(final String user_id, final String relation) {
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
                        headers.put("relation_id", relation);
                        return headers;
                    }
                };
                AppController.getInstance().addToRequestQueue(jsonObjReq,
                        tag_json_obj);
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
                AppController.getInstance().addToRequestQueue(jsonObjReq,
                        tag_json_obj);
            }
        }).start();
    }

    /**
     * The edited party information will be sent to the server using a JsonObjectRequest.  The changed information
     * will then be displayed on the screen.
     *
     * @param party_id
     */
    private void editParty(final String party_id) {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                        Const.URL_PARTY, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                getDataFromServer();
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
                        headers.put("id", party_id);
                        headers.put("party_name", party_name);
                        headers.put("date", dat);
                        headers.put("time", time);
                        headers.put("privacy", priv);
                        headers.put("max_people", maxp);
                        headers.put("alerts", alert);
                        headers.put("host", hosts);
                        headers.put("location", loc);
                        return headers;
                    }
                };
                AppController.getInstance().addToRequestQueue(jsonObjReq,
                        tag_json_obj);
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

    /*private String findID(String user_name) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(user_name)) {
                return usernames.get(i);
            }
        }
        return "ID not found";
    }
    private String findunam(String user_name) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(user_name)) {
                return unames.get(i);
            }
        }
        return "ID not found";
    }*/

    /**
     * When "View in maps" button is clicked, this will take you to the MapsActivity screen which
     * shows the party location in Google Maps.
     *
     * @param view
     */
    private void goLocation(View view) {
        Intent intent = new Intent(HostScreen.this, MapsActivity.class);
        intent.putExtra("party_location", loc);
        intent.putExtra("party_id", party_id);
        intent.putExtra("username", user_name);
        intent.putExtra("relation", relation);
        intent.putExtra("party_name", party_name);
        intent.putExtra("prev", "host");
        finish();
        startActivity(intent);
    }

    /**
     * User will be taken to the photos screen when the photos button is pressed.
     *
     * @param view
     */
    private void goPhotos(View view) {
        Intent intent = new Intent(HostScreen.this, Photos.class);
        intent.putExtra("party_name", party_id);
        intent.putExtra("relation", relation);
        intent.putExtra("user_id", user_id);
        intent.putExtra("party_name", party_name);
        intent.putExtra("prev", "host");
        startActivity(intent);
    }

    /**
     * User will be taken to the Comments screen when the comments button is pressed.
     *
     * @param view
     */
    private void goComments(View view) {
        Intent intent = new Intent(HostScreen.this, Comments.class);
        intent.putExtra("party_id", party_id);
        intent.putExtra("username", user_name);
        intent.putExtra("relation", relation);
        intent.putExtra("party_name", party_name);
        intent.putExtra("prev", "host");
        startActivity(intent);
    }

    /**
     * An alert dialog is displayed with and EditText box for the user to change the party name.  If "Ok" is pressed,
     * the new name will be sent to the server, if "cancel" is pressed, the dialog box is closed.
     */
    private void editname() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("New Party Name");
        alert.setMessage("Message");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                party_name = input.getText().toString();

                editParty(party_id);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    /**
     * A date selector (calender) is displayed.  When a new date is selected it will be sent to the server.
     */
    private void editdate() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("New Date (yyyy-mm-dd)");
        alert.setMessage("Message");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                dat = input.getText().toString();

                editParty(party_id);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        dateText.setText(dat);
        alert.show();
    }



    /**
     * A time selector (clock) is displayed.  When a new time is selected it will be sent to the server.
     */
    private void edittime(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("New Time (xx:xx:00)");
        alert.setMessage("Message");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                time = input.getText().toString();

                editParty(party_id);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }


    /**
     * An alert dialog is displayed with and EditText box for the user to change the location.  If "Ok" is pressed,
     * the new location will be sent to the server, if "cancel" is pressed, the dialog box is closed.
     */
    private void editlocation() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("New Party Location");
        alert.setMessage("Message");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                loc = input.getText().toString();

                editParty(party_id);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }


    /**
     * A JsonArrayRequest with the party id is sent to the server that grabs all of the users invited to the party and
     * displayed in the list view.
     */
    private void getAllUsers() {
        list.clear();
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
                                usernames.add(response.getJSONObject(i).getString("user_id"));
                                idList.add(response.getJSONObject(i).getString("id"));
                                scannedList.add(response.getJSONObject(i).getString("scanned_in"));
                                //unames.add(response.getJSONObject(i).getString("username"));
                                adapter.notifyDataSetChanged();
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

    public void scanUsers(final String party_id, final String user_id) {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        Const.URL_SCAN, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
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
                        headers.put("party_id", party_id);
                        headers.put("user_id", user_id);
                        headers.put("scanned_in", "1");
                        return headers;
                    }
                };
                AppController.getInstance().addToRequestQueue(jsonObjReq,
                        tag_json_obj);
            }
        }).start();
    }

    public void getUserIDByFullName(final String f_name, final String l_name, final String party_id) {
        Log.d("test p", party_id);
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        Const.URL_USERS_BY_FULL_NAME, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String user_id;
                                try {
                                    user_id = response.getString("id");
                                    scanUsers(party_id, user_id);
                                    Log.d("test u", user_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("test error", e.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("test error", error.toString());
                    }
                }) {
                    /**
                     * Passing some request headers
                     */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("f_name", f_name);
                        headers.put("l_name", l_name);
                        return headers;
                    }
                };
                AppController.getInstance().addToRequestQueue(jsonObjReq,
                        tag_json_obj);
            }
        }).start();
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
