package tk.talcharnes.unborify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import agency.tango.android.avatarviewglide.GlideLoader;
import tk.talcharnes.unborify.MainNavigationFragments.FollowingFragment;
import tk.talcharnes.unborify.MainNavigationFragments.MainActivityFragment;
import tk.talcharnes.unborify.MainNavigationFragments.OtherFragment;
import tk.talcharnes.unborify.MainNavigationFragments.TopicsFragment;
import tk.talcharnes.unborify.MainNavigationFragments.TrendingFragment;
import tk.talcharnes.unborify.OtherFragmentActivities.ContactUs.ContactUsFragment;
import tk.talcharnes.unborify.OtherFragmentActivities.Notifications.NotificationFragment;
import tk.talcharnes.unborify.Profile.ProfileActivity;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.OtherFragmentActivities.MyPhotos.MyPhotosFragment;

/**
 * Created by Tal.
 * This activity sets the navigation drawers as well the fragments and is the main fall back
 * activity.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DatabaseReference notificationRef;
    private ChildEventListener notificationListener;
    private Toolbar toolbar;
    private String uid, userName;
    private IImageLoader imageLoader;
    private NoSwipePager viewPager;
    private BottomBarAdapter pagerAdapter;

    public static int fragment_id = R.id.nav_home;
    public static int previous_fragment_id = fragment_id;

    /**
     * Initializes basic initialization of components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing main components
        initialize();

        if (savedInstanceState == null) {
            fragment_id = R.id.nav_home;
            previous_fragment_id = fragment_id;
        }

    }

    /**
     * This function initializes basic stuff.
     */
    public void initialize() {
        Log.d(TAG, "Initializing Main Activity.");

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.drawable.ic_logo);
        }
        imageLoader = new GlideLoader();

        FirebaseUser user = FirebaseConstants.getUser();
        userName = user.getDisplayName();
        uid = user.getUid();
        Uri uri = user.getPhotoUrl();
        String uriString = (uri != null) ? uri.toString() : "";
        AvatarView profileView = toolbar.findViewById(R.id.avatarImage);
        imageLoader.loadImage(profileView, uriString, userName);
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        // myNotifications Stuff
        if (user != null) {
            notificationRef = FirebaseConstants.getRef().child(FirebaseConstants.USERS)
                    .child(user.getUid()).child(FirebaseConstants.NOTIFICATION);
            // setNotificationListener();
        }

        setupViewPager();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_home) {
                    viewPager.setCurrentItem(0);
                } else if (tabId == R.id.tab_trending) {
                    viewPager.setCurrentItem(1);
                } else if (tabId == R.id.tab_topics) {
                    viewPager.setCurrentItem(2);
                } else if (tabId == R.id.tab_following) {
                    viewPager.setCurrentItem(3);
                } else {
                    viewPager.setCurrentItem(4);
                }
            }
        });
    }

    private void setupViewPager() {
        viewPager = (NoSwipePager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        pagerAdapter.addFragments(new MainActivityFragment());
        pagerAdapter.addFragments(new TrendingFragment());
        pagerAdapter.addFragments(new TopicsFragment());
        pagerAdapter.addFragments(new FollowingFragment());
        pagerAdapter.addFragments(new OtherFragment());

        viewPager.setAdapter(pagerAdapter);
    }

    /**
     * This function handles back presses.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * This function handles hidden menu on the toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //MenuItem item = menu.findItem(R.id.action_picture);

        if (fragment_id == R.id.nav_home && FirebaseAuth.getInstance().getCurrentUser() != null) {
            //item.setVisible(true);
            getMenuInflater().inflate(R.menu.main, menu);
        } else {
            //item.setVisible(false);
            /*if(fragment_id == R.id.nav_notifications) {
                getMenuInflater().inflate(R.menu.notifications, menu);
            }*/
        }

        return true;
    }

    /**
     * This function handles the items clicked on the toolbar.
     */
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

        return super.onOptionsItemSelected(item);

    }

    /**
     * This function handles the action with the picture icon on the toolbar is clicked.
     */
    public void takePic() {
        Intent intent = new Intent(this, PhotoUploadActivity.class);
        startActivity(intent);
    }

    /**
     * This function checks if a new notification has been add.
     */
    public void setNotificationListener() {
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

    /**
     * This function cleans up the notification listener when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if(notificationListener != null) {
            notificationRef.removeEventListener(notificationListener);
        }*/
    }

}
