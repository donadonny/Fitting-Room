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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tk.talcharnes.unborify.Photo;
import tk.talcharnes.unborify.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyPhotosFragment extends Fragment {
    private static final String LOG_TAG = MyPhotosFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Photo> photoList;
    String user;

    public MyPhotosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_my_photos, container, false);
        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.my_photos_recyclerview);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();


        photoList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // TODO: 7/17/2017 Get reference to users (NOT PHOTOS), get list of photos, AND THEN find the photos from the photos section of the database to return to user 
        final DatabaseReference photoReference = firebaseDatabase.getReference().child("Photos");
        final DatabaseReference userPhotos = firebaseDatabase.getReference().child("users").child(user);

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
                                                         mAdapter.notifyDataSetChanged();
                                                     }
                                                 }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "Failed to read value.", databaseError.toException());
            }
        });



        mAdapter = new MyPhotoAdapter(photoList, getContext());
        mRecyclerView.setAdapter(mAdapter);



        return rootview;
    }
}
