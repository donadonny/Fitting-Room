package tk.talcharnes.unborify.OtherFragmentActivities.Notifications;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.util.List;
import tk.talcharnes.unborify.Models.NotificationModel;
import tk.talcharnes.unborify.Models.UserModel;
import tk.talcharnes.unborify.PhotoCard.Comments.CommentActivity;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.StorageConstants;

/**
 * Created by Khuram Chaudhry on 9/17/17.
 * This class sets up each notification to a visual card.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ItemRowHolder> {

    public static final String TAG = NotificationAdapter.class.getSimpleName();

    private Context mContext;
    private List<NotificationModel> dataList;

    NotificationAdapter(Context context, List<NotificationModel> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }

    @Override
    public NotificationAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_notification,
                parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.ItemRowHolder holder, int position) {
        final NotificationModel notification = dataList.get(position);

        StorageReference storageRef = StorageConstants.getImageRef(notification.getPhotoUrl());
        StorageConstants.loadImageUsingGlide(mContext, holder.imageView, storageRef,
                holder.progressBar, 0);

        holder.message.setText(notification.getMessage());

        DatabaseContants.getUserRef(notification.getSenderID()).child(UserModel.NAME_KEY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            holder.senderID.setText(dataSnapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage());
                    }
                });

        holder.notificationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = DatabaseContants.getCurrentUser();
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("url", notification.getPhotoUrl());
                intent.putExtra("photoUserID", user.getUid());
                intent.putExtra("currentUser", user.getUid());
                intent.putExtra("name", user.getDisplayName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList.isEmpty()) ? 0 : dataList.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {

        private CardView notificationCardView;
        private TextView message, senderID;
        private ImageView imageView;
        private ProgressBar progressBar;

        ItemRowHolder(View view) {
            super(view);

            if (!dataList.isEmpty()) {
                this.notificationCardView = (CardView) view.findViewById(R.id.notification_card_view);
                this.imageView = (ImageView) view.findViewById(R.id.notification_image);
                this.progressBar = (ProgressBar) view.findViewById(R.id.notification_progressbar);
                this.message = (TextView) view.findViewById(R.id.message);
                this.senderID = (TextView) view.findViewById(R.id.senderID);
            }
        }

    }

}
