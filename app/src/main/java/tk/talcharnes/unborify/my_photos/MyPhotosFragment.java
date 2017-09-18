package tk.talcharnes.unborify.my_photos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tk.talcharnes.unborify.Photo;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyPhotosFragment extends Fragment {

    private static final String LOG_TAG = MyPhotosFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Photo> photoList;
    String userId, userName;

    public MyPhotosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_my_photos, container, false);
        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.my_photos_recyclerview);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userId = user.getUid();
            userName = user.getDisplayName();
        }

        photoList = new ArrayList<>();

        Query query = FirebaseConstants.getRef().child("Photos").orderByChild(Photo.USER_KEY)
                .equalTo(userId);

        // Read from the database
        query.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists()) {
                     photoList.clear();
                     for(DataSnapshot child : dataSnapshot.getChildren()) {
                         Photo photo = child.getValue(Photo.class);
                         photoList.add(photo);
                         System.out.println(photo.getUrl());
                     }
                     Collections.reverse(photoList);
                     mAdapter.notifyDataSetChanged();
                 }
             }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "Failed to read value.", databaseError.toException());
            }
        });



        mAdapter = new MyPhotoAdapter(photoList, getContext(), userId, userName);
        mRecyclerView.setAdapter(mAdapter);



        return rootview;
    }
}
