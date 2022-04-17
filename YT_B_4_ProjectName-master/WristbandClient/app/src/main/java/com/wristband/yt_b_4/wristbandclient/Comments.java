package com.wristband.yt_b_4.wristbandclient;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.wristband.yt_b_4.wristbandclient.app.AppController;
import com.wristband.yt_b_4.wristbandclient.models.Party;
import com.wristband.yt_b_4.wristbandclient.utils.Const;
import com.wristband.yt_b_4.wristbandclient.models.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Comments extends AppCompatActivity {
    Button btnComment, viewBtn, deleteBtn, cancelBtn, yes, no;
    ListView listView;
    List list = new ArrayList();
    List comment_id_list = new ArrayList();
    ArrayList<String> comments = new ArrayList<String>();
    ArrayAdapter adapter;
    ProgressDialog pDialog;
    String user_id, party_id, username, relation, party_name, mnus, user_rel;
    int screen;
    String comment;
    Dialog CommentDialog;
    Dialog DeleteDialog;
    String name = "test test: ";
    EditText cmt;
    String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        btnComment = (Button) findViewById(R.id.send);
        mnus = getIntent().getStringExtra("menu");
        cancelBtn = (Button) findViewById(R.id.cancel);
        cmt = (EditText) findViewById(R.id.editText);
        party_id = getIntent().getStringExtra("party_id");
        relation = getIntent().getStringExtra("relation");
        party_name = getIntent().getStringExtra("party_name");
        user_rel = getIntent().getStringExtra("user_rel");
        screen = Integer.parseInt(relation);


        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new user with values from the EditTexts
                comment = cmt.getText().toString();
                sendComment(view, comment);
            }

        });
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
        inflater.inflate(R.menu.regular, menu);
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
     * Depending on your status in the party, this will act as a back button.  If host, you will be taken to the host screen,
     * but if you are a guest or cohost you will be taken to the guest screen.  This is done through intent with the
     * party name and user relation passed through the screens.
     */
    @Override
    public void onBackPressed() {
        Intent intent;
        switch (screen) {
            case 1://host
                intent = new Intent(this, HostScreen.class);
                intent.putExtra("party_name", party_name);
                intent.putExtra("relation", relation);
                intent.putExtra("menu", mnus);
                startActivity(intent);
                finish();
                break;
            case 2://guest
                intent = new Intent(this, GuestScreen.class);
                intent.putExtra("party_name", party_name);
                intent.putExtra("relation", relation);
                intent.putExtra("menu", mnus);
                startActivity(intent);
                finish();
                break;
            case 3://cohost
                intent = new Intent(this, GuestScreen.class);
                intent.putExtra("party_name", party_name);
                intent.putExtra("relation", relation);
                intent.putExtra("menu", mnus);

                startActivity(intent);
                finish();
                break;
            default:
        }
    }

    /**
     * This is called once the screen is opened. The listview will be filled by grabbing all of the comments
     * in this party from the database.  When a list view item is clicked however, the comment dialog will pop
     * up on the screen with different action options.
     */
    private void initializeControls() {
        SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        String f_name = settings.getString("f_name", "default");
        String l_name = settings.getString("l_name", "default");
        username = f_name + " " + l_name;
        listView = (ListView) findViewById(R.id.list_view);
        //SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        //user_id = settings.getString("id", "default");
        //user_name= settings.getString("username", "default");
        adapter = new ArrayAdapter(Comments.this, android.R.layout.simple_list_item_1, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView

                View view = super.getView(position, convertView, parent);

                return view;
            }
        };

        listView.setAdapter(adapter);


        //listView.setBackgroundColor(Color.CYAN);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                commentingDialog(v, i);
            }
        });

        getAllCommentsByPartyId(party_id);
    }


    /**
     * When the send button is pressed, the typed comment will be sent to the database and displayed
     * in the list view.
     *
     * @param view
     * @param comment
     */
    private void sendComment(View view, String comment) {
        SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        String f_name = settings.getString("f_name", "default");
        String l_name = settings.getString("l_name", "default");
        username = f_name + " " + l_name;
        Comment c = new Comment(party_id, username, comment);
        sendDataToServer(username, c);
    }


    /**
     * Sends a Json request using volley to the database and returns all of the comments in this specific party
     * and only this party.  The comments are added to the listview on the screen.
     *
     * @param party_id
     */
    private void getAllCommentsByPartyId(String party_id) {
        JsonArrayRequest req = new JsonArrayRequest(Const.URL_GET_COMMENTS + "/" + party_id,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                String id = response.getJSONObject(i).getString("id");
                                String uname = response.getJSONObject(i).getString("username");
                                String text = response.getJSONObject(i).getString("comment");
                                list.add(uname + ": " + text);
                                comment_id_list.add(id);
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

    /**
     * The comment created by a user is sent to the database using a JsonObject Request with volley.
     * The comment will already be displayed from the sendComment() method.
     *
     * @param comment
     */
    private void sendDataToServer(final String username, final Comment comment) {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        Const.URL_COMMENTS, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String id = null;
                                String com = null;
                                try {
                                    id = response.getString("id");
                                    com = response.getString("comment");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("test", e.toString());
                                }
                                list.add(username + ": " + com);
                                comment_id_list.add(id);
                                adapter.notifyDataSetChanged();
                                cmt.setText("");


                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("test", error.toString());

                    }
                }) {
                    /**
                     * Passing some request headers
                     * */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("party_id", comment.getPartyId());
                        headers.put("username", comment.getUsername());
                        headers.put("text", comment.getText());

                        return headers;
                    }
                };
                AppController.getInstance().addToRequestQueue(jsonObjReq,
                        tag_json_obj);
            }
        }).start();
    }

    /**
     * A commenting dialog is brought up on the screen with the text "What would you like to do?"
     * and 2 or 3 buttons depending on your relation to the party.  If cancel is pressed, the dialog is
     * closed.  If view is pressed, you are brought to the User Info screen of the user whose comment
     * you pressed.  If you are a host, there will be an option to delete comment, which, if pressed,
     * will bring up another dialog that asks you yes or no.
     *
     * @param view
     * @param i
     */
    public void commentingDialog(View view, int i) {
        final int selection = i;
        final SharedPreferences[] settings = new SharedPreferences[1];
        final SharedPreferences.Editor[] editor = new SharedPreferences.Editor[1];
        CommentDialog = new Dialog(Comments.this);
        CommentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CommentDialog.setContentView(R.layout.comment_dialog);
        CommentDialog.setTitle("What would you like to do?");

        deleteBtn = CommentDialog.findViewById(R.id.delete_comment);
        cancelBtn = CommentDialog.findViewById(R.id.cancel);

        deleteBtn.setEnabled(true);
        if (relation.equals("2") || (relation.equals("3"))){
            deleteBtn.setVisibility(View.INVISIBLE);
        }
        deleteBtn.setEnabled(true);


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletionDialog(selection);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDialog.cancel();
            }
        });

        CommentDialog.show();
    }

    /**
     * A JsonObjectRequest is sent to the database and the selected comment will be deleted from the DB.
     * The comment will also be removed form the list view.
     *
     * @param id
     */
    private void deleteComment(final String id) {
        new Thread(new Runnable() {
            public void run() {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                        Const.URL_COMMENTS + "/" + id, null,
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

    /**
     * A dialog that is brought up when trying to delete a comment.  If yes is pressed, deleteComment() will
     * be called and the window will close.  If no is pressed, the dialog will just close and nothing will happen.
     *
     * @param i
     */
    public void deletionDialog(final int i) {
        final SharedPreferences[] settings = new SharedPreferences[1];
        final SharedPreferences.Editor[] editor = new SharedPreferences.Editor[1];
        DeleteDialog = new Dialog(Comments.this);
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
                deleteComment(comment_id_list.get(i).toString());
                comment_id_list.remove(i);
                list.remove(i);
                CommentDialog.cancel();
                DeleteDialog.cancel();
                adapter.notifyDataSetChanged();
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
}
