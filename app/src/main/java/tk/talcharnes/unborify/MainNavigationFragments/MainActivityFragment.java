package tk.talcharnes.unborify.MainNavigationFragments;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.talcharnes.unborify.Models.CommentModel;
import tk.talcharnes.unborify.Models.PhotoModel;
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

    private ArrayList<PhotoModel> photoModelList;
    private String userId, userName;
    private String oldestPostId;
    private DatabaseReference photoReference;
    private DatabaseReference reportRef;

    private InterstitialAd mInterstitialAd;
    private Boolean showAd = false;
    private View rootView;
    private SwipePlaceHolderView mSwipeView;
    private Button refreshButton;
    private TextView refresh_textview, noImagesTextView;
    private Context mContext;
    private int widthInDP;
    private int heightInDP;
    private boolean refresh;
    private Spinner spinner;
    private boolean firstTime = true;
    private boolean categoryMode = false;

    /**
     * Constructor.
     */
    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        photoReference = DatabaseContants.getPhotoRef();
        reportRef = FirebaseConstants.getRef().child(FirebaseConstants.REPORTS);
        oldestPostId = "";

        initializeBasicSetup();

        initializeSwipePlaceHolderView();
        Log.d(LOG_TAG, "Load main");

        //initializeAd();

        return rootView;
    }

    /**
     * Initializes Basic stuff. The photoModelList, mAdView, and the fab buttons.
     */
    private void initializeBasicSetup() {
        //choose your favorite adapter
        photoModelList = new ArrayList<PhotoModel>();
        FirebaseUser user = FirebaseConstants.getUser();
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

    }

    /**
     * Initializes SwipePlaceHolderView.
     */
    private void initializeSwipePlaceHolderView() {
        mSwipeView = (SwipePlaceHolderView) rootView.findViewById(R.id.swipeView);

        int bottomMargin = Utils.dpToPx(90);
        Point windowSize = Utils.getDisplaySize(getActivity().getWindowManager());
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

        spinner = (Spinner) getActivity().findViewById(R.id.toolbar).findViewById(R.id.spinner);
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
                        noImagesTextView.setText(getActivity().getResources()
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
                        /*final DatabaseReference photoRef = photoReference.child(PhotoUtilities
                                .removeWebPFromUrl(photoModel.getUrl()));*/

                        /*Randomizing votes for photos
                        photoReference.child(photoModel.getUrl().replace(".webp", "")).child("likes").setValue((int) (Math.random()*10));
                        photoReference.child(photoModel.getUrl().replace(".webp", "")).child("dislikes").setValue((int) (Math.random()*10));*/

                        /*Randomizing categories for photos
                        String[] categories = getActivity().getResources().getStringArray(R.array.spinner_list_item_array);
                        photoReference.child(photoModel.getUrl().replace(".webp", "")).child("category")
                                .setValue(categories[(int) Math.floor((Math.random() * categories.length-1) + 1)]);*/
                        DatabaseReference ref = photoReference.child(photoModel.getUrl().replace(".webp", ""));

//                        System.out.println();

//                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                                    oldPhoto oldPhoto = child.getValue(oldPhoto.class);
//                                    PhotoModel photo = new PhotoModel(oldPhoto.getUser(),
//                                            oldPhoto.getOccasion_subtitle(), oldPhoto.getCategory(),
//                                            oldPhoto.getLikes(), oldPhoto.getDislikes(),
//                                            oldPhoto.getReports(), oldPhoto.getOrientation(), oldPhoto.getUrl());
//                                    DatabaseContants.getPhotoRef().child(""+oldPhoto.getUrl()).setValue(photo);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });

//                        DatabaseReference ref2 = photoReference.child(photoUrl).child("votes");
//
//                        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                GenericTypeIndicator<HashMap<String, String>> t =
//                                        new GenericTypeIndicator<HashMap<String, String>>();
//
//                                HashMap<String, String> map = new HashMap<String, String>();
//                                for(DataSnapshot child : dataSnapshot.getChildren()) {
//                                    map.put(child.getKey().toString(), child.getValue().toString());
//                                }
//                                DatabaseContants.getVotesRef().child(""+photoUrl).setValue(map);
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });

                        if(photoModel != null) {
                            if(list.size() == 0) {
                                oldestPostId = PhotoUtilities.removeWebPFromUrl(photoModel.getUrl());
                            }
                            list.add(new PhotoCard(mContext, photoModel, mSwipeView, userId, userName,
                                    photoReference, reportRef));
                        }
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
                // Failed to read value
                Log.w(LOG_TAG, "Failed to read value.", error.toException());
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
                                    mSwipeView, userId, userName, photoReference, reportRef));
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

    /**
     * Initializes Ad.
     */
    private void initializeAd() {
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                showAd = false;
                Log.i("Ads", "onAdClosed");
            }
        });
    }

}

class oldPhoto {
    private long likes;
    private long dislikes;
    private String url;
    private HashMap<String, String> Votes;
    private long reports;
    private String user;
    private String occasion_subtitle;
    private int orientation;
    private boolean isAd;
    private String category;
    public final static String OCCASION_SUBTITLE_KEY = "occasion_subtitle";
    public final static String USER_KEY = "user";
    public final static String REPORTS_KEY = "reports";
    public static final String URL_KEY = "url";
    public static final String DISLIKES_KEY = "dislikes";
    public static final String LIKES_KEY = "likes";
    public static final String CATEGORY_KEY = "category";
    public static final String VOTES = "votes";

    public oldPhoto() {

    }

    public oldPhoto(String user, String occasion_subtitle, String category, long likes, long dislikes, long reports,
                 int orientation, String url) {
        this.user = user;
        this.occasion_subtitle = occasion_subtitle;
        this.category = category;
        this.likes = likes;
        this.dislikes = dislikes;
        this.reports = reports;
        this.orientation = orientation;
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public void setVotes(HashMap<String, String> Votes) {
        this.Votes = Votes;
    }

    public HashMap<String, String> getVotes() {
        return Votes;
    }

    public long getDislikes() {
        return dislikes;
    }

    public void setDislikes(long dislikes) {
        this.dislikes = dislikes;
    }

    public long getReports() {
        return reports;
    }

    public void setReports(long reports) {
        this.reports = reports;
    }

    public String getOccasion_subtitle() {
        return occasion_subtitle;
    }

    public void setOccasion_subtitle(String occasion_subtitle) {
        this.occasion_subtitle = occasion_subtitle;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isAd() {
        return isAd;
    }

    public void setAd(boolean ad) {
        isAd = ad;
    }

    public String getCategory() {
        if (category != null) {
            return category;
        }
        return "Fashion";
    }

    public void setCategory(String category) {
        this.category = category;
    }

}