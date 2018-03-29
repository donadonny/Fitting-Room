package tk.talcharnes.unborify.OtherFragmentActivities.MyPhotos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.mindorks.placeholderview.InfinitePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import tk.talcharnes.unborify.Models.PhotoModel;
import tk.talcharnes.unborify.PhotoCard.Comments.CommentActivity;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;
import tk.talcharnes.unborify.PhotoCard.ZoomPhotoActivity;
import tk.talcharnes.unborify.Utilities.StorageConstants;

/**
 * Created by Khuram Chaudhry on 9/21/17.
 * This class sets up the information for the user's photo. Also allows user to edit and delete.
 */

@Layout(R.layout.my_photo_card)
class PhotoView {

    private static final String TAG = PhotoView.class.getSimpleName();

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

    @View(R.id.share_image_button)
    private ImageButton shareButton;

    @View(R.id.progress_bar)
    private ProgressBar progressBar;

    private PhotoModel mPhotoModel;
    private Context mContext;
    private String mUserId, mUserName;
    private InfinitePlaceHolderView placeHolderView;

    PhotoView(Context context, PhotoModel photoModel, String userId, String userName,
              InfinitePlaceHolderView mLoadMoreView) {
        mContext = context;
        mPhotoModel = photoModel;
        mUserId = userId;
        mUserName = userName;
        placeHolderView = mLoadMoreView;
    }

    @Resolve
    private void onResolved() {
        occasionTextView.setText(mPhotoModel.getOccasionSubtitle());

        String urlString = mPhotoModel.getUrl();
        if (urlString != null && !urlString.isEmpty()) {
            StorageReference storageRef = StorageConstants.getImageRef(urlString);
            StorageConstants.loadImageUsingGlide(mContext, imageView, storageRef, progressBar,
                    mPhotoModel.getOrientation());
        }

        setRatingBar();

        setListeners();
    }

    /**
     * This method sets the photo rating.
     */
    private void setRatingBar() {
        long likes = mPhotoModel.getLikes();
        long dislikes = mPhotoModel.getDislikes();
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
    }

    /**
     * This method displays the popup menu and handles the user interactions on the menu.
     */
    private void setListeners() {
        commentsButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("url", mPhotoModel.getUrl());
                intent.putExtra("photoUserID", mPhotoModel.getUserUid());
                intent.putExtra("currentUser", mUserId);
                intent.putExtra("name", mUserName);
                mContext.startActivity(intent);
            }
        });

        zoomButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(mContext, ZoomPhotoActivity.class);
                intent.putExtra("url", mPhotoModel.getUrl());
                intent.putExtra("rotation", getRotation(mPhotoModel.getOrientation()));
                mContext.startActivity(intent);
            }
        });

        shareButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Log.d(TAG, "Share button Clicked");
                Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/FittingRoom_Data/", "share_photo.png");

                saveFile(destination, imageInByte);
                Uri uri = Uri.fromFile(destination);

                Intent mmsIntent = new Intent(Intent.ACTION_SEND);
                mmsIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        mContext.getResources().getString(R.string.share_text) +
                                mContext.getResources().getString(R.string.app_name));
                mmsIntent.putExtra(Intent.EXTRA_STREAM, uri);
                mmsIntent.setType("image/*");
                mContext.startActivity(Intent.createChooser(mmsIntent, "Send"));

            }
        });
        menuButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                showPopup(view);
            }
        });
    }

    /**
     * This method returns the degrees to rotate based the orientation.
     * @param orientation - integer of photo's orientation.
     */
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

    /**
     * This method displays the popup menu and handles the user interactions on the menu.
     */
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
                        showEditDialog();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    /**
     * This method deletes the photo, its comments, reports, and its image in the storage.
     */
    private void deletePhoto() {
        String url = PhotoUtilities.removeWebPFromUrl(mPhotoModel.getUrl());

        DatabaseReference photoRef = DatabaseContants.getPhotoRef(url);
        StorageReference storageReference = StorageConstants.getImageRef(mPhotoModel.getUrl());

        DatabaseContants.getReportRef(url).removeValue();
        DatabaseContants.getCommentRef().child(url).removeValue();
        photoRef.removeValue();
        storageReference.delete();
        placeHolderView.removeView(this);
    }

    /**
     * This method brings up a dialog to edit the occasion subtitle and updates the database.
     */
    private void showEditDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);

        final android.view.View dialogView = LayoutInflater.from(mContext).
                inflate(R.layout.dialog_edit_comment, null);

        final EditText editText = dialogView.findViewById(R.id.comment_edit_dialog_box);
        editText.setHint("Edit Occasion");
        editText.setText(mPhotoModel.getOccasionSubtitle());

        dialogBuilder.setTitle("Edit Photo Occasion")
                .setView(dialogView)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newOccasionMessage = editText.getText().toString();
                        if (newOccasionMessage.length() < 1) {
                            editText.setError(mContext.getString(R.string.comment_too_short_error));
                        } else {
                            occasionTextView.setText(newOccasionMessage);
                            DatabaseContants.getPhotoRef(PhotoUtilities
                                    .removeWebPFromUrl(mPhotoModel.getUrl()))
                                    .child(PhotoModel.OCCASION_SUBTITLE_KEY)
                                    .setValue(newOccasionMessage);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * This method saves a file to the device.
     * @param destination - A file object with a path indicating where to save the file.
     * @param fileData - A byte array representing the file to store.
     */
    private void saveFile(File destination, byte[] fileData) {
        FileOutputStream fo;
        try {
            fo = new FileOutputStream(destination);
            fo.write(fileData);
            fo.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}