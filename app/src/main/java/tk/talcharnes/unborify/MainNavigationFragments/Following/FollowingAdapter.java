package tk.talcharnes.unborify.MainNavigationFragments.Following;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import agency.tango.android.avatarview.views.AvatarView;
import tk.talcharnes.unborify.Models.Photo;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.UserProfile.UserProfileAdapter;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;

/**
 * Created by khuramchaudhry on 11/1/17.
 */

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ItemRowHolder> {

    public static final String TAG = FollowingAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<String> userUids;
    private String uid;

    public FollowingAdapter(Context mContext, ArrayList<String> userUids, String uid) {
        this.mContext = mContext;
        this.userUids = userUids;
        this.uid = uid;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent,
                false);
        return new ItemRowHolder(v);    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, int position) {
        String uid = userUids.get(position);
        StorageReference photoRef = FirebaseStorage.getInstance().getReference()
                .child(FirebaseConstants.PROFILE_IMAGE).child(uid+".webp");
        Log.d(TAG, photoRef.getPath());
        FirebaseConstants.loadImageUsingGlide(mContext, holder.userPhoto, photoRef);
        FirebaseConstants.getRef().child(FirebaseConstants.USERS).child(uid)
                .child(FirebaseConstants.USERNAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    holder.userName.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(!uid.isEmpty()) {
            Log.d(TAG, "Loading user Photos");
            FirebaseConstants.getRef().child(FirebaseConstants.PHOTOS)
                    .orderByChild(Photo.USER_KEY).equalTo(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> urls = new ArrayList<String>();
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                urls.add(snapshot.getKey());
                                Log.d(TAG, snapshot.getKey());
                            }
                            if(urls.size() > 0) {
                                holder.photoList.setLayoutManager(new
                                        LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL,
                                        false));
                                holder.photoList.setHasFixedSize(false);
                                UserProfileAdapter adapter = new UserProfileAdapter(mContext,
                                        FirebaseConstants.getUser().getUid(), urls, "Photos");
                                holder.photoList.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }


    }

    @Override
    public int getItemCount() {
        return userUids.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {

        private AvatarView userPhoto;
        private TextView userName;
        private RecyclerView photoList;

        ItemRowHolder(View view) {
            super(view);
            if(!userUids.isEmpty()) {
                this.userPhoto = (AvatarView) view.findViewById(R.id.avatarImage);
                this.userName = (TextView) view.findViewById(R.id.textview);
                this.photoList = (RecyclerView) view.findViewById(R.id.my_recycler_view);
            }
        }
    }
}