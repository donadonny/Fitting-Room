package tk.talcharnes.unborify.OtherFragmentActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import tk.talcharnes.unborify.R;

/**
 * Created by khuramchaudhry on 10/22/17.
 */

public class LikesActivity  extends AppCompatActivity {

    private static final String TAG = FavoritesActivity.class.getSimpleName();

    /**
     * Initializes basic initialization of components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        /* Set up Toolbar to return back to the MainActivity */
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

}
