package tk.talcharnes.unborify.UserProfile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import tk.talcharnes.unborify.Models.UserModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.GlideLoader2;

/**
 * Created by Khuram Chaudhry on 10/19/17.
 * This Activity displays the user information.
 */

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = UserProfileActivity.class.getSimpleName();
    private final int FOLLOWING_COLOR = R.color.colorPrimaryDark,
            FOLLOW_COLOR = R.color.colorAccent, FOLLOWING_TEXT = R.string.following,
            FOLLOW_TEXT = R.string.follow;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AvatarView avatarView;
    private TextView userNameText, userJoinedText;
    private IImageLoader imageLoader;
    private Button followingButton;
    private boolean isFollowing;

    /**
     * This Adapter holds the different fragments for the Activity tabs.
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * Initializes basic initialization of components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initializeViews();
        initializeToolbarSettings();
        initializeUserSettings();
    }

    /**
     * This function initializes the UI elements for the Activity.
     */
    private void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        avatarView = (AvatarView) findViewById(R.id.avatarImage);
        userNameText = (TextView) findViewById(R.id.user_profile_name);
        userJoinedText = (TextView) findViewById(R.id.user_date_joined);
        followingButton = (Button) findViewById(R.id.following_btn);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * This function sets up the toolbar.
     */
    private void initializeToolbarSettings() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    /**
     * This function sets up user info and the following button.
     */
    private void initializeUserSettings() {
        imageLoader = new GlideLoader2();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            final String uid = intent.getStringExtra("uid");

            DatabaseContants.getUserRef(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                if (userModel != null) {
                                    String name = userModel.getName();
                                    userNameText.setText((name.length() > 18) ?
                                            name.substring(0, 15) + "...\n..." + name.substring(15)
                                            : name);
                                    String dateJoined = DatabaseContants.convertTime(
                                            userModel.getDateJoined());
                                    userJoinedText.setText(dateJoined);
                                    imageLoader.loadImage(avatarView, uid, userModel.getName());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                        }
                    });

            isFollowing = false;

            final DatabaseReference followingRef = DatabaseContants.getUserFollowingRef().child(uid);

            followingRef.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                setFollowingButton(FOLLOWING_TEXT, FOLLOWING_COLOR);
                                isFollowing = true;
                            } else {
                                setFollowingButton(FOLLOW_TEXT, FOLLOW_COLOR);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, databaseError.getMessage());
                        }
            });

            followingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isFollowing) {
                        followingRef.child(uid).removeValue();
                        setFollowingButton(FOLLOW_TEXT, FOLLOW_COLOR);
                    }
                    else {
                        followingRef.setValue(getString(FOLLOWING_TEXT));
                        setFollowingButton(FOLLOWING_TEXT, FOLLOWING_COLOR);
                    }
                    isFollowing = !isFollowing;
                }
            });
        }
    }

    /**
     * This function sets the following button's text and color.
     */
    private void setFollowingButton(int buttonText, int buttonColor) {
        followingButton.setText(getString(buttonText));
        followingButton.setBackgroundColor(ContextCompat
                .getColor(getApplicationContext(), buttonColor));

    }

    /**
     * This function adds the fragments to the ViewPager.
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            bundle.putString("uid", intent.getStringExtra("uid"));
        }

        UserPhotoFragment userPhotoFragment = new UserPhotoFragment();
        userPhotoFragment.setArguments(bundle);

        UserFollowingFragment userFollowingFragment = new UserFollowingFragment();
        userFollowingFragment.setArguments(bundle);

        UserLikesFragment userLikesFragment = new UserLikesFragment();
        userLikesFragment.setArguments(bundle);

        adapter.addFragment(userPhotoFragment, "PHOTOS");
        adapter.addFragment(userFollowingFragment, "FOLLOWING");
        adapter.addFragment(userLikesFragment, "LIKES");
        viewPager.setAdapter(adapter);
    }

}