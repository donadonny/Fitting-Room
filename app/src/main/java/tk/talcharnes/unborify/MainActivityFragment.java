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
        private String userId;
        private String oldestPostId;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference photoReference = firebaseDatabase.getReference().child("Photos");
        boolean initializePhotoList;

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
                        userId = user.getUid();

                        Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + userId);
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



            // Read from the database
            photoReference.limitToFirst(6).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    initializePhotoList = true;
                    addPhotosToArrayList(dataSnapshot);
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
                    final Photo photo = (Photo) dataObject;
                    final String dislikeStringKey = "dislike";
                    final String likeStringKey = "like";
                    if(!userId.equals(photo.getUser())) {

                        photoReference.child(photo.getUrl()).child("Votes").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (snapshot.getValue().toString().equals(likeStringKey)) {
                                        photo.setLikes(photo.getLikes() - 1);
                                        photo.setDislikes(photo.getDislikes() + 1);
                                        photoReference.child(photo.getUrl()).setValue(photo);

                                        photoReference.child(photo.getUrl()).child("Votes").child(userId).setValue(dislikeStringKey);

                                        Log.d(LOG_TAG, "snapshot value is like");
                                    } else {
                                        Log.d(LOG_TAG, "snapshot value is already dislike");
                                    }

                                } else {
                                    photo.setDislikes(photo.getDislikes() + 1);
                                    photoReference.child(photo.getUrl()).setValue(photo);
                                    photoReference.child(photo.getUrl()).child("Votes").child(userId).setValue(dislikeStringKey);
                                    Log.d(LOG_TAG, "snapshot value does not exist");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(LOG_TAG, "cancelled with error - " + databaseError);
                            }

                        });

                    }
                    Log.d(LOG_TAG, "Left card Exit");
                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    // TODO: 7/17/2017 add if/else statement if the photo is from the user it does nothing but go to next photo. Else it votes.

                    final Photo photo = (Photo) dataObject;
                    final String dislikeStringKey = "dislike";
                    final String likeStringKey = "like";
                    if(!userId.equals(photo.getUser())) {
                        photoReference.child(photo.getUrl()).child("Votes").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (snapshot.getValue().toString().equals(likeStringKey)) {
                                        Log.d(LOG_TAG, "snapshot value is already like");
                                    } else {
                                        photo.setLikes(photo.getLikes() + 1);
                                        photo.setDislikes(photo.getDislikes() - 1);
                                        photoReference.child(photo.getUrl()).setValue(photo);
                                        photoReference.child(photo.getUrl()).child("Votes").child(userId).setValue(likeStringKey);

                                        Log.d(LOG_TAG, "snapshot value is dislike");
                                    }

                                } else {
                                    photo.setLikes(photo.getLikes() + 1);
                                    photoReference.child(photo.getUrl()).setValue(photo);
                                    photoReference.child(photo.getUrl()).child("Votes").child(userId).setValue(likeStringKey);
                                    Log.d(LOG_TAG, "snapshot value does not exist");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(LOG_TAG, "cancelled with error - " + databaseError);
                            }

                        });
                    }

                    Log.d(LOG_TAG, "Right card Exit");
                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    // TODO: 7/17/2017 Get another chunk of photos (15 or whatever is left in the list. whichever is less).
                    // Then notify dataset changed
                    // TODO: 7/17/2017 add ads
                    photoReference.orderByChild(Photo.URL_KEY).startAt(oldestPostId).limitToFirst(6).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                               initializePhotoList = false;
                               addPhotosToArrayList(dataSnapshot);
                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {
                               Log.w(LOG_TAG, "Failed to read value.", databaseError.toException());
                           }
                       });


                            Log.d("LIST", "notified");
                }

                @Override
                public void onScroll(float v) {
                    View view = swipeFlingAdapterView.getSelectedView();


//                    REMOVE Comments below to add transparency effect on thumbs up/down and rating numbers

//                    view.findViewById(R.id.thumb_up).setAlpha(v < 0 ? -v : 0);
//                    view.findViewById(R.id.thumb_down).setAlpha(v > 0 ? v : 0);
//                    view.findViewById(R.id.amount_thumbs_up).setAlpha(v < 0 ? -v : 0);
//                    view.findViewById(R.id.amount_thumbs_down).setAlpha(v > 0 ? v : 0);
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

        private void addPhotosToArrayList(DataSnapshot dataSnapshot){
            // Make int counter to delete first item in array as it is a repeat
            int i = 0;
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                if(i == 0 && !initializePhotoList){
                    i++;
                }
                else {
                    oldestPostId = child.getKey();
                    Photo photo = child.getValue(Photo.class);
                    photoList.add(photo);
                    System.out.println("here si the data==>>" + child.getKey());
                }
            }
//
//            Map<String, Object> objectMap = (HashMap<String, Object>)
//                    dataSnapshot.getValue();
//            if (objectMap != null) {
//                for (Object obj : objectMap.values()) {
//                    if (obj instanceof Map) {
//                        Map<String, Object> mapObj = (Map<String, Object>) obj;
//                        Photo photo = new Photo();
//                        photo.setOccasion_subtitle((String) mapObj.get(Photo.OCCASION_SUBTITLE_KEY));
//                        photo.setUrl((String) mapObj.get(Photo.URL_KEY));
//                        photo.setUser((String) mapObj.get(Photo.USER_KEY));
//                        photo.setLikes((Long) mapObj.get(Photo.LIKES_KEY));
//                        photo.setDislikes((Long) mapObj.get(Photo.DISLIKES_KEY));
//                        photo.setReports((Long) mapObj.get(Photo.REPORTS_KEY));
//
//                        photoList.add(photo);
//                        oldestPostId = photo.getUrl();
//                        Log.d(LOG_TAG, "oldestPostId = " + oldestPostId);
//                    }
//                }
//            }

            swipeViewAdapter.notifyDataSetChanged();

        }

    }