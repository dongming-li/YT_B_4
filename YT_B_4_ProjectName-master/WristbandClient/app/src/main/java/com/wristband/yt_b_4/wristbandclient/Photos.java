package com.wristband.yt_b_4.wristbandclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

public class Photos extends AppCompatActivity {
    ListView listView;
    List list = new ArrayList();
    private String party_name, mnus, relation, user_id, prev_class;
    private EditText caption;
    private List<String> captions = new ArrayList<String>();
    private List<Integer> ids = new ArrayList<Integer>();
    private Button btnPhoto;
    public static int RESULT_LOAD_IMAGE = 1;
    ImageButton pic;
    private int screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        this.pic = (ImageButton) findViewById(R.id.pict);

        Intent intent = getIntent();
        party_name = intent.getStringExtra("party_name");
        relation = intent.getStringExtra("relation");
        user_id = intent.getStringExtra("user_id");
        mnus = getIntent().getStringExtra("menu");
        prev_class = getIntent().getStringExtra("prev");
        caption = (EditText) findViewById(R.id.caption);
        screen = Integer.parseInt(relation);
        listView = (ListView) findViewById(R.id.list_view);
        btnPhoto = (Button) findViewById(R.id.btnPhotos);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GaleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(GaleryIntent, RESULT_LOAD_IMAGE);
            }


        });
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new user with values from the EditTexts
                addPhoto(view);
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


            pic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            Toast.makeText(getApplicationContext(), picturePath, Toast.LENGTH_LONG).show();
        }
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

    /*
        takes the caption and picture path and adds them to the listview and adds the picture path
        to the database
     */
    public void addPhoto(View v) {
        String pic_text = caption.getText().toString();
        captions.add(pic_text);
    }


}
