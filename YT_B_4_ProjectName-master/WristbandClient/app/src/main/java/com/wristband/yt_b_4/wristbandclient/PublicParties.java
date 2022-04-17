package com.wristband.yt_b_4.wristbandclient;

/**
 * Created by Jackguzzetta on 10/23/17.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

import com.wristband.yt_b_4.wristbandclient.app.AppController;
import com.wristband.yt_b_4.wristbandclient.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;


public class PublicParties extends AppCompatActivity {
    ListView listView;
    List list = new ArrayList();
    ArrayList<String> party_ids = new ArrayList<String>();

    ArrayAdapter adapter;
    ProgressDialog pDialog;
    String user_id, user_name;
    boolean isHost;
    String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicparty);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        initializeControls();
    }


    /**
     * Creates a menu in the action bar that gives you options to logout, delete party and view your profile
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
     * Method is called once the screen is opened. Method makes a call to getAllParties and
     * fills the list view with the values in list.
     */
    private void initializeControls() {
        getAllPartiesBypub();
        listView = (ListView) findViewById(R.id.list_view2);
        SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        user_id = settings.getString("id", "default");
        user_name = settings.getString("username", "default");
        adapter = new ArrayAdapter(PublicParties.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String party_name = (String) parent.getItemAtPosition(position);
                guestScreen(party_name);

            }
        });


    }

    /**
     * Method sends the current user to either the Host screen or the guest screen.
     * The method checks the host for the given party. If the user is the host then
     * they will be given relation 1, otherwise they will be given relation 2.
     *
     * @param party_name
     */
    public void guestScreen(String party_name) {
        Intent intent;
        getHost(party_name);
        if (isHost) {
            intent = new Intent(this, HostScreen.class);
            intent.putExtra("relation", "1");
        } else {
            intent = new Intent(this, GuestScreen.class);
            intent.putExtra("relation", "2");
        }
        intent.putExtra("menu", "publicmenu");
        intent.putExtra("previous", "public");
        intent.putExtra("party_name", party_name);
        intent.putExtra("username", user_name);
        startActivity(intent);
        finish();
        startActivity(intent);
    }

    /**
     * If the method is executed the current user will be logged out
     * and returned to the login screen.
     *
     * @param view
     */
    public void logout(View view) {
        Intent intent = new Intent(this, Login.class);
        finish();
        startActivity(intent);
    }

    /**
     * creates a new intent that switches the activity to HomeScreen
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PublicParties.this, HomeScreen.class);
        startActivity(intent);
    }


    /**
     * Checks the data base for a list of party ids in the Relation table and adds them
     * to the party_ids array list.
     * <p>
     * Method then calls the getDataFromServer(final String id)
     * method using ID as a parameter
     */
    private void getAllPartiesBypub() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500L); //wait for party to be created first
                    JsonArrayRequest req = new JsonArrayRequest(Const.URL_RELATION,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        for (int i = 0; i < response.length(); i++) {
                                            String id = response.getJSONObject(i).getString("party_id");
                                            party_ids.add(id);
                                            getDataFromServer(id);
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Method checks the data base Party table for parties matching the id given in the
     * parameters. If the privacy of the party is marked with a 1 it will get added to
     * List provided it has not already been added.
     *
     * @param id
     */
    private void getDataFromServer(final String id) {
        new Thread(new Runnable() {
            public void run() {
                //showProgressDialog();
                JsonArrayRequest req = new JsonArrayRequest(Const.URL_PARTY + id,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    String name = response.getJSONObject(0).getString("party_name");
                                    String privacy = response.getJSONObject(0).getString("privacy");
                                    int priv = Integer.parseInt(privacy);
                                    if (priv == 1 && (list.contains(name) == false)) {
                                        list.add(name);
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
        }).start();
    }

    /**
     * look at user_name to see if its getting the right username!!!
     * might have to come up with a way to get all people in the party
     * and check to see if current user is in the party
     * if not, intent to new screen with option to add self as guest to party!
     *
     * @param pty_name
     */
    private void getHost(final String pty_name) {
        new Thread(new Runnable() {
            public void run() {
                JsonArrayRequest req = new JsonArrayRequest(Const.URL_PARTY_BY_NAME + pty_name,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    String host = response.getJSONObject(0).getString("host");
                                    if (host == user_name)
                                        isHost = true;
                                    else
                                        isHost = false;
                                } catch (JSONException e) {
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                AppController.getInstance().addToRequestQueue(req, tag_json_arry);
            }
        }).start();
    }
}
