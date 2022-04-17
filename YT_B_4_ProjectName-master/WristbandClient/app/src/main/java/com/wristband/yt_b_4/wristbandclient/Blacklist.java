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
import android.widget.Button;

import com.facebook.login.LoginManager;

public class Blacklist extends AppCompatActivity {
    private Button btnBack;
    private String party_id, relation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        btnBack = (Button) findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new user with values from the EditTexts
                goBack(view);
            }

        });
        Intent intent = getIntent();
        party_id = intent.getStringExtra("party_name");
        relation = intent.getStringExtra("relation");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_info, menu);
        return true;
    }

    /**
     * Method initiates a drop down menu that contains different items.
     * The first case is a button that creates an intent that sends a
     * user to the About screen.
     * The second case is a logout button that is designed to log a user out.
     *
     * @param item
     * @return returns true if the user gets no errors
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
     * creates an intent that sends a user back to the host screen
     *
     * @param view
     */
    private void goBack(View view) {
        Intent intent = new Intent(Blacklist.this, HostScreen.class);
        intent.putExtra("party_name", party_id);
        intent.putExtra("relation", relation);

        startActivity(intent);
    }
}
