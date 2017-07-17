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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

    /**
     * A placeholder fragment containing a simple view.
     */
    public class MainActivityFragment extends Fragment {
        FloatingActionButton fab;
        SwipeFlingAdapterView swipeFlingAdapterView;
        SwipeViewAdapter swipeViewAdapter;
        private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
        private AdView mAdView;
        private AdRequest mAdRequest;
        ArrayList<Photo> photoList;
        private int i = 0;
        static final int REQUEST_IMAGE_CAPTURE = 1;

        //        For Firebase Auth
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthListener;
        public static final int RC_SIGN_IN = 1;


        public MainActivityFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//            For firebase auth
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user != null) {
                        // User is signed in
                        String userID = user.getUid();

                        Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User is signed out
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setIsSmartLockEnabled(false)
                                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                        .build(),
                                RC_SIGN_IN);
                        Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                    }

                }
            };

            // The following code is a test

            swipeFlingAdapterView = (SwipeFlingAdapterView) rootView.findViewById(R.id.frame);
            // add entertaining things to arraylist using photoList.add()


            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference photoReference = firebaseDatabase.getReference().child("Photos");

            // Read from the database
            photoReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    Map<String, Object> objectMap = (HashMap<String, Object>)
                            dataSnapshot.getValue();
                    if (objectMap != null) {
                        for (Object obj : objectMap.values()) {
                            if (obj instanceof Map) {
                                Map<String, Object> mapObj = (Map<String, Object>) obj;
                                Photo photo = new Photo();
                                photo.setOccasion_subtitle((String) mapObj.get(Photo.OCCASION_SUBTITLE_KEY));
                                photo.setUrl((String) mapObj.get(Photo.URL_KEY));
                                photo.setUser((String) mapObj.get(Photo.USER_KEY));
                                photo.setLikes((Long) mapObj.get(Photo.LIKES_KEY));
                                photo.setDislikes((Long) mapObj.get(Photo.DISLIKES_KEY));
                                photo.setReports((Long) mapObj.get(Photo.REPORTS_KEY));
                                photoList.add(photo);
                            }
                        }
                    }

                    swipeViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(LOG_TAG, "Failed to read value.", error.toException());
                }
            });



            //choose your favorite adapter
            photoList = new ArrayList<Photo>();

            swipeViewAdapter = new SwipeViewAdapter(getContext(), photoList);


            //set the listener and the adapter
            swipeFlingAdapterView.setAdapter(swipeViewAdapter);


            swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // this is the simplest way to delete an object from the Adapter (/AdapterView)
                    Log.d("LIST", "removed object!");
                    photoList.remove(0);
                    swipeViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLeftCardExit(Object dataObject) {
                    //Do something on the left!
                    //You also have access to the original object.
                    //If you want to use it just cast it (String) dataObject
//                  // TODO: 7/17/2017 add if/else statement if the photo is from the user it does nothing but go to next photo. Else it votes.
                    Photo photo = (Photo) dataObject;
                    photo.setDislikes(photo.getDislikes() + 1);
                    photoReference.child(photo.getUrl()).setValue(photo);

                    Log.d(LOG_TAG, "Left card Exit");
                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    Photo photo = (Photo) dataObject;
                    //                  // TODO: 7/17/2017 add if/else statement if the photo is from the user it does nothing but go to next photo. Else it votes.
                    photo.setLikes(photo.getLikes() + 1);
                    photoReference.child(photo.getUrl()).setValue(photo);
                    Log.d(LOG_TAG, "Right card Exit");
                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    // TODO: 7/17/2017 Get another chunk of photos (15 or whatever is left in the list. whichever is less). Then notify dataset changed
                    // TODO: 7/17/2017 add ads
                    Log.d("LIST", "notified");
                }

                @Override
                public void onScroll(float v) {
                    View view = swipeFlingAdapterView.getSelectedView();
                    view.findViewById(R.id.thumb_up).setAlpha(v < 0 ? -v : 0);
                    view.findViewById(R.id.thumb_down).setAlpha(v > 0 ? v : 0);
                }
            });

            // Optionally add an OnItemClickListener
            swipeFlingAdapterView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {
                    Log.d(LOG_TAG, "Item clicked");
                }
            });
            //        Test over

            ////        Load ad
            mAdView = (AdView) rootView.findViewById(R.id.adView);
            mAdRequest = new AdRequest.Builder().build();
            mAdView.loadAd(mAdRequest);


            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            mAuth.addAuthStateListener(mAuthListener);
        }

        @Override
        public void onStop() {
            super.onStop();
            if (mAuthListener != null) {
                mAuth.removeAuthStateListener(mAuthListener);
            }
        }

    }