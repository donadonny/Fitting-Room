package tk.talcharnes.unborify.UserProfile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.GlideLoader2;

/**
 * Created by Khuram Chaudhry on 10/19/17.
 * This is a RecyclerView Adapter that displays a list of photo cards.
 */

public class UserProfileAdapter
        extends RecyclerView.Adapter<UserProfileAdapter.SingleItemRowHolder> {

    public static final String TAG = UserProfileAdapter.class.getSimpleName();

    private List<String> urlList;
    private IImageLoader imageLoader;

    /**
     * This class binds the UI elements for each item in the RecyclerView.
     */
    class SingleItemRowHolder extends RecyclerView.ViewHolder {

        private AvatarView avatarImage;
        private TextView userNameText;

        SingleItemRowHolder(View view) {
            super(view);
            if (!urlList.isEmpty()) {
                avatarImage = (AvatarView) view.findViewById(R.id.avatarImage);
                userNameText = (TextView) view.findViewById(R.id.userNameText);
            }
        }

    }

    /**
     * RecyclerView Constructor.
     * @param urls - The list of photo urls.
     */
    public UserProfileAdapter(List<String> urls) {
        this.urlList = urls;
        imageLoader = new GlideLoader2();
    }

    /**
     * This functions sets the view for the items in the Adapter.
     */
    @NonNull
    @Override
    public UserProfileAdapter.SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_avater_view, viewGroup, false);
        return new UserProfileAdapter.SingleItemRowHolder(v);
    }

    /**
     * This function sets the UI elements for each item in the RecyclerView.
     */
    @Override
    public void onBindViewHolder(@NonNull final SingleItemRowHolder holder, int i) {
        final int position = holder.getAdapterPosition();
        DatabaseContants.getUserNameRef(urlList.get(position))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String name = dataSnapshot.getValue(String.class);
                            if (name != null) {
                                imageLoader.loadImage(holder.avatarImage, urlList.get(position),
                                        name);
                                holder.userNameText.setText(name);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage());
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