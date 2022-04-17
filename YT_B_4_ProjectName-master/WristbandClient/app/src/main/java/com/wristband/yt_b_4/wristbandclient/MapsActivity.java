package com.wristband.yt_b_4.wristbandclient;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.login.LoginManager;
import com.wristband.yt_b_4.wristbandclient.app.AppController;
import com.wristband.yt_b_4.wristbandclient.models.Party;
import com.wristband.yt_b_4.wristbandclient.utils.Const;

import android.view.MenuInflater;
import android.view.Menu;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Geocoder;

import java.util.Locale;

import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.identity.intents.Address;

import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String address;
    private ProgressDialog pDialog;
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";
    private String party_name, mnus, prev_class;
    private String party_id, relation;
    private int screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        mnus = getIntent().getStringExtra("menu");
        address = getIntent().getStringExtra("party_location");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        party_id = getIntent().getStringExtra("party_id");
        relation = getIntent().getStringExtra("relation");
        party_name = getIntent().getStringExtra("party_name");
        screen = Integer.parseInt(relation);
    }


    /**
     * Creates a menu in the action bar that gives you options to logout, delete party and view your profile
     *
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapsmenu, menu);
        return true;
    }

    /**
     * opens a dropdown menu that is filled with buttons a user can click.
     * case1 will call onBackPressed and switch the activity to the home screen.
     *
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        double longitude;
        double latitude;
        mMap = googleMap;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocationName(address, 1);
            android.location.Address address = addresses.get(0);
            longitude = address.getLongitude();
            latitude = address.getLatitude();
            LatLng loc = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(loc).title(party_name));
            float zoomLevel = 16.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoomLevel));
        } catch (Exception e) {

            Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address));
            startActivity(searchAddress);

        }


    }
}
