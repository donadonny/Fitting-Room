    package tk.talcharnes.unborify;

    import android.content.Intent;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.support.annotation.NonNull;
    import android.support.design.widget.FloatingActionButton;
    import android.support.v4.app.Fragment;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;

    import com.firebase.ui.auth.AuthUI;
    import com.google.android.gms.ads.AdRequest;
    import com.google.android.gms.ads.AdView;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.lorentzos.flingswipe.SwipeFlingAdapterView;

    import java.util.ArrayList;
    import java.util.Arrays;

    /**
     * A placeholder fragment containing a simple view.
     */
    public class MainActivityFragment extends Fragment {
        FloatingActionButton fab;
        SwipeFlingAdapterView swipeFlingAdapterView;
        ArrayList<String> al;
        ArrayAdapter<String> arrayAdapter;
        private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
        private AdView mAdView;
        private AdRequest mAdRequest;
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
            // add entertaining things to arraylist using al.add()
            al = new ArrayList<String>();
            al.add("1");
            al.add("2");
            //choose your favorite adapter
            arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.swipe_layout, R.id.helloText, al);


            //set the listener and the adapter
            swipeFlingAdapterView.setAdapter(arrayAdapter);


            swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // this is the simplest way to delete an object from the Adapter (/AdapterView)
                    Log.d("LIST", "removed object!");
                    al.remove(0);
                    arrayAdapter.notifyDataSetChanged();
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
                    al.add("item number " + i);
                    i++;
                    arrayAdapter.notifyDataSetChanged();
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

            fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent shareIntent = new Intent();
//                    shareIntent.setAction(Intent.ACTION_SEND);
//                    shareIntent.putExtra(Intent.EXTRA_TEXT, "TEST");
//                    shareIntent.setType("text/plain");
//                    startActivity(shareIntent);
//                }
//            });

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

        private void dispatchTakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }