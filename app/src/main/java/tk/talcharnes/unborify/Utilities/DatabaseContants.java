package tk.talcharnes.unborify.Utilities;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tk.talcharnes.unborify.Models.PhotoModel;

/**
 * Created by Khuram Chaudhry on 11/27/17.
 *
 * This class holds key static values and common methods for the Firebase Database.
 */

public class DatabaseContants {

    public final static String MAIN = "Database_Restructure";
    public final static String USERS = "Users";
    public final static String USERNAME = "name";
    public final static String FOLLOWING = "Following";
    public final static String PHOTOS = "Photos";
    public final static String COMMENTS = "Comments";
    public final static String VOTES = "Votes";
    public final static String FAVORITES = "Favorites";
    public final static String REPORTS = "Reports";
    public final static String NOTIFICATIONS = "Notifications";
    public final static String INSTANCEIDS = "InstanceIds";
    public final static String INSTANCEID = "instanceId";
    public final static String CONTACT_US = "Contact_us";

    public static DatabaseReference getRef() {
        return FirebaseDatabase.getInstance().getReference().child(MAIN);
    }

    public static DatabaseReference getUserRef() {
        return getRef().child(USERS);
    }

    public static DatabaseReference getUserRef(String uid) {
        return getRef().child(USERS).child(uid);
    }

    public static DatabaseReference getCurrentUserRef() {
        return getRef().child(USERS).child(getCurrentUser().getUid());
    }

    public static DatabaseReference getTokenRef() {
        return getRef().child(INSTANCEIDS);
    }

    public static DatabaseReference getPhotoRef() {
        return getRef().child(PHOTOS);
    }

    public static DatabaseReference getPhotoRef(String url) {
        return getRef().child(PHOTOS).child(url);
    }

    public static DatabaseReference getCommentRef() {
        return getRef().child(COMMENTS);
    }

    public static DatabaseReference getVotesRef() {
        return getRef().child(VOTES);
    }

    public static DatabaseReference getFavoritesRef(String uid) {
        return getRef().child(FAVORITES).child(uid);
    }

    public static DatabaseReference getReportRef(String id) {
        return getRef().child(REPORTS).child(id);
    }

    public static DatabaseReference getNotificationRef(String uid) {
        return getRef().child(NOTIFICATIONS).child(uid);
    }

    public static DatabaseReference getCurrentUserNotificationRef() {
        return getRef().child(NOTIFICATIONS).child(getCurrentUser().getUid());
    }

    public static DatabaseReference getFollowingRef() {
        return getRef().child(FOLLOWING);
    }

    public static DatabaseReference getUserFollowingRef() {
        return getRef().child(FOLLOWING).child(getCurrentUser().getUid());
    }

    public static DatabaseReference getContactRef() {
        return getRef().child(CONTACT_US);
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static boolean checkRefValue(DataSnapshot dataSnapshot) {
        return (dataSnapshot.exists() && dataSnapshot.getValue() != null);
    }

    public static void getData(DatabaseReference databaseReference,
                               ValueEventListener valueEventListener) {
        databaseReference.addListenerForSingleValueEvent(valueEventListener);

    }

    public static void getUserPhotos(String uid, ValueEventListener valueEventListener) {
        getPhotoRef().orderByChild(PhotoModel.USER_KEY).equalTo(uid)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public static void setToken() {
        String uid = getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        getTokenRef().child(uid).child(INSTANCEID).setValue(token);
    }

    public static void setToken(String token) {
        if(getCurrentUser() != null) {
            String uid = getCurrentUser().getUid();
            getTokenRef().child(uid).child(INSTANCEID).setValue(token);
        }
    }

    public static String convertTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return df.format(date);
    }

    public static void logDatabaseError(String TAG, DatabaseError databaseError) {
        Log.d(TAG, "Database Error");
        Log.d(TAG, "Error Code: " + databaseError.getCode());
        Log.d(TAG, databaseError.getMessage());
        Log.d(TAG, databaseError.getDetails());
    }
}
