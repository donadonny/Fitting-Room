package tk.talcharnes.unborify.MainNavigationFragments;

import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;

import java.util.ArrayList;

import tk.talcharnes.unborify.Photo;
import tk.talcharnes.unborify.PhotoCard;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.Utils;

/**
 * Created by khuramchaudhry on 9/29/17.
 *
 */


public class FollowingFragment extends Fragment {

    private static final String TAG = FollowingFragment.class.getSimpleName();

    private View rootView;
    private SwipePlaceHolderView mSwipeView;
    private Button refreshButton;
    private TextView refresh_textview, noImagesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_following, container, false);

        Log.d(TAG, "Load Following Fragment");

        initializeBasicSetup();

        initializeSwipePlaceHolderView();

        return rootView;
    }
    /**
     * Initializes Basic stuff. The photoList, mAdView, and the fab buttons.
     */
    private void initializeBasicSetup() {
        noImagesTextView = rootView.findViewById(R.id.noImagesTitle);

        refreshButton = rootView.findViewById(R.id.refreshBtn);

        refresh_textview = rootView.findViewById(R.id.refreshTitle);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshButton.setVisibility(View.GONE);
                refresh_textview.setVisibility(View.GONE);
                getPhotos();
            }
        });

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

        mSwipeView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
                Log.d(TAG, "Swipe");
                //do something when the count changes to some specific value.
                //For Example: Call server to fetch more data when count is zero
                if (count < 1) {
                    Log.d(TAG, "Empty SwipeView");
                    Log.d(TAG, "No more photos");
                    refreshButton.setVisibility(View.VISIBLE);
                    refresh_textview.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Get photos from the database and adds it to the SwipePlaceHolderView.
     */
    private void getPhotos() {
        final long startTime = System.currentTimeMillis();
        Query query = FirebaseConstants.getRef().child(FirebaseConstants.PHOTOS)
                .orderByChild("votes/"+FirebaseConstants.getUser().getUid()).equalTo("likes");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userId = FirebaseConstants.getUser().getUid();
                String userName = FirebaseConstants.getUserName();
                DatabaseReference photoReference = FirebaseConstants.getRef()
                        .child(FirebaseConstants.PHOTOS);
                DatabaseReference reportRef = FirebaseConstants.getRef()
                        .child(FirebaseConstants.REPORTS);

                int count = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Photo photo = snapshot.getValue(Photo.class);
                    mSwipeView.addView(new PhotoCard(getActivity(), photo, mSwipeView,
                            userId, userName, photoReference, reportRef));
                    Log.d(TAG, snapshot.getKey());
                    count++;
                }
                if(count < 1) {
                    noImagesTextView.setVisibility(View.VISIBLE);
                }

                Log.d(TAG, "Got data.");
                mSwipeView.refreshDrawableState();
                final long endTime = System.currentTimeMillis();
                Log.d(TAG, "Total execution time: " + (endTime - startTime));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}