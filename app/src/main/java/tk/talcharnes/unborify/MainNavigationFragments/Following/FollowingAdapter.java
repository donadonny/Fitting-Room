package tk.talcharnes.unborify.MainNavigationFragments.Following;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import agency.tango.android.avatarview.views.AvatarView;
import tk.talcharnes.unborify.Models.UserModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.UserProfile.UserProfileAdapter;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.StorageConstants;

/**
 * Created by Khuram Chaudhry on 11/1/17.
 *
 * This adapter is designed for FollowingFragment and loads the avatar image, name, and the photos
 *      for each user the main user is following.
 */

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ItemRowHolder> {

    public static final String TAG = FollowingAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<String> userUids;

    /**
     *  This is the default constructor.
     *  @param mContext: Context of the activity.
     *  @param userUids: String list containing uids of users.
     */
    public FollowingAdapter(Context mContext, ArrayList<String> userUids) {
        this.mContext = mContext;
        this.userUids = userUids;
    }

    /**
     * This function loads the layout to used for each ItemRowHolder object.
     */
    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent,
                false);
        return new ItemRowHolder(v);
    }

    /**
     * This function grabs data and apply's data to each ItemRowHolder.
     */
    @Override
    public void onBindViewHolder(final ItemRowHolder holder, int position) {

        // Getting the current uid from the list of uids.
        String uid = userUids.get(position);

        // Checks if the uid is not empty before filling up the data. Conditionals protects against
        //    a bug in which the an empty uid was received from the database.
        if(!uid.isEmpty()) {

            // Setting the user profile image.
            StorageConstants.loadProfileImage(mContext, holder.userPhoto, uid);

            // Grabbing user's name from the database.
            DatabaseContants.getUserRef(uid).child(UserModel.NAME_KEY)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (DatabaseContants.checkRefValue(dataSnapshot)) {
                                holder.userName.setText(dataSnapshot.getValue(String.class));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            DatabaseContants.logDatabaseError(TAG, databaseError);
                        }
                    });

            // Grabbing the user's photos from the database.
            DatabaseContants.getUserPhotos(uid, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> urls = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        urls.add(snapshot.getKey());
                    }
                    if (urls.size() > 0) {
                        holder.photoList.setLayoutManager(new
                                LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL,
                                false));
                        holder.photoList.setHasFixedSize(false);
                        UserProfileAdapter adapter = new UserProfileAdapter(mContext,
                                DatabaseContants.getCurrentUser().getUid(), urls, true);
                        holder.photoList.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    DatabaseContants.logDatabaseError(TAG, databaseError);
                }
            });
        }

    }

    /**
     * This function returns number of the ItemRowHolder objects in the adapter.
     */
    @Override
    public int getItemCount() {
        return userUids.size();
    }

    /**
     * ItemRowHolder class.
     */
    class ItemRowHolder extends RecyclerView.ViewHolder {

        private AvatarView userPhoto;
        private TextView userName;
        private RecyclerView photoList;

        ItemRowHolder(View view) {
            super(view);
            if (!userUids.isEmpty()) {
                this.userPhoto = (AvatarView) view.findViewById(R.id.avatarImage);
                this.userName = (TextView) view.findViewById(R.id.textview);
                this.photoList = (RecyclerView) view.findViewById(R.id.my_recycler_view);
            }
        }
    }
}