package tk.talcharnes.unborify.Utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import tk.talcharnes.unborify.Report;

/**
 * Created by Tal on 9/4/2017.
 */

public class FirebaseConstants {
    public final static String COMMENTS = "Comments";
    public final static String PHOTOS = "Photos";
    public final static String USERS = "users";
    public final static String INSTANCEID = "instanceId";
    public final static String USERDATA = "users/data";
    public final static String USERNAME = "name";
    public final static String IMAGES = "images";
    public final static String REPORTS = "Reports";
    public final static String NOTIFICATION = "notifications";
    public final static String VOTES = "votes";
    public final static String PHOTO_REPORTS = "reports";
    public final static String REPORTED_BY = "reported_by";
    public final static String NUM_REPORTS = "numReports";
    public final static String DATE_JOINED = "date_joined";
    public final static String COMMENT_KEY = "comment_key";
    public final static String COMMENT_STRING = "commentString";
    public final static String OCCASION_SUBTITLE = "occasion_subtitle";
    public final static String CONTACT_US = "Contact_us";
    public final static String CONTACT_TYPE_TIP = "Tip";
    public final static String CONTACT_TYPE = "contact_type";
    public final static String CONTACT_TYPE_BUG = "Bug";
    public final static String CONTACT_TYPE_OTHER = "Other";
    public final static String CONTACT_US_MESSAGE = "message";
    public final static String EMAIL = "email";
    public final static String URL = "url";



    public static void setToken(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            FirebaseDatabase.getInstance().getReference().child(USERDATA).
                    child(user.getUid()).child(INSTANCEID).setValue(token);
        }
    }

    public static void setReport(final String TAG, final Context context, final String reportID,
                                 final String userID) {
        final DatabaseReference reportRef =  FirebaseDatabase.getInstance().getReference()
                .child(REPORTS).child(reportID);
        reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(FirebaseConstants.REPORTED_BY).child(userID).exists()) {
                        Log.d(TAG, "Already reported by user: " + userID);
                        Toast.makeText(context, "You have reported this already.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        long numReports = (long) dataSnapshot.child(NUM_REPORTS).getValue();
                        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US)
                                .format(new Date());
                        reportRef.child(NUM_REPORTS).setValue(numReports + 1);
                        reportRef.child(REPORTED_BY).child(userID).setValue(timeStamp);
                        Log.d(TAG, "User made a report. Another report was added.");
                        Toast.makeText(context, "A report was made.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US)
                            .format(new Date());
                    HashMap<String, String> reports = new HashMap<String, String>();
                    reports.put(userID, timeStamp);
                    Report report = new Report(1, reports);
                    reportRef.setValue(report);
                    Log.d(TAG, "User made a report. A new report was made.");
                    Toast.makeText(context, "A report was made.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "cancelled with error: " + databaseError);
                Toast.makeText(context, "Failed to a report.", Toast.LENGTH_SHORT).show();
            }

        });
    }

}
