package tk.talcharnes.unborify;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.GlideLoader2;

/**
 * Created by khuramchaudhry on 10/19/17.
 */

public class UserProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AvatarView avatarView;
    private TextView userNameText, userJoinedText;
    private IImageLoader imageLoader;
    private Button followingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initializeViews();

        initializeToolbarSettings();

    }

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

    private void initializeToolbarSettings() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        imageLoader = new GlideLoader2();

        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            final String uid = intent.getStringExtra("uid");
            final String following = getResources().getString(R.string.following);

            FirebaseConstants.getRef().child(FirebaseConstants.USERS).child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null) {
                                    if(user.getName().length() > 18) {

                                    }
                                    String name = user.getName();
                                    userNameText.setText((name.length() > 18) ?
                                            name.substring(0, 15) + "...\n..." + name.substring(15)
                                            : name);
                                    userJoinedText.setText(user.getDate_joined());
                                    String profileUri = user.getUri() + "";
                                    imageLoader.loadImage(avatarView, uid, user.getName());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            FirebaseConstants.getRef().child(FirebaseConstants.USERS)
                    .child(FirebaseConstants.getUser().getUid())
                    .child(FirebaseConstants.USER_CONNECTIONS).child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                followingButton.setText(following);
                                followingButton.setBackgroundColor(ContextCompat
                                        .getColor(getApplicationContext(),
                                                R.color.colorPrimaryDark));
                                followingButton.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            followingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseConstants.getRef().child(FirebaseConstants.USERS)
                            .child(FirebaseConstants.getUser().getUid())
                            .child(FirebaseConstants.USER_CONNECTIONS).child(uid)
                            .setValue(following);
                    followingButton.setText(following);
                    followingButton.setBackgroundColor(ContextCompat
                            .getColor(getApplicationContext(), R.color.colorPrimaryDark));
                    followingButton.setEnabled(false);
                }
            });
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}