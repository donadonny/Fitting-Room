package tk.talcharnes.unborify.OtherFragmentActivities.LIkes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import tk.talcharnes.unborify.OtherFragmentActivities.Favorites.FavoritesActivity;
import tk.talcharnes.unborify.R;

/**
 * Created by Khuram Chaudhry on 10/22/17.
 * This activity with its fragment displays the photos the user likes.
 */

public class LikesActivity extends AppCompatActivity {

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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getIntent() != null) {
                    Integer fragmentNum = getIntent().getIntExtra("fragmentNumber", 0);
                    Intent output = new Intent();
                    output.putExtra("fragmentNumber", fragmentNum.intValue());
                    setResult(0, output);
                    finish();
                }
            }
        });
    }

}
