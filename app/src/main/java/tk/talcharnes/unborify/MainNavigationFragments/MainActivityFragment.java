package tk.talcharnes.unborify.MainNavigationFragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.auth.User;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import tk.talcharnes.unborify.Models.CommentModel;
import tk.talcharnes.unborify.Models.DealsModel;
import tk.talcharnes.unborify.Models.PhotoModel;
import tk.talcharnes.unborify.Models.UserModel;
import tk.talcharnes.unborify.PhotoCard.AdCard;
import tk.talcharnes.unborify.PhotoCard.PhotoCard;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;
import tk.talcharnes.unborify.Utilities.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private String userId, userName;
    private String oldestPostId;
    private DatabaseReference photoReference;
    private View rootView;
    private SwipePlaceHolderView mSwipeView;
    private Button refreshButton;
    private TextView refresh_textview, noImagesTextView;
    private Context mContext;
    private boolean refresh;
    private Spinner spinner;
    private boolean firstTime = true;
    private boolean categoryMode = false;
    private Activity activity;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        photoReference = DatabaseContants.getPhotoRef();
        oldestPostId = "";
        
        activity = getActivity();

        initializeBasicSetup();
        
        return rootView;
    }

    /**
     * Initializes Basic stuff. The photoModelList, mAdView, and the fab buttons.
     */
    private void initializeBasicSetup() {
        //choose your favorite adapter
        FirebaseUser user = DatabaseContants.getCurrentUser();
        userId = user.getUid();
        userName = user.getDisplayName();

        //Native banner ad
        /*AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        refreshButton = rootView.findViewById(R.id.refreshBtn);

        refresh_textview = rootView.findViewById(R.id.refreshTitle);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldestPostId = "";
                refresh = false;
                refreshButton.setVisibility(View.GONE);
                refresh_textview.setVisibility(View.GONE);
                getPhotos();
            }
        });

        noImagesTextView = rootView.findViewById(R.id.noImagesTitle);

        mSwipeView = (SwipePlaceHolderView) rootView.findViewById(R.id.swipeView);
        spinner = (Spinner) activity.findViewById(R.id.toolbar).findViewById(R.id.spinner);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*FirebaseDatabase.getInstance().getReference().child("Photos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final HashSet<String> users = new HashSet<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String category = snapshot.child("category").getValue(String.class);
                    String ocassion = snapshot.child("occasion_subtitle").getValue(String.class);
                    Integer o = snapshot.child("orientation").getValue(Integer.class);
                    String url = snapshot.child("url").getValue(String.class);
                    String uid = snapshot.child("user").getValue(String.class);
                    long likes = 0;
                    long dislikes = 0;
                    PhotoModel photo = new PhotoModel(uid, ocassion, category, 0, 0, (o ==  null) ? 0 : o, url);
                    Log.d(LOG_TAG, snapshot.getKey());
                    if(photo != null && photo.getUserUid() != null) {
                        users.add(photo.getUserUid());
                        for(DataSnapshot comment : snapshot.child("Comments").getChildren()) {
                            String message = comment.child("commentString").getValue(String.class);
                            String puid = comment.child("photo_Uploader").getValue(String.class);
                            String key = comment.child("comment_key").getValue(String.class);
                            String cuid = comment.child("commenter").getValue(String.class);
                            String curl = comment.child("photo_url").getValue(String.class);
                            CommentModel commentModel = new CommentModel(puid, message, System.currentTimeMillis(), curl, key, cuid);
                            DatabaseContants.getCommentRef().child(comment.getKey()).setValue(commentModel);
                        }
                        for(DataSnapshot vote : snapshot.child("votes").getChildren()) {
                            DatabaseContants.getVotesRef().child(snapshot.getKey()).child(vote.getKey()).setValue(vote.getValue());
                            if(vote.getValue(String.class).equals("likes")) {
                                likes++;
                            } else {
                                dislikes++;
                            }
                        }
                        photo.setLikes(likes);
                        photo.setDislikes(dislikes);
                        DatabaseContants.getPhotoRef(snapshot.getKey()).setValue(photo);
                    }
                }
                FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(users.contains(snapshot.getKey())) {
                                String email = snapshot.child("email").getValue(String.class);
                                String name = snapshot.child("name").getValue(String.class);
                                String uri = snapshot.child("uri").getValue(String.class);
                                String date = snapshot.child("date_joined").getValue(String.class);
                                long milliseconds = 0;
                                if(date != null) {
                                    date = date.replace(",", "");
                                    date = date.replaceAll(" ", "-");
                                    SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
                                    SimpleDateFormat f2 = new SimpleDateFormat("MMM-dd-yyyy");
                                    try {
                                        Date d = f.parse(date);
                                        milliseconds = d.getTime();
                                    } catch (Exception e) {
                                        try {
                                            Date d = f2.parse(date);
                                            milliseconds = (milliseconds == 0) ? d.getTime() : milliseconds;
                                        } catch (Exception e2) {

                                        }
                                    }
                                }
                                UserModel user = new UserModel(name, email, uri, milliseconds);
                                DatabaseContants.getRef().child(DatabaseContants.INSTANCEIDS).child(snapshot.getKey()).child(DatabaseContants.INSTANCEID).setValue(snapshot.child("instanceId").getValue());
                                for(DataSnapshot not : snapshot.child("Notifications").getChildren()) {
                                    DatabaseContants.getNotificationRef(snapshot.getKey()).child(not.getKey()).setValue(not.getValue());
                                }
                                for(DataSnapshot not : snapshot.child("user_favorites").getChildren()) {
                                    DatabaseContants.getFavoritesRef(snapshot.getKey()).child(not.getKey()).setValue(System.currentTimeMillis());
                                }
                                for(DataSnapshot not : snapshot.child("user_connections").getChildren()) {
                                    DatabaseContants.getFollowingRef().child(snapshot.getKey()).child(not.getKey()).setValue("Following");
                                }
                                DatabaseContants.getUserRef(snapshot.getKey()).setValue(user);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        initializeSwipePlaceHolderView();
    }

    /**
     * Initializes SwipePlaceHolderView.
     */
    private void initializeSwipePlaceHolderView() {
        int bottomMargin = Utils.dpToPx(90);
        Point windowSize = Utils.getDisplaySize(activity.getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setIsUndoEnabled(true)
                .setHeightSwipeDistFactor(10)
                .setWidthSwipeDistFactor(5)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth((int) (windowSize.x * .99))
                        .setViewHeight(((int) (windowSize.y * .90)) - bottomMargin)
                        .setViewGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f));

        getPhotos();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String chosen = parent.getItemAtPosition(position).toString();
                oldestPostId = "";
                Log.d(LOG_TAG, "category chosen: " + chosen);
                mSwipeView.removeAllViews();
                noImagesTextView.setVisibility(View.GONE);
                if (firstTime) {
                    if (chosen.equals("All")) {
                        getPhotos();
                        categoryMode = false;
                    } else {
                        getPhotos(chosen);
                        categoryMode = true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        firstTime = false;

        mSwipeView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
                Log.d(LOG_TAG, "Swipe");
                //do something when the count changes to some specific value.
                //For Example: Call server to fetch more data when count is zero
                if (count < 1) {
                    /*if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }*/
                    Log.d(LOG_TAG, "Empty SwipeView");
                    if (categoryMode) {
                        noImagesTextView.setVisibility(View.VISIBLE);
                        noImagesTextView.setText(activity.getResources()
                                .getString(R.string.no_image_title_6));
                    } else if (refresh) {
                        Log.d(LOG_TAG, "No more photos");
                        refreshButton.setVisibility(View.VISIBLE);
                        refresh_textview.setVisibility(View.VISIBLE);
                    } else {
                        getPhotos();
                        Log.d(LOG_TAG, "Getting more photos");
                    }
                }
            }
        });
    }

    /**
     * Get photos from the database and adds it to the SwipePlaceHolderView.
     */
    private void getPhotos() {
        mContext = getContext();

        final long startTime = System.currentTimeMillis();
        Query query;

        if (oldestPostId.isEmpty()) {
            query = photoReference.orderByChild(PhotoModel.URL_KEY).limitToLast(9);
        } else {
            query = photoReference.orderByChild(PhotoModel.URL_KEY).endAt(oldestPostId).limitToLast(8);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<PhotoCard> list = new ArrayList<PhotoCard>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        PhotoModel photoModel = child.getValue(PhotoModel.class);

                        if(list.size() == 0 && photoModel != null) {
                            oldestPostId = PhotoUtilities.removeWebPFromUrl(photoModel.getUrl());
                        }
                        list.add(new PhotoCard(mContext, photoModel, mSwipeView, userId, userName));
                    }

                    int stopAt = (list.size() < 8) ? -1 : 0;
                    for (int i = list.size() - 1; i > stopAt; i--) {
                        mSwipeView.addView(list.get(i));
                    }
                    if (list.size() < 7) {
                        int diff = 7 - list.size();
                        while (diff > 0) {
                            mSwipeView.addView(new AdCard(mContext, mSwipeView));
                            diff--;
                        }
                        oldestPostId = "";
                        refresh = true;
                    } else {
                        mSwipeView.addView(new AdCard(mContext, mSwipeView));
                    }
                    System.out.println("Got data.");
                    mSwipeView.refreshDrawableState();
                    final long endTime = System.currentTimeMillis();
                    Log.d(LOG_TAG, "Total execution time: " + (endTime - startTime));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(LOG_TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void getPhotos(String category) {
        mContext = getContext();

        final long startTime = System.currentTimeMillis();

        Query query = photoReference.orderByChild(PhotoModel.CATEGORY_KEY).equalTo(category)
                .limitToFirst(8);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<PhotoModel> photoModels = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        PhotoModel photoModel = child.getValue(PhotoModel.class);

                        if (photoModel != null) {
                            photoModels.add(photoModel);
                        }
                    }
                    int count = photoModels.size();
                    if(photoModels.isEmpty()) {
                        noImagesTextView.setVisibility(View.VISIBLE);
                    } else {
                        while (count > 0) {
                            mSwipeView.addView(new PhotoCard(mContext, photoModels.get(count - 1),
                                    mSwipeView, userId, userName));
                            if (count - 1 % 8 == 0) {
                                mSwipeView.addView(new AdCard(mContext, mSwipeView));
                                mSwipeView.addView(new AdCard(mContext, mSwipeView));
                            }
                            count--;
                        }
                        photoModels.clear();
                    }

                    Log.d(LOG_TAG, "Retrieved data");
                    mSwipeView.refreshDrawableState();
                    final long endTime = System.currentTimeMillis();
                    Log.d(LOG_TAG, "Data Load time: " + (endTime - startTime));
                } else {
                    noImagesTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(LOG_TAG, "Failed to read value.", error.toException());
            }
        });
    }

}

