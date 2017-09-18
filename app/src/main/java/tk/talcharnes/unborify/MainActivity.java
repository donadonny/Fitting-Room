package tk.talcharnes.unborify;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tk.talcharnes.unborify.NavigationFragments.AboutFragment;
import tk.talcharnes.unborify.NavigationFragments.ContactUsFragment;
import tk.talcharnes.unborify.NavigationFragments.HelpFragment;
import tk.talcharnes.unborify.NavigationFragments.Notifications.NotificationFragment;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.my_photos.MyPhotosFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ImageView profileImage;
    private TextView nameText, emailText;
    private ImageButton profileImageButton;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference notificationRef;
    private ChildEventListener notificationListener;
    private Toolbar toolbar;
    private String uid, userName;

    public static int fragment_id = R.id.nav_home;
    public static int previous_fragment_id = fragment_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing main components
        initialize();

        if (savedInstanceState == null) {
            fragment_id = R.id.nav_home;
            previous_fragment_id = fragment_id;
            loadFragment();
        }

    }

    /**
     * This function initializes basic stuff.
     * */
    public void initialize() {
        Log.d(TAG, "Initializing Main Activity.");

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Navigation Drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setUpNavigationView();

        // Navigation Header
        View navHeader = navigationView.getHeaderView(0);
        nameText = (TextView) navHeader.findViewById(R.id.user_name);
        emailText = (TextView) navHeader.findViewById(R.id.user_email);
        profileImage = (ImageView) navHeader.findViewById(R.id.img_profile);
        profileImageButton = (ImageButton) navHeader.findViewById(R.id.profile_image_button);
        loadHeaderData();

        // myNotifications Stuff
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            notificationRef = FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseConstants.USERDATA).child(user.getUid())
                    .child(FirebaseConstants.NOTIFICATION);
           // setNotificationListener();
        }
    }

    /**
     * This function loads user's data: image, name, and email.
     * */
    private void loadHeaderData() {
        FirebaseUser user = FirebaseConstants.getUser();
        if(user != null) {
            Log.d(TAG, "Loading user data to the navigation toolbar.");
            userName = user.getDisplayName();
            final String email = user.getEmail();
            uid = user.getUid();
            nameText.setText(userName);
            emailText.setText(email);
            Uri uri = user.getPhotoUrl();
            Glide.with(this).load("http://www.womenshealthmag.com/sites/womenshealthmag.com/files/images/power-of-smile_0.jpg")
                    .crossFade()
                    .thumbnail(.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileImage);
            // showing dot next to notifications label
            navigationView.getMenu().getItem(2).setActionView(R.layout.menu_dot);
            profileImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("name", userName);
                    intent.putExtra("email", email);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }
            });
        } else {
            Log.d(TAG, "User does not exist.");
        }
    }

    /**
     * This function sets up the drawer.
     * */
    private void setUpNavigationView() {
        Log.d(TAG, "Initializing the navigation drawer.");

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                previous_fragment_id = fragment_id;
                fragment_id = menuItem.getItemId();

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                if(fragment_id == R.id.action_sign_out) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.sign_out_title)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FirebaseAuth.getInstance().signOut();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.d(TAG, "Canceling out sign out dialog.");
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                    menuItem.setChecked(false);
                } else if(previous_fragment_id != fragment_id) {
                    loadFragment();
                } else {
                    drawer.closeDrawers();
                    Log.d(TAG, "User clicked on the same Fragment.");
                }

                return true;
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    /**
     * This function transitions from the current fragment to the one clicked.
     * */
    private void loadFragment() {
        Log.d(TAG, "Preparing to switch fragments");

        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
        xfragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        xfragmentTransaction.replace(R.id.frame, getFragment()).commit();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();

        Log.d(TAG, "Switched fragments.");
    }

    /**
     * This function returns the proper fragment based on the click in navigation drawer.
     * */
    private Fragment getFragment() {
        switch (fragment_id) {
            case R.id.nav_photos:
                Log.d(TAG, "Returning the photos fragment.");
                navigationView.getMenu().getItem(1).setChecked(true);
                return new MyPhotosFragment();
            case R.id.nav_notifications:
                Log.d(TAG, "Returning the notifications fragment.");
                navigationView.getMenu().getItem(2).setChecked(true);
                return new NotificationFragment();
            case R.id.nav_help:
                Log.d(TAG, "Returning the help fragment.");
                navigationView.getMenu().getItem(3).setChecked(true);
                return new HelpFragment();
            case R.id.nav_contact_us:
                Log.d(TAG, "Returning the contact fragment.");
                navigationView.getMenu().getItem(4).setChecked(true);
                return new ContactUsFragment();
            case R.id.nav_about_us:
                Log.d(TAG, "Returning the about fragment.");
                navigationView.getMenu().getItem(5).setChecked(true);
                return new AboutFragment();
            default:
                Log.d(TAG, "Returning the home fragment.");
                navigationView.getMenu().getItem(0).setChecked(true);
                return new MainActivityFragment();
        }
    }

    /**
     * This function handles back presses.
     * */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            Log.d(TAG, "Closing the navigation drawer with back pressed.");
            return;
        }

        /* If the current fragment is the main fragment then the back key
            back sends the user back to main fragment. */
        if(fragment_id != R.id.nav_home) {
            fragment_id = R.id.nav_home;
            loadFragment();
            Log.d(TAG, "Back pressed. Returning to the home fragment.");
            return;
        }

        super.onBackPressed();
    }

    /**
     * This function handles hidden menu on the toolbar.
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //MenuItem item = menu.findItem(R.id.action_picture);

        if(fragment_id == R.id.nav_home && FirebaseAuth.getInstance().getCurrentUser() != null) {
            //item.setVisible(true);
            getMenuInflater().inflate(R.menu.main, menu);
        } else {
            //item.setVisible(false);
            if(fragment_id == R.id.nav_notifications) {
                getMenuInflater().inflate(R.menu.notifications, menu);
            }
        }

        return true;
    }

    /**
     * This function handles the items clicked on the toolbar.
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_picture) {
            Log.d(TAG, "Starting up the photo upload Activity.");
            takePic();
            return true;
        }

        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    /**
     * This function handles the action with the picture icon on the toolbar is clicked.
     * */
    public void takePic(){
        Intent intent = new Intent(this, PhotoUploadActivity.class);
        startActivity(intent);
    }

    public void setNotificationListener(){
        notificationListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                /*myNotifications myNotificationsSnapshot = dataSnapshot
                        .getValue(myNotifications.class);

                if(myNotificationsSnapshot != null) {

                    System.out.println(myNotificationsSnapshot.getRead()+"------------"+
                            myNotificationsSnapshot.getPhotoUrl() + "------------"+
                            myNotificationsSnapshot.getMessage() +"------------"+
                            myNotificationsSnapshot.getSenderID() +"------------"+

                    String title = "user: " + myNotificationsSnapshot.getSenderName() +
                            " commented on your picture.";

                    String message = myNotificationsSnapshot.getMessage();

                    SharedPreferences sharedPref = MainActivity.this
                            .getSharedPreferences("saved_notification_key", Context.MODE_PRIVATE);

                    String pastKey = sharedPref.getString(FirebaseConstants.Notification_KEY,
                            FirebaseConstants.Notification_KEY);

                    String currentNotificationKey = dataSnapshot.getKey();

                    if(!currentNotificationKey.equals(pastKey)) {

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        Intent intent = new Intent(MainActivity.this, CommentActivity.class);
                        intent.putExtra("url", myNotificationsSnapshot.getPhotoUrl());
                        intent.putExtra("photoUserID", uid);
                        intent.putExtra("currentUser", uid);
                        intent.putExtra("notified", currentNotificationKey);
                        PendingIntent resultPendingIntent = PendingIntent
                                .getActivity(MainActivity.this, 0, intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

                        android.app.Notification notification = new NotificationCompat
                                .Builder(MainActivity.this, "comment_notification")
                                .setContentTitle(title)
                                .setContentText(message)
                                .setContentIntent(resultPendingIntent)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .build();
                        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE |
                                Notification.FLAG_AUTO_CANCEL;

                        int SERVER_DATA_RECEIVED = 1;
                        notificationManager.notify(SERVER_DATA_RECEIVED, notification);
                    }
                }*/
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        notificationRef.addChildEventListener(notificationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if(notificationListener != null) {
            notificationRef.removeEventListener(notificationListener);
        }*/
    }

}
