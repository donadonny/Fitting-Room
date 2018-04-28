package tk.talcharnes.unborify.UserProfile;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.util.List;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.StorageConstants;

/**
 * Created by Khuram Chaudhry on 10/19/17.
 * This is a RecyclerView Adapter that displays a list of photo cards with the current user's rating.
 */

public class UserProfileAdapter
        extends RecyclerView.Adapter<UserProfileAdapter.SingleItemRowHolder> {

    public static final String TAG = UserProfileAdapter.class.getSimpleName();

    private Context mContext;
    private String uid;
    private List<String> urlList;
    private boolean fashionPhotos;
    private StorageReference storageReference;

    /**
     * This class binds the UI elements for each item in the RecyclerView.
     */
    class SingleItemRowHolder extends RecyclerView.ViewHolder {

        private ImageView photo, userRating;
        private ProgressBar progressBar;

        SingleItemRowHolder(View view) {
            super(view);
            if (!urlList.isEmpty()) {
                photo = (ImageView) view.findViewById(R.id.photoImageView);
                userRating = (ImageView) view.findViewById(R.id.ratingImageView);
                progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            }
        }

    }

    /**
     * RecyclerView Constructor.
     * @param context - The Application Context of the main Activity.
     * @param uid - The user id of the current user.
     * @param urls - The list of photo urls.
     * @param fashionPhotos -  A boolean value that tells the Adapter to display fashion photos if
     *          true and profile photos if false.
     */
    public UserProfileAdapter(Context context, String uid, List<String> urls, boolean fashionPhotos) {
        this.urlList = urls;
        this.mContext = context;
        this.uid = uid;
        this.fashionPhotos = fashionPhotos;
        storageReference = StorageConstants.getRef().child(((fashionPhotos) ?
                StorageConstants.IMAGES : StorageConstants.PROFILE_IMAGE));
    }

    /**
     * This functions sets the view for the items in the Adapter.
     */
    @Override
    public UserProfileAdapter.SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_card, viewGroup, false);
        return new UserProfileAdapter.SingleItemRowHolder(v);
    }

    /**
     * This function sets the UI elements for each item in the RecyclerView.
     */
    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, int i) {
        StorageReference photoRef = storageReference.child(urlList.get(i) + ".webp");
        Log.d(TAG, photoRef.getPath());
        StorageConstants.loadImageUsingGlide(mContext, holder.photo, photoRef,
                holder.progressBar);

        DatabaseContants.getVotesRef().child(urlList.get(i)).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(fashionPhotos) {
                            holder.photo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openCommentActivity(urlList.get(index));
                                }
                            });
                        }
                        if (dataSnapshot.exists()) {
                            String rating = String.valueOf(dataSnapshot.getValue());
                            if (rating.equals("likes")) {
                                holder.userRating.setBackgroundColor(ContextCompat
                                        .getColor(mContext, R.color.bg_screen2));
                                holder.userRating.setImageDrawable(ContextCompat
                                        .getDrawable(mContext, R.drawable.ic_thumb_up_white_24dp));
                            } else if (rating.equals("dislikes")) {
                                holder.userRating.setBackgroundColor(ContextCompat
                                        .getColor(mContext, R.color.bg_screen1));
                                holder.userRating.setImageDrawable(ContextCompat
                                        .getDrawable(mContext, R.drawable.ic_thumb_down_white_24dp));
                            }
                        } else {
                            if(fashionPhotos) {
                                holder.userRating.setVisibility(View.INVISIBLE);
                            } else {
                                holder.userRating.setBackgroundColor(ContextCompat
                                        .getColor(mContext, R.color.bg_screen4));
                                holder.userRating.setImageDrawable(ContextCompat
                                        .getDrawable(mContext, R.drawable.ic_following));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getDetails());
                    }
                });

    }

    /**
     * This function returns the number of items in the Adapter.
     */
    @Override
    public int getItemCount() {
        return urlList.size();
    }

}