package tk.talcharnes.unborify.OtherFragmentActivities.Notifications;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import tk.talcharnes.unborify.CommentActivity;
import tk.talcharnes.unborify.MyTransformation;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.myNotifications;

/**
 * Created by khuramchaudhry on 9/17/17.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ItemRowHolder> {

    private Context mContext;
    private List<myNotifications> dataList;

    NotificationAdapter(Context context, List<myNotifications> dataList) {
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
        final myNotifications notification = dataList.get(position);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child(FirebaseConstants.IMAGES).child(notification.getPhotoUrl());
        FirebaseConstants.loadImageUsingGlide(mContext, holder.imageView, storageRef,
                holder.progressBar, 0);
        holder.message.setText(notification.getMessage());
        FirebaseConstants.getRef().child(FirebaseConstants.USERS).child(notification.getSenderID())
                .child(FirebaseConstants.USERNAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            holder.senderID.setText(dataSnapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        holder.notificationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseConstants.getUser();
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
