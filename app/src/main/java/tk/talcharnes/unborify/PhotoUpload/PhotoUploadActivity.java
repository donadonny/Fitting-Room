package tk.talcharnes.unborify.PhotoUpload;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import tk.talcharnes.unborify.R;

/**
 * Created by Tal.
 * This activity with it's fragment displays the uploads screen for users to upload their
 * occasion photos.
 */

public class PhotoUploadActivity extends AppCompatActivity {

    private final static String TAG = PhotoUploadActivity.class.getSimpleName();

    private static String category = "Fashion";

    /**
     * Initializes basic initialization of components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(tk.talcharnes.unborify.R.layout.activity_photo_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    /**
     * This method returns the category that the user chose.
     */
    public static String getCategory() {
        return category;
    }

}
