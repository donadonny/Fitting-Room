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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.google.firebase.auth.FirebaseUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import tk.talcharnes.unborify.BottomBar.BottomBarAdapter;
import tk.talcharnes.unborify.BottomBar.NoSwipePager;
import tk.talcharnes.unborify.MainNavigationFragments.DealsFragment;
import tk.talcharnes.unborify.MainNavigationFragments.Following.FollowingFragment;
import tk.talcharnes.unborify.MainNavigationFragments.MainActivityFragment;
import tk.talcharnes.unborify.MainNavigationFragments.OtherFragment;
import tk.talcharnes.unborify.MainNavigationFragments.TrendingFragment;
import tk.talcharnes.unborify.PhotoUpload.PhotoUploadActivity;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Tal.
 * This activity is the main activity of the application. It sets up the navigation drawers for
 *  the bottom tab bar as well the toolbar.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static int fragment_id = R.id.nav_home;
    public static int previous_fragment_id = fragment_id;

    private Toolbar toolbar;
    private NoSwipePager viewPager;
    private BottomBarAdapter pagerAdapter;
    //private Spinner spinner;

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
     * This function initializes the UI elements and main initialization logic.
     */
    public void initialize() {
        Log.d(TAG, "Initializing Main Activity.");

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.drawable.ic_logo);
        }

        FirebaseUser user = DatabaseContants.getCurrentUser();
        String userName = user.getDisplayName();
        String uid = user.getUid();
        Uri uri = user.getPhotoUrl();
        String uriString = (uri != null) ? uri.toString() : "";

        //spinner = (Spinner) toolbar.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        //        R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //spinner.setAdapter(adapter);

        setupViewPager();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                //spinner.setVisibility(View.INVISIBLE);
                switch (tabId) {
                    case R.id.tab_home: viewPager.setCurrentItem(0); break;
                    case R.id.tab_trending: viewPager.setCurrentItem(1); break;
                    case R.id.tab_deals: viewPager.setCurrentItem(2); break;
                    case R.id.tab_following: viewPager.setCurrentItem(3); break;
                    default: viewPager.setCurrentItem(4); break;
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
        pagerAdapter.addFragments(new DealsFragment());
        pagerAdapter.addFragments(new FollowingFragment());
        pagerAdapter.addFragments(new OtherFragment());

        viewPager.setAdapter(pagerAdapter);
    }


}
