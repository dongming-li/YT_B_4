package com.wristband.yt_b_4.wristbandclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.wristband.yt_b_4.wristbandclient.app.AppController;
import com.wristband.yt_b_4.wristbandclient.app.QRGenerator;
import com.wristband.yt_b_4.wristbandclient.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User_Info extends AppCompatActivity {
    private Button btnRemove;
    private TextView txtuser, txtfirst, usern, txtid;
    private String user_id, current_id, mnus, user_name, flname, lname, party_name, prev_class, relation, user_rel, party_id;
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";
    private ArrayList<String> names;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userIDs;
    private ImageView code;
    private int screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__info);
        btnRemove = (Button) findViewById(R.id.btnRemove);
        txtuser = (TextView) findViewById(R.id.usertxt);
        usern = (TextView) findViewById(R.id.usern);
        txtid = (TextView) findViewById(R.id.idtxt);
        code = (ImageView) findViewById(R.id.qr);
        mnus = getIntent().getStringExtra("menu");
        user_id = getIntent().getStringExtra("user_id");
        user_name = getIntent().getStringExtra("user_name");
        party_name = getIntent().getStringExtra("party_name");
        relation = getIntent().getStringExtra("relation");
        party_id = getIntent().getStringExtra("party_id");
        screen = Integer.parseInt(relation);
        user_rel = getIntent().getStringExtra("user_rel");
        prev_class = getIntent().getStringExtra("prev");

        if (prev_class.equals("guest") || relation.equals("1") || (user_rel.equals("3") && (relation.equals("3"))))
            btnRemove.setVisibility(View.INVISIBLE);
        else {
            btnRemove.setVisibility(View.VISIBLE);
        }

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new user with values from the EditTexts
                goRemove();
            }

        });

        //getDataFromServer();

        //user_id = getUserID(user_name);
        txtuser.setText(user_name);
        lname = user_name.substring(user_name.split(" ")[0].length() + 1, user_name.length());
        usern.setText("");
        txtid.setText("User ID: " + user_id);
        SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        current_id = settings.getString("id", "default");
        String f_name = settings.getString("f_name", "default");
        String l_name = settings.getString("l_name", "default");
        addqr(f_name, l_name, current_id);
    }

    /**
     * Uses the QRGenerator app to create a qr code based off of a users
     * user name. The code is generated as a bitmap
     * that can then be used in an image view.
     */

    private void addqr(String f_name, String l_name, String user_id) {
        QRGenerator x = new QRGenerator(f_name + "-" + l_name + "...");
        code.setImageBitmap(x.createQR());
    }

    /**
     * Creates a menu in the action bar that gives you options to logout, delete party and view your profile
     *
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.regular, menu);
        return true;
    }

    /**
     * opens a dropdown menu that is filled with buttons a user can click.
     * case1 will call onBackPressed and switch the activity to the home screen. Case2 creates
     * a new intent that switches the activity to the About class. On case3 the user will be logged out
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
     * Depending on your status in the party, this will act as a back button.  If host, you will be taken to the host screen,
     * but if you are a guest or cohost you will be taken to the guest screen.  This is done through intent with the
     * party name and user relation passed through the screens.
     */
    @Override
    public void onBackPressed() {
        Intent intent;
        if (prev_class.equals("host")) {
            intent = new Intent(User_Info.this, HostScreen.class);
        } else {
            intent = new Intent(User_Info.this, GuestScreen.class);
        }
        prev_class = "user_info";
        intent.putExtra("party_name", party_name);
        intent.putExtra("prev", prev_class);
        intent.putExtra("relation", user_rel);
        intent.putExtra("menu", mnus);
        startActivity(intent);
    }

    /**
     * When method is called the user currently selected will be removed from the party
     * relation table in the database using the current user_id, and party_id
     */
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
        onBackPressed();
    }

}
