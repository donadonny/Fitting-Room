package tk.talcharnes.unborify.MainNavigationFragments;

import android.content.Context;
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

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;

import java.util.ArrayList;
import java.util.List;

import tk.talcharnes.unborify.Models.PhotoModel;
import tk.talcharnes.unborify.PhotoCard.AdCard;
import tk.talcharnes.unborify.PhotoCard.PhotoCard;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.Utils;

/**
 * Created by khuramchaudhry on 9/29/17.
 */

public class TrendingFragment extends Fragment {

    private static final String TAG = TrendingFragment.class.getSimpleName();

    private String userId, userName;
    private DatabaseReference photoReference;
    private DatabaseReference reportRef;

    private View rootView;
    private SwipePlaceHolderView mSwipeView;
    private Button refreshButton;
    private TextView refresh_textview;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_trending, container, false);
        photoReference = FirebaseConstants.getRef().child(FirebaseConstants.PHOTOS);
        reportRef = FirebaseConstants.getRef().child(FirebaseConstants.REPORTS);

        initializeBasicSetup();

        initializeSwipePlaceHolderView();
        Log.d(TAG, "Load trending");

        return rootView;
    }

    /**
     * Initializes Basic stuff. The photoList, mAdView, and the fab buttons.
     */
    private void initializeBasicSetup() {
        FirebaseUser user = FirebaseConstants.getUser();
        userId = user.getUid();
        userName = user.getDisplayName();

        // ReportModel Fab button
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
        mContext = getContext();

        final long startTime = System.currentTimeMillis();

        Query query;

        query = photoReference.orderByChild(PhotoModel.LIKES_KEY).limitToLast(24);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<PhotoModel> photoModels = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        PhotoModel photoModel = child.getValue(PhotoModel.class);
                        if(photoModel != null) {
                            photoModels.add(photoModel);
                        }
                    }

                    int count = photoModels.size();
                    while(count > 0) {
                        mSwipeView.addView(new PhotoCard(mContext, photoModels.get(count-1), mSwipeView,
                                userId, userName, photoReference, reportRef));
                        if (count - 1 % 8 == 0) {
                            mSwipeView.addView(new AdCard(mContext, mSwipeView));
                            mSwipeView.addView(new AdCard(mContext, mSwipeView));
                        }
                        count--;
                    }
                    photoModels.clear();

                    Log.d(TAG, "Retrieved data");
                    mSwipeView.refreshDrawableState();
                    final long endTime = System.currentTimeMillis();
                    Log.d(TAG, "Data Load time: " + (endTime - startTime));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}