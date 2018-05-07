package tk.talcharnes.unborify.OtherFragmentActivities.Help;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import tk.talcharnes.unborify.R;

/**
 * Created by Khuram Chaudhry on 9/29/17.
 * This activity with its fragment displays an help screen.
 */

public class HelpActivity extends AppCompatActivity {

    /**
     * Initializes basic initialization of components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

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
