package tk.talcharnes.unborify.MainNavigationFragments.Following;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.InfinitePlaceHolderView;
import java.util.ArrayList;
import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import tk.talcharnes.unborify.Models.PhotoModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.GlideLoader2;

/**
 * Created by Khuram Chaudhry on 11/1/17.
 *
 * This adapter is designed for FollowingFragment and loads the avatar image, name, and the photos
 *      for each user the main user is following.
 */

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ItemRowHolder> {

    public static final String TAG = FollowingAdapter.class.getSimpleName();

    private Activity mActivity;
    private ArrayList<String> userUids;
    private IImageLoader imageLoader;


    /**
     *  This is the default constructor.
     *  @param mActivity: The main activity.
     *  @param userUids: String list containing uids of users.
     */
    public FollowingAdapter(Activity mActivity, ArrayList<String> userUids) {
        this.mActivity = mActivity;
        this.userUids = userUids;
        imageLoader = new GlideLoader2();
    }

    /**
     * This function loads the layout to used for each ItemRowHolder object.
     */
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user_info, parent,
                false);
        return new ItemRowHolder(v);
    }

    /**
     * This function grabs data and apply's data to each ItemRowHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, int i) {
        int position = holder.getAdapterPosition();

        // Getting the current uid from the list of uids.
        final String uid = userUids.get(position);

        // Checks if the uid is not empty before filling up the data. Conditionals protects against
        //    a bug in which the an empty uid was received from the database.
        if(!uid.isEmpty()) {

            // Setting the user profile image and grabbing user's name from the database.
            DatabaseContants.getUserNameRef(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.getValue(String.class);
                        if (name != null) {
                            imageLoader.loadImage(holder.userPhoto, uid, name);
                            holder.userName.setText(name);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    DatabaseContants.logDatabaseError(TAG, databaseError);
                }
            });

            holder.mLoadMoreView.getBuilder().setHasFixedSize(false).setItemViewCacheSize(10)
                    .setLayoutManager(new LinearLayoutManager(mActivity,
                            LinearLayoutManager.HORIZONTAL, false));

            // Grabbing the user's photos from the database.
            DatabaseContants.retrievePhotosFromDatabaseUsingUrl(TAG, mActivity, holder.itemView,
                    DatabaseContants.getPhotoRef().orderByChild(PhotoModel.USER_KEY)
                            .equalTo(uid), holder.mLoadMoreView,
                    R.string.no_image_title_3, false);
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
        private InfinitePlaceHolderView mLoadMoreView;

        ItemRowHolder(View view) {
            super(view);
            if (!userUids.isEmpty()) {
                this.userPhoto = (AvatarView) view.findViewById(R.id.avatarImage);
                this.userName = (TextView) view.findViewById(R.id.textview);
                this.mLoadMoreView = (InfinitePlaceHolderView) view.findViewById(R.id.loadMoreView);
            }
        }
    }
}