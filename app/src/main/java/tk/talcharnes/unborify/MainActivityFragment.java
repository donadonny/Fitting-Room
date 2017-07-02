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
        ArrayList<Photo> photoListTest;
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
            DatabaseReference photoReference = firebaseDatabase.getReference().child("Photos");
            // Read from the database
            photoReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
//                    Photo photo = new Photo();
//                    while (dataSnapshot.getChildren().iterator().hasNext()){
//                        photo = dataSnapshot.getValue(Photo.class);
//
//                    }

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
                                photoListTest.add(photo);
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
            photoListTest = new ArrayList<Photo>();
//            Photo photo = new Photo();
//            photo.setOccasion_subtitle("TESTING NOW");
//            photo.setUrl("https://s-media-cache-ak0.pinimg.com/736x/a5/98/1f/a5981fcc09689034ec9dc9201c9787f5--taco-taco-work-memes");
//            photo.setLikes(1);
//            photo.setDislikes(2);
//            photo.setReports(1);
//            photo.setUser("DSLKDJFLDSJDKSL");
//            photoListTest.add(photo);
//            Photo photo2 = new Photo();
//            photo2.setOccasion_subtitle("TESTING THIS NOW");
//            photo2.setUrl("https://thetab.com/blogs.dir/91/files/2017/01/maxresdefault-1");
//            photo2.setLikes(2);
//            photo2.setDislikes(4);
//            photo2.setReports(3);
//            photo2.setUser("AAAAAAAA");
//            photoListTest.add(photo2);
//            Photo photo3 = new Photo();
//            photo3.setOccasion_subtitle("WHATWHAT?");
//            photo3.setLikes(1);
//            photo3.setDislikes(2);
//            photo3.setUser("FFFFFFF");
//            photo3.setUrl("https://s-media-cache-ak0.pinimg.com/736x/00/ec/ff/00ecff1aed4858fb5eae5b2573d347e9");
//            photoListTest.add(photo3);
//

            swipeViewAdapter = new SwipeViewAdapter(getContext(), photoListTest);


            //set the listener and the adapter
            swipeFlingAdapterView.setAdapter(swipeViewAdapter);


            swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // this is the simplest way to delete an object from the Adapter (/AdapterView)
                    Log.d("LIST", "removed object!");
                    photoListTest.remove(0);
                    swipeViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLeftCardExit(Object dataObject) {
                    //Do something on the left!
                    //You also have access to the original object.
                    //If you want to use it just cast it (String) dataObject
                    Log.d(LOG_TAG, "Left card Exit");
                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    Log.d(LOG_TAG, "Right card Exit");
                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    // Ask for more data here
//                    al.add("item number " + i);
//                    i++;
//                    swipeViewAdapter.notifyDataSetChanged();
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