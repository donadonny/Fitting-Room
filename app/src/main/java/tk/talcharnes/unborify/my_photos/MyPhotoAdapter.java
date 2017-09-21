package tk.talcharnes.unborify.my_photos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.List;

import tk.talcharnes.unborify.CommentActivity;
import tk.talcharnes.unborify.Photo;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;
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
        TextView occastionTextView;
        SimpleRatingBar ratingBar;
        ImageButton commentsButton, zoomButton, menuButton;

        public ViewHolder(View v) {
            super(v);
            mImageView = v.findViewById(R.id.card_image_view);
            occastionTextView = v.findViewById(R.id.occasion_cardview_textview);
            commentsButton = v.findViewById(R.id.comments_button);
            zoomButton = v.findViewById(R.id.zoom_button);
            menuButton = v.findViewById(R.id.menu_button);
            ratingBar = v.findViewById(R.id.ratingbar);

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
        final int pos = position;
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Photo photo = mDataset.get(pos);
        long likes = photo.getLikes();
        long dislikes = photo.getDislikes();
        dislikes = (dislikes < 1) ? 1 : dislikes;
        likes = (likes < 1) ? 1 : likes;
        float totalVotes = likes + dislikes;
        float rating = (likes / totalVotes) * 100f;
        int index = (int) Math.floor(rating/20f);
        int[] fillColor = mContext.getResources().getIntArray(R.array.array_rate_colors);
        int[] shadowColor = mContext.getResources().getIntArray(R.array.array_rate_shadow_colors);

        holder.ratingBar.setFillColor(fillColor[index]);
        holder.ratingBar.setBorderColor(shadowColor[index]);

        holder.ratingBar.setRating(rating/20f);

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
                mContext.startActivity(intent);
            }
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
        final Photo photo = mDataset.get(i);
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
                        deletePhoto(photo);
                        return true;
                    case R.id.action_edit_photo:
                        showEditStringDialog(photo);
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
            if (orientation != 0) {
                rotation = 90;
            }
        } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (orientation == 0) {
                rotation = 90;
            }
        }
        return rotation;
    }

    private void deletePhoto(Photo photo) {

        DatabaseReference photoDBReference = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.PHOTOS)
                .child(PhotoUtilities.removeWebPFromUrl(photo.getUrl()));

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images")
                .child(photo.getUrl());

        photoDBReference.removeValue();
        storageReference.delete();

        //// TODO: 9/10/2017 see if photo exists in reports and if so delete report
    }


    private void showEditStringDialog(final Photo photo) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ;
        final View dialogView = inflater.inflate(R.layout.dialog_edit_comment, null);
        final EditText edt = dialogView.findViewById(R.id.comment_edit_dialog_box);
        edt.setHint("Edit Occasion");
        dialogBuilder.setView(dialogView);

        String occasionString = photo.getOccasion_subtitle();
        if(occasionString != null && !occasionString.isEmpty()) {
            edt.setText(occasionString);
        }
        dialogBuilder.setTitle("Edit Photo Occasion");

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newOccasion = edt.getText().toString();
                if (newOccasion.isEmpty() ||
                        newOccasion.equals("")
                        || newOccasion == null) {

                    edt.setError("Occasion can not be blank");
                } else if (newOccasion.length() < 5) {
                    edt.setError("Occasion must be longer than 5 characters");
                } else {
                    DatabaseReference photoDBReference = FirebaseConstants.getRef()
                            .child(FirebaseConstants.PHOTOS)
                            .child(PhotoUtilities.removeWebPFromUrl(photo.getUrl()))
                            .child(FirebaseConstants.OCCASION_SUBTITLE);

                    photoDBReference.setValue(newOccasion);

                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}

