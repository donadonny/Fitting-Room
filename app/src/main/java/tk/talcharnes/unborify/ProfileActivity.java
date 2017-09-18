package tk.talcharnes.unborify;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import tk.talcharnes.unborify.Utilities.FirebaseConstants;

/**
 * Created by khuramchaudhry on 9/2/17.
 *
 */

public class ProfileActivity extends AppCompatActivity {

    private final static String TAG = ProfileActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TextView nameText, emailText, joinedText;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView imageView;
    private Bitmap thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialize();

    }

    /**
     * This function initializes basic stuff.
     * */
    public void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        nameText = (TextView) findViewById(R.id.user_profile_name);
        emailText = (TextView) findViewById(R.id.user_profile_email);
        joinedText = (TextView) findViewById(R.id.user_date_joined);
        imageView = (ImageView) findViewById(R.id.user_profile_photo);

        /* Set up Toolbar to return back to the MainActivity */
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        /* Set up the user's name, email, and the register date */
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            String name = intent.getStringExtra("name");
            String email = intent.getStringExtra("email");
            String uid = intent.getStringExtra("uid");

            nameText.setText(name);
            emailText.setText(email);

            FirebaseDatabase.getInstance().getReference(FirebaseConstants.USERDATA).child(uid).child(FirebaseConstants.DATE_JOINED)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null) {
                                joinedText.setText(dataSnapshot.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } else {
            nameText.setText("Bobby Bob");
            emailText.setText("bobbybob@bob.com");
            joinedText.setText("Jan 1, 2000");
        }

        Glide.with(this).load("http://www.womenshealthmag.com/sites/womenshealthmag.com/files/images/power-of-smile_0.jpg")
                .crossFade()
                .thumbnail(.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    /**
     * This function handles hidden menu on the toolbar.
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    /**
     * This function handles the items clicked on the toolbar.
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if(id == R.id.action_edit) {

            showEditDialog();

        } else if(id == R.id.action_settings) {
            Toast.makeText(ProfileActivity.this, "This feature is not available.",
                    Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);

    }

    /**
     * This function shows and handles options for edit.
     * */
    private void showEditDialog() {
        String[] array = {"Change Name", "Change Password", "Change Profile Photo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("What do you like to change?")
                .setItems(array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Toast.makeText(ProfileActivity.this, "This feature is" +
                                        " not available.", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(ProfileActivity.this, "This feature is " +
                                        "not available.", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(ProfileActivity.this, "This feature is " +
                                        "not available.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void showChoosePictureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("What do you like to do?");
        final String[] options = {"Take Picture", "Pick from Gallery"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (options[item]) {
                    case "Take Picture":
                        cameraIntent();
                        break;
                    case "Pick from Gallery":
                        galleryIntent();
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(
                            getApplicationContext().getContentResolver(), data.getData());
                    imageView.setBackground(new BitmapDrawable(getResources(), thumbnail));
                } catch (IOException e) {
                    e.printStackTrace(); }
            } else if (requestCode == REQUEST_CAMERA) {
                thumbnail = (Bitmap) data.getExtras().get("data");
                imageView.setBackground(new BitmapDrawable(getResources(), thumbnail));
            }
        }
    }

}
