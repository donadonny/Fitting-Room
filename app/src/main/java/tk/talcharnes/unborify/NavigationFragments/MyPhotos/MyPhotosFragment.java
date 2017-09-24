package tk.talcharnes.unborify.NavigationFragments.MyPhotos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.InfinitePlaceHolderView;

import java.util.ArrayList;
import java.util.Collections;

import tk.talcharnes.unborify.Photo;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyPhotosFragment extends Fragment {

    private static final String LOG_TAG = MyPhotosFragment.class.getSimpleName();

    private View rootview;
    private InfinitePlaceHolderView mLoadMoreView;
    private ArrayList<Photo> photoList;
    private String userId, userName;

    public MyPhotosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_my_photos, container, false);
        mLoadMoreView = (InfinitePlaceHolderView) rootview.findViewById(R.id.loadMoreView);

        FirebaseUser user = FirebaseConstants.getUser();
        if (user != null) {
            userId = user.getUid();
            userName = user.getDisplayName();
        }
        photoList = new ArrayList<>();

        setUpPhotos();

        return rootview;
    }

    public void setUpPhotos() {
        Query query = FirebaseConstants.getRef().child("Photos").orderByChild(Photo.USER_KEY)
                .equalTo(userId);

        // Read from the database
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    photoList.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Photo photo = child.getValue(Photo.class);
                        if (photo != null) {
                            photoList.add(photo);
                            System.out.println(photo.getUrl());
                        }
                    }
                    Collections.reverse(photoList);

                    for (int i = 0; i < LoadMoreView.LOAD_VIEW_SET_COUNT && i < photoList.size(); i++) {
                        mLoadMoreView.addView(new PhotoView(getActivity(), photoList.get(i), userId, userName));
                    }
                    if (photoList.size() > LoadMoreView.LOAD_VIEW_SET_COUNT) {
                        mLoadMoreView.setLoadMoreResolver(new LoadMoreView(mLoadMoreView, photoList, userId, userName));
                    }
                } else {
                    setDefaultView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "Failed to read value.", databaseError.toException());
                setDefaultView();
            }
        });
    }

    public void setDefaultView() {
        LinearLayout linearLayout = (LinearLayout) rootview.findViewById(R.id.activity_main);
        TextView textView = new TextView(getActivity());
        textView.setText("You have no Photos. :(");
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(textView);
    }
}
