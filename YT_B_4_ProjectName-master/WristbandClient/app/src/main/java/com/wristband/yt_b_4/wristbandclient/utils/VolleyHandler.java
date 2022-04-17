package com.wristband.yt_b_4.wristbandclient.utils;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wristband.yt_b_4.wristbandclient.app.AppController;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mike on 11/5/2017.
 */

public class VolleyHandler extends Application {
    static final String tag_json_obj = "jobj_req";
    final String tag_json_arry = "jarray_req";




    //Used by Add_User
    public static void inviteUser(final String party_id, final String user_id, final String privladges) {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
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
                        headers.put("relation", privladges); //2 = normal guest, 3=cohost
                        return headers;
                    }
                };
                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(jsonObjReq,
                        tag_json_obj);
                // Cancelling request
                // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);

    }

    //Used by Add_User
    public static void invitebyNumber(final String number, final String username, final String id) {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        Const.URL_TEXT, null,
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
                        headers.put("number", number);
                        headers.put("username", username);
                        headers.put("id", id);
                        return headers;
                    }
                };
                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(jsonObjReq,
                        tag_json_obj);
                // Cancelling request
                // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
            }
}
