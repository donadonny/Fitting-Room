package tk.talcharnes.unborify.OtherFragmentActivities.MyPhotos;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.InfinitePlaceHolderView;
import java.util.ArrayList;
import java.util.Collections;
import tk.talcharnes.unborify.Models.PhotoModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Khuram Chaudhry on 9/29/17.
 * This fragment gets the user's photos from the database.
 */
public class MyPhotosFragment extends Fragment {

    private static final String LOG_TAG = MyPhotosFragment.class.getSimpleName();

    private View rootview;
    private InfinitePlaceHolderView mLoadMoreView;
    private ArrayList<PhotoModel> photoModelList;
    private String userId, userName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_my_photos, container, false);
        mLoadMoreView = (InfinitePlaceHolderView) rootview.findViewById(R.id.loadMoreView);

        FirebaseUser user = DatabaseContants.getCurrentUser();
        userId = user.getUid();
        userName = user.getDisplayName();

        photoModelList = new ArrayList<>();

        setUpPhotos();

        return rootview;
    }

    /**
     * The method gets the photos.
     */
    public void setUpPhotos() {
        DatabaseContants.getPhotoRef().orderByChild(PhotoModel.USER_KEY).equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            photoModelList.clear();
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                PhotoModel photoModel = child.getValue(PhotoModel.class);
                                if (photoModel != null) {
                                    photoModelList.add(photoModel);
                                    System.out.println(photoModel.getUrl());
                                }
                            }
                            Collections.reverse(photoModelList);

                            for (int i = 0; i < LoadMoreView.LOAD_VIEW_SET_COUNT && i < photoModelList.size(); i++) {
                                mLoadMoreView.addView(new PhotoView(getActivity(), photoModelList.get(i),
                                        userId, userName, mLoadMoreView));
                            }
                            if (photoModelList.size() > LoadMoreView.LOAD_VIEW_SET_COUNT) {
                                mLoadMoreView.setLoadMoreResolver(new LoadMoreView(mLoadMoreView,
                                        photoModelList, userId, userName));
                            }
                        } else {
                            setDefaultView();
                        }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                setDefaultView();
                Log.e(LOG_TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    /**
     * The method set the view if there is no photos belonging to the user.
     */
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
