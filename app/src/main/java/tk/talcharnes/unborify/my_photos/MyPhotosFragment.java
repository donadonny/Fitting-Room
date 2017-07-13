package tk.talcharnes.unborify.my_photos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tk.talcharnes.unborify.Photo;
import tk.talcharnes.unborify.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyPhotosFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Photo> photoList;

    public MyPhotosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_my_photos, container, false);
        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.my_photos_recyclerview);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        photoList = new ArrayList<>();
        Photo photo = new Photo();
        photo.setLikes(1);
        photoList.add(photo);
        Photo photo2 = new Photo();
        photo2.setLikes(2);
        photo2.setUrl("blah");

        photoList.add(photo2);
        Photo photo3 = new Photo();
        photo3.setLikes(3);

        photoList.add(photo3);

        mAdapter = new MyPhotoAdapter(photoList, getContext());
        mRecyclerView.setAdapter(mAdapter);



        return rootview;
    }
}
