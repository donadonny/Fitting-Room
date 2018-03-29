package tk.talcharnes.unborify.OtherFragmentActivities.MyPhotos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import tk.talcharnes.unborify.R;

/**
 * Created by Khuram Chaudhry on 9/29/17.
 * This activity with its fragment displays user's photos.
 */

public class MyPhotosActivity extends AppCompatActivity {

    /**
     * Initializes basic initialization of components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_photos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        /* Set up Toolbar to return back to the MainActivity */
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

}
