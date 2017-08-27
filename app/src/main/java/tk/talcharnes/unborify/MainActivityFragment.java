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
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    FloatingActionButton fab;
    FloatingActionButton likeButton, dislikeButton, reportButton;

    SwipeFlingAdapterView swipeFlingAdapterView;
    SwipeViewAdapter swipeViewAdapter;
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
                swipeFlingAdapterView.getTopCardListener().selectLeft();
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeFlingAdapterView.getTopCardListener().selectRight();
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isReported = true;
                swipeFlingAdapterView.getTopCardListener().selectLeft();
            }
        });
    }

    /**
     * Initializes FlingAdapterView.
     * */
    private void initializeFlingAdapterView() {
        swipeViewAdapter = new SwipeViewAdapter(getActivity(), photoList);

        swipeFlingAdapterView = rootView.findViewById(R.id.frame);

        swipeViewAdapter = new SwipeViewAdapter(getActivity(), photoList);

        //set the listener and the adapter
        swipeFlingAdapterView.setAdapter(swipeViewAdapter);

        swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                photoList.remove(0);
                if(photoList.isEmpty() && !oldestPostId.isEmpty()) {
                    showAd = true;
                }
                swipeViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                final Photo photo = (Photo) dataObject;
                final String name = photo.getUrl().replace(".webp","");
                final String dislikeStringKey = "dislike";
                final String likeStringKey = "like";
                if(isReported) {
                    final DatabaseReference reportsRef = firebaseDatabase.getReference().child("Reports");
                    final Query query = reportsRef.child(name);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                if(!snapshot.child("reported_by").child(userId).exists()) {
                                    Log.d(LOG_TAG, "User already reported photo.");
                                } else {
                                    long numReports = (long) snapshot.child("numReports").getValue();
                                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
                                    photoReference.child(name).child("reports").setValue(numReports + 1);
                                    reportsRef.child(name).child("numReports").setValue(numReports + 1);
                                    reportsRef.child(name).child("reported_by").child(userId).setValue(timeStamp);
                                    Log.d(LOG_TAG, "Another report add.");
                                }
                            } else {
                                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
                                HashMap<String, String> reports = new HashMap<String, String>();
                                reports.put(userId, timeStamp);
                                Report report = new Report(1, reports);
                                reportsRef.child(name).setValue(report);
                                photoReference.child(name).child("reports").setValue(1);
                                Log.d(LOG_TAG, "A new report.");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(LOG_TAG, "cancelled with error - " + databaseError);
                        }
                    });
                } else if(!userId.equals(photo.getUser())) {
                    Query query = photoReference.child(name).child("Votes").child(userId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.getValue().toString().equals(likeStringKey)) {
                                    photo.setLikes(photo.getLikes() - 1);
                                    photo.setDislikes(photo.getDislikes() + 1);
                                    photoReference.child(name).setValue(photo);
                                    userReference.child(photo.getUser()).child(name).setValue(photo);
                                    photoReference.child(name).child("Votes").child(userId).setValue(dislikeStringKey);

                                    Log.d(LOG_TAG, "snapshot value is like");
                                } else {
                                    Log.d(LOG_TAG, "snapshot value is already dislike");
                                }

                            } else {
                                photo.setDislikes(photo.getDislikes() + 1);
                                photoReference.child(name).setValue(photo);
                                userReference.child(photo.getUser()).child(name).setValue(photo);
                                photoReference.child(name).child("Votes").child(userId).setValue(dislikeStringKey);
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
                Log.d(LOG_TAG, "Left card Exit");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                final Photo photo = (Photo) dataObject;
                final String dislikeStringKey = "dislike";
                final String likeStringKey = "like";
                if (!userId.equals(photo.getUser())) {
                    photoReference.child(photo.getUrl().replace(".webp", "")).child("Votes").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.getValue().toString().equals(likeStringKey)) {
                                    Log.d(LOG_TAG, "snapshot value is already like");
                                } else {
                                    photo.setLikes(photo.getLikes() + 1);
                                    photo.setDislikes(photo.getDislikes() - 1);
                                    photoReference.child(photo.getUrl().replace(".webp", "")).setValue(photo);
                                    photoReference.child(photo.getUrl().replace(".webp", "")).child("Votes").child(userId).setValue(likeStringKey);

                                    Log.d(LOG_TAG, "snapshot value is dislike");
                                }

                            } else {
                                photo.setLikes(photo.getLikes() + 1);
                                photoReference.child(photo.getUrl().replace(".webp", "")).setValue(photo);
                                photoReference.child(photo.getUrl().replace(".webp", "")).child("Votes").child(userId).setValue(likeStringKey);
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

                Log.d(LOG_TAG, "Right card Exit");
            }

            /**
             * This function starts a query calls to retrieve the images.
             * */
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // TODO: 7/17/2017 Get another chunk of photos (15 or whatever is left in the list. whichever is less).

                // TODO: 7/17/2017 add ads
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
             * No idea what this does.
             * */
            @Override
            public void onScroll(float v) {
                View view = swipeFlingAdapterView.getSelectedView();
            /* REMOVE Comments below to add transparency effect on thumbs up/down and rating numbers
               view.findViewById(R.id.thumb_up).setAlpha(v < 0 ? -v : 0);
               view.findViewById(R.id.thumb_down).setAlpha(v > 0 ? v : 0);
               view.findViewById(R.id.amount_thumbs_up).setAlpha(v < 0 ? -v : 0);
               view.findViewById(R.id.amount_thumbs_down).setAlpha(v > 0 ? v : 0); */
            }

        });

        // Optionally add an OnItemClickListener
        swipeFlingAdapterView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Log.d(LOG_TAG, "Item clicked");
            }
        });
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
        swipeViewAdapter.notifyDataSetChanged();
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