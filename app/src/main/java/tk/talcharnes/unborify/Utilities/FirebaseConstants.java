package tk.talcharnes.unborify.Utilities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

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

    public final static String Notification_URL = "";


    public static void setToken(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            FirebaseDatabase.getInstance().getReference().child(USERDATA).
                    child(user.getUid()).child(INSTANCEID).setValue(token);
        }
    }

}
