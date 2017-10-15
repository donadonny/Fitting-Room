package tk.talcharnes.unborify;

import android.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.Map;

/**
 * Created by Tal.
 * This activity with it's fragment displays the uploads screen for users to upload their
 * occasion photos.
 */

public class PhotoUploadActivity extends AppCompatActivity {

    private final static String TAG = PhotoUploadActivity.class.getSimpleName();

    /**
     * Initializes basic initialization of components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


    }

}
