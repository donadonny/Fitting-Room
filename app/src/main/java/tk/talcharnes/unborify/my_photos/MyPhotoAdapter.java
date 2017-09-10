package tk.talcharnes.unborify.my_photos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import tk.talcharnes.unborify.CommentActivity;
import tk.talcharnes.unborify.Photo;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.ZoomPhoto;

/**
 * Created by Tal on 7/12/2017.
 */

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.ViewHolder> {

    private List<Photo> mDataset;
    private Context mContext;
    private String mUserId, mUserName;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView mImageView;
        TextView likesCountView, dislikesCountView, occastionTextView;
        ImageButton commentsButton, zoomButton, menuButton;

        public ViewHolder(View v) {
            super(v);
            mImageView = v.findViewById(R.id.card_image_view);
            likesCountView = v.findViewById(R.id.likes);
            dislikesCountView = v.findViewById(R.id.dislikes);
            occastionTextView = v.findViewById(R.id.occasion_cardview_textview);
            commentsButton = v.findViewById(R.id.comments_button);
            zoomButton = v.findViewById(R.id.zoom_button);
            menuButton = v.findViewById(R.id.menu_button);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyPhotoAdapter(List<Photo> myDataset, Context context, String userId, String userName) {
        mDataset = myDataset;
        mContext = context;
        mUserId = userId;
        mUserName = userName;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyPhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_photo_card, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int  pos = position;
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Photo photo = mDataset.get(pos);
        String likes = ""+photo.getLikes();
        String dislikes = ""+photo.getDislikes();
        holder.likesCountView.setText(likes);
        holder.dislikesCountView.setText(dislikes);
        holder.occastionTextView.setText(photo.getOccasion_subtitle());
        String urlString = photo.getUrl();
        if (urlString != null && !urlString.isEmpty()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(urlString);
            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(storageRef)
                    .into(holder.mImageView);
        }

        holder.commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("url", photo.getUrl());
                intent.putExtra("photoUserID", photo.getUser());
                intent.putExtra("currentUser", mUserId);
                intent.putExtra("name", mUserName);
                mContext.startActivity(intent);            }
        });

        holder.zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ZoomPhoto.class);
                intent.putExtra("url", photo.getUrl());
                intent.putExtra("rotation", getRotation(photo.getOrientation()));
                mContext.startActivity(intent);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, pos);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void showPopup(android.view.View v, final int i) {
        PopupMenu popup = new PopupMenu(mContext, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_my_photos, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(
                    android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete_photo:
                        Toast.makeText(mContext, "This feature is not available", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_edit_photo:
                        Toast.makeText(mContext, "This feature is not available", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
                //return false;
            }
        });
    }

    private int getRotation(int orientation) {
        int rotation = 0;
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (orientation!= 0) {
                rotation = 90;
            }
        } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (orientation == 0) {
                rotation = 90;
            }
        }
        return rotation;
    }
}

