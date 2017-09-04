package tk.talcharnes.unborify;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tk.talcharnes.unborify.Utilities.FirebaseConstants;

/**
 * Created by khuramchaudhry on 9/2/17.
 *
 */

public class ProfileActivity extends AppCompatActivity {

    private final static String TAG = ProfileActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TextView nameText, emailText, joinedText;

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

        /* Set up Toolbar to return back to the MainActivity */
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        /* Set up the user's name, email, and the register date */
        Intent intent = getIntent();
        if(intent != null) {
            String name = intent.getStringExtra("name");
            String email = intent.getStringExtra("email");
            String uid = intent.getStringExtra("uid");

            nameText.setText(name);
            emailText.setText(email);

           // TODO: 9/4/2017  
            FirebaseDatabase.getInstance().getReference("Users").child(uid).child(FirebaseConstants.DATE_JOINED)
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

}
