package tk.talcharnes.unborify.Utilities;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mindorks.placeholderview.InfinitePlaceHolderView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import tk.talcharnes.unborify.Models.PhotoModel;
import tk.talcharnes.unborify.LoadPhotoView.LoadMoreView;
import tk.talcharnes.unborify.LoadPhotoView.PhotoView;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.UserProfile.UserProfileAdapter;

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
    public final static String DEALS = "Deals";
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

    public static DatabaseReference getUserNameRef(String uid) {
        return getRef().child(USERS).child(uid).child(USERNAME);
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

    public static DatabaseReference getDealsRef() {
        return getRef().child(DEALS);
    }

    public static boolean checkRefValue(DataSnapshot dataSnapshot) {
        return (dataSnapshot.exists() && dataSnapshot.getValue() != null);
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

    public static void retrievePhotosFromDatabase(String tag, Activity activity, View view, Query query,
                                                  InfinitePlaceHolderView iPlaceHolderView, 
                                                  int errorMessage, boolean canEdit) {
        final String userId = getCurrentUser().getUid();
        final String userName = getCurrentUser().getDisplayName();
        
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<PhotoModel> photoList = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        PhotoModel photoModel = child.getValue(PhotoModel.class);
                        if (photoModel != null) {
                            photoList.add(photoModel);
                        }
                    }
                    Collections.reverse(photoList);

                    for (int i = 0; i < LoadMoreView.LOAD_VIEW_SET_COUNT && i < photoList.size(); i++) {
                        iPlaceHolderView.addView(new PhotoView(activity, photoList.get(i),
                                userId, userName, iPlaceHolderView, canEdit));
                    }
                    if (photoList.size() > LoadMoreView.LOAD_VIEW_SET_COUNT) {
                        iPlaceHolderView.setLoadMoreResolver(new LoadMoreView(iPlaceHolderView,
                                photoList, userId, userName, canEdit));
                    }
                } else {
                    setDefaultView(activity, view, errorMessage);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                setDefaultView(activity, view, errorMessage);
                logDatabaseError(tag, databaseError);
            }
        });
    }

    public static void retrievePhotosFromDatabaseUsingUrl(String tag, Activity activity, View view, Query query,
                                                  InfinitePlaceHolderView iPlaceHolderView,
                                                  int errorMessage, boolean canEdit) {
        final String userId = getCurrentUser().getUid();
        final String userName = getCurrentUser().getDisplayName();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> photoList = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String url = child.getKey();
                        photoList.add(url);
                    }
                    Collections.reverse(photoList);

                    for (int i = 0; i < LoadMoreView.LOAD_VIEW_SET_COUNT && i < photoList.size(); i++) {
                        getPhotoRef(photoList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                PhotoModel photoModel = dataSnapshot.getValue(PhotoModel.class);
                                if(photoModel != null) {
                                    iPlaceHolderView.addView(new PhotoView(activity, photoModel,
                                            userId, userName, iPlaceHolderView, canEdit));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                logDatabaseError(tag, databaseError);
                            }
                        });
                    }
                    if (photoList.size() > LoadMoreView.LOAD_VIEW_SET_COUNT) {
                        iPlaceHolderView.setLoadMoreResolver(new LoadMoreView(iPlaceHolderView,
                                photoList, userId, userName));
                    }
                } else {
                    setDefaultView(activity, view, errorMessage);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                setDefaultView(activity, view, errorMessage);
                logDatabaseError(tag, databaseError);
            }
        });
    }

    public static void retriveProfilePhotosFromDatabase(String tag, Activity activity, View view,
                                                        Query query, RecyclerView recyclerView,
                                                        int errorMessage) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> photoList = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String url = child.getKey();
                        photoList.add(url);
                    }
                    if(photoList.size() == 0) {
                        setDefaultView(activity, view, errorMessage);
                    } else {
                        UserProfileAdapter adapter = new UserProfileAdapter(photoList);

                        recyclerView.setLayoutManager(new LinearLayoutManager(activity,
                                LinearLayoutManager.HORIZONTAL, false));
                        recyclerView.setHasFixedSize(false);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                setDefaultView(activity, view, errorMessage);
                logDatabaseError(tag, databaseError);
            }
        });
    }

    /**
     * The method set the view if there is no photos belonging to the user.
     */
    private static void setDefaultView(Activity activity, View rootView, int errorMessage) {
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.activity_main);
        TextView textView = new TextView(activity);
        textView.setText(activity.getString(errorMessage));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(textView);
    }
}
