package tk.talcharnes.unborify.Utilities;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    public final static String USER_CONNECTIONS = "user_connections";
    public final static String PHOTOS = "Photos";
    public final static String COMMENTS = "Comments";

    public static DatabaseReference getRef() {
        return FirebaseDatabase.getInstance().getReference().child(MAIN);
    }

    public static DatabaseReference getUserRef() {
        return getRef().child(USERS);
    }

    public static DatabaseReference getCurrentUserRef(String uid) {
        return getRef().child(USERS).child(uid);
    }

    public static DatabaseReference getPhotoRef() {
        return getRef().child(PHOTOS);
    }

    public static DatabaseReference getCommentRef() {
        return getRef().child(COMMENTS);
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

    public static void logDatabaseError(String TAG, DatabaseError databaseError) {
        Log.d(TAG, "Database Error");
        Log.d(TAG, "Error Code: " + databaseError.getCode());
        Log.d(TAG, databaseError.getMessage());
        Log.d(TAG, databaseError.getDetails());
    }
}
