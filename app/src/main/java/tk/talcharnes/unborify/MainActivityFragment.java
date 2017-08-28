package tk.talcharnes.unborify;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;

import java.util.ArrayList;
import java.util.Arrays;

import static tk.talcharnes.unborify.R.id.frame;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    FloatingActionButton fab;
    FloatingActionButton likeButton, dislikeButton, reportButton;

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private AdView mAdView;
    private AdRequest mAdRequest;
    ArrayList<Photo> photoList;
    private int i = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String userId;
    private String oldestPostId = "";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference photoReference = firebaseDatabase.getReference().child("Photos");
    DatabaseReference userReference = firebaseDatabase.getReference().child("users");
    boolean initializePhotoList;

//    For swipe views
private SwipePlaceHolderView mSwipeView;


    //        For Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static final int RC_SIGN_IN = 1;
    private InterstitialAd mInterstitialAd;
    private Boolean showAd = false;
    private View rootView;
    private Boolean isReported =  false;

    /**
     * Constructor.
     * */
    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        isUserLoggedIn();

        initializeBasicSetup();

        initializeFlingAdapterView();

        initializeAd();

        return rootView;
    }

    /**
     * Checks if the user is logged in. If not, then the user is prompt to log in.
     * */
    private void isUserLoggedIn() {
        //For firebase auth
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    userId = user.getUid();

                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + userId);
                } else {
                    // User is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder().setLogo(R.mipmap.ic_launcher)
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }

            }
        };
    }

    /**
     * Initializes Basic stuff. The photoList, mAdView, and the fab buttons.
     * */
    private void initializeBasicSetup() {
        mSwipeView = (SwipePlaceHolderView) getActivity().findViewById(frame);
        //choose your favorite adapter
        photoList = new ArrayList<Photo>();

        //Native banner ad
        mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Fab buttons
        likeButton = rootView.findViewById(R.id.thumbs_up_fab);
        dislikeButton = rootView.findViewById(R.id.thumbs_down_fab);
        reportButton = rootView.findViewById(R.id.report_button_fab);

        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeView.doSwipe(false);
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeView.doSwipe(true);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isReported = true;
                mSwipeView.doSwipe(false);
            }
        });
    }

    /**
     * Initializes FlingAdapterView.
     * */
    private void initializeFlingAdapterView() {
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_like)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_dislike));

        mSwipeView.addItemRemoveListener(new ItemRemovedListener() {

            @Override
            public void onItemRemoved(int count) {
                //do something when the count changes to some specific value.
                //For Example: Call server to fetch more data when count is zero
                if (count <= 3) {
                    onAdapterAboutToEmpty();
                }
            }
        });

        for (Photo photo : photoList) {
            mSwipeView.addView(new PhotoCard(getContext(), photo, mSwipeView, firebaseDatabase, photoReference, userReference, userId));
        }
    }


            /**
             * This function starts a query calls to retrieve the images.
             * */
            private void onAdapterAboutToEmpty() {
                if (mInterstitialAd.isLoaded() && showAd) {
                    mInterstitialAd.show();
                    showAd = false;
                } else {
                    //Log.d("TAG", "The interstitial wasn't loaded yet.");
                }

                if(photoList.isEmpty()) {
                    Query query = (oldestPostId.isEmpty()) ?
                            photoReference.orderByChild(Photo.URL_KEY).limitToFirst(8) :
                            photoReference.orderByChild(Photo.URL_KEY).startAt(oldestPostId).limitToFirst(8);

                    // Read from the database
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            initializePhotoList = true;
                            addPhotosToArrayList(dataSnapshot);
                            System.out.println("Got data.");
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(LOG_TAG, "Failed to read value.", error.toException());
                        }
                    });
                }
            }


    /**
     * Initializes Ad.
     * */
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

    /**
     * Retrieves and adds photos to the photoList.
     * */
    private void addPhotosToArrayList(DataSnapshot dataSnapshot) {
        for(DataSnapshot child : dataSnapshot.getChildren()) {
            Photo photo = child.getValue(Photo.class);
            photoList.add(photo);
        }
        if(photoList.size() > 1) {
            photoList.remove(0);
        }
        oldestPostId = photoList.get(photoList.size()-1).getUrl();
    }

    /**
     * This function records the user's vote in the database.
     * */
    private void setVote(final Photo photo, final String rating) {
        if (!userId.equals(photo.getUser())) {
            Query query = photoReference.child(photo.getUrl().replace(".webp", "")).child("Votes").child(userId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String value = photo.getVotes().get(userId);
                        if(value.equals(rating)) {
                            Log.d(LOG_TAG, "User already " + rating + " this photo.");
                        } else {
                            if(rating.equals("like")) {
                                photo.setLikes(photo.getLikes() + 1);
                                photo.setDislikes(photo.getDislikes() - 1);
                            } else {
                                photo.setLikes(photo.getLikes() - 1);
                                photo.setDislikes(photo.getDislikes() + 1);
                            }
                            //photoReference.child(photo.getUrl()).setValue(photo);
                            //photoReference.child(photo.getUrl()).child("Votes").child(userId).setValue(rating);
                        }

                    } else {
                        if(rating.equals("like")) {
                            photo.setLikes(photo.getLikes() + 1);
                        } else {
                            photo.setDislikes(photo.getDislikes() + 1);
                        }
                        //photoReference.child(photo.getUrl()).setValue(photo);
                        //photoReference.child(photo.getUrl()).child("Votes").child(userId).setValue(rating);
                        Log.d(LOG_TAG, "snapshot value does not exist");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(LOG_TAG, "cancelled with error - " + databaseError);
                }

            });
        } else {
            Log.d(LOG_TAG, "User trying to vote on own photo");
        }
    }

    /**
     * Adds the Auth Listener when the app is started up.
     * */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Removes the Auth Listener when the app is closed.
     * */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}