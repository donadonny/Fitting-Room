package tk.talcharnes.unborify.NavigationFragments.MyPhotos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import tk.talcharnes.unborify.CommentActivity;
import tk.talcharnes.unborify.Photo;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;
import tk.talcharnes.unborify.ZoomPhotoActivity;

/**
 * Created by khuramchaudhry on 9/21/17.
 */

@Layout(R.layout.my_photo_card)
public class PhotoView {

    @View(R.id.card_image_view)
    private ImageView imageView;

    @View(R.id.occasion_cardview_textview)
    private TextView occasionTextView;

    @View(R.id.comments_button)
    private ImageButton commentsButton;

    @View(R.id.zoom_button)
    private ImageButton zoomButton;

    @View(R.id.menu_button)
    private ImageButton menuButton;

    @View(R.id.ratingbar)
    private SimpleRatingBar ratingBar;

    private Photo mPhoto;
    private Context mContext;
    private String mUserId, mUserName;

    public PhotoView(Context context, Photo photo, String userId, String userName) {
        mContext = context;
        mPhoto = photo;
        mUserId = userId;
        mUserName = userName;
    }

    @Resolve
    private void onResolved() {
        long likes = mPhoto.getLikes();
        long dislikes = mPhoto.getDislikes();
        dislikes = (dislikes < 1) ? 1 : dislikes;
        likes = (likes < 1) ? 1 : likes;
        float totalVotes = likes + dislikes;
        float rating = (likes / totalVotes) * 100f;
        int index = (int) Math.floor(rating / 20f);
        int[] fillColor = mContext.getResources().getIntArray(R.array.array_rate_colors);
        int[] shadowColor = mContext.getResources().getIntArray(R.array.array_rate_shadow_colors);

        ratingBar.setFillColor(fillColor[index]);
        ratingBar.setBorderColor(shadowColor[index]);

        ratingBar.setRating(rating / 20f);

        occasionTextView.setText(mPhoto.getOccasion_subtitle());

        String urlString = mPhoto.getUrl();
        if (urlString != null && !urlString.isEmpty()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("images").child(urlString);
            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(storageRef)
                    .into(imageView);
        }

        commentsButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("url", mPhoto.getUrl());
                intent.putExtra("photoUserID", mPhoto.getUser());
                intent.putExtra("currentUser", mUserId);
                intent.putExtra("name", mUserName);
                mContext.startActivity(intent);
            }
        });

        zoomButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(mContext, ZoomPhotoActivity.class);
                intent.putExtra("url", mPhoto.getUrl());
                intent.putExtra("rotation", getRotation(mPhoto.getOrientation()));
                mContext.startActivity(intent);
            }
        });

        menuButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                showPopup(view);
            }
        });
    }

    private int getRotation(int orientation) {
        int rotation = 0;
        if (mContext.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            if (orientation != 0) {
                rotation = 90;
            }
        } else if (mContext.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            if (orientation == 0) {
                rotation = 90;
            }
        }
        return rotation;
    }

    private void showPopup(android.view.View v) {
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
                        deletePhoto();
                        return true;
                    case R.id.action_edit_photo:
                        showEditStringDialog();
                        return true;
                    default:
                        return false;
                }
                //return false;
            }
        });
    }

    private void deletePhoto() {

        DatabaseReference photoDBReference = FirebaseConstants.getRef()
                .child(FirebaseConstants.PHOTOS)
                .child(PhotoUtilities.removeWebPFromUrl(mPhoto.getUrl()));

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().
                child("images").child(mPhoto.getUrl());

        photoDBReference.removeValue();
        storageReference.delete();

        //// TODO: 9/10/2017 see if photo exists in reports and if so delete report
    }

    private void showEditStringDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ;
        final android.view.View dialogView = inflater.inflate(R.layout.dialog_edit_comment, null);
        final EditText edt = dialogView.findViewById(R.id.comment_edit_dialog_box);
        edt.setHint("Edit Occasion");
        dialogBuilder.setView(dialogView);

        String occasionString = mPhoto.getOccasion_subtitle();
        if (occasionString != null && !occasionString.isEmpty()) {
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
                            .child(PhotoUtilities.removeWebPFromUrl(mPhoto.getUrl()))
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