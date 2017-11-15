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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.mindorks.placeholderview.InfinitePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import tk.talcharnes.unborify.Models.CommentModel;
import tk.talcharnes.unborify.PhotoCard.Comments.CommentActivity;
import tk.talcharnes.unborify.Models.Photo;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;
import tk.talcharnes.unborify.PhotoCard.ZoomPhotoActivity;

/**
 * Created by khuramchaudhry on 9/21/17.
 */

@Layout(R.layout.my_photo_card)
public class PhotoView {

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

    private Photo mPhoto;
    private Context mContext;
    private String mUserId, mUserName;
    private InfinitePlaceHolderView placeHolderView;

    public PhotoView(Context context, Photo photo, String userId, String userName,
                     InfinitePlaceHolderView mLoadMoreView) {
        mContext = context;
        mPhoto = photo;
        mUserId = userId;
        mUserName = userName;
        placeHolderView = mLoadMoreView;
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
            FirebaseConstants.loadImageUsingGlide(mContext, imageView, storageRef, progressBar,
                    mPhoto.getOrientation());
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
                mContext.startActivity(Intent.createChooser(mmsIntent,"Send"));

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

        Log.d(TAG, "Deleting: " + mPhoto.getUrl());

        DatabaseReference photoDBReference = FirebaseConstants.getRef()
                .child(FirebaseConstants.PHOTOS)
                .child(PhotoUtilities.removeWebPFromUrl(mPhoto.getUrl()));

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().
                child("images").child(mPhoto.getUrl());

        deleteReport(PhotoUtilities.removeWebPFromUrl(mPhoto.getUrl()));

        final DatabaseReference commentsRef = photoDBReference.child(FirebaseConstants.COMMENTS);
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot child : dataSnapshot.getChildren()) {
                        CommentModel commentModel = child.getValue(CommentModel.class);
                        if(commentModel != null) {
                            deleteReport(commentModel.getCommentKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        photoDBReference.removeValue();
        storageReference.delete();
        placeHolderView.removeView(this);
    }

    private void deleteReport(String key) {
        final DatabaseReference reportRef = FirebaseConstants.getRef()
                .child(FirebaseConstants.REPORTS).child(key);
        reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    reportRef.removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showEditStringDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final android.view.View dialogView = inflater.inflate(R.layout.dialog_edit_comment, null);
        final EditText edt = dialogView.findViewById(R.id.comment_edit_dialog_box);
        edt.setHint("Edit Occasion");
        dialogBuilder.setView(dialogView);

        String occasionString = mPhoto.getOccasion_subtitle();
        if (occasionString != null && !occasionString.isEmpty()) {
            edt.setText(occasionString);
            Log.d(TAG, "Current Occasion String: " + occasionString);
        }
        dialogBuilder.setTitle("Edit Photo Occasion");

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newOccasion = edt.getText().toString();
                if (newOccasion.isEmpty() ||
                        newOccasion.equals("")
                        || newOccasion == null) {

                    edt.setError(mContext.getString(R.string.occasion_cannot_be_empty_string));
                    Toast.makeText(mContext, mContext.getString(R.string.occasion_cannot_be_empty_string), Toast.LENGTH_SHORT).show();
                } else if (newOccasion.length() < 5) {
                    edt.setError(mContext.getString(R.string.comment_too_short_error));
                    Toast.makeText(mContext, mContext.getString(R.string.comment_too_short_error), Toast.LENGTH_SHORT).show();

                } else {
                    Log.d(TAG, "New Occasion String: " + newOccasion);
                    DatabaseReference photoDBReference = FirebaseConstants.getRef()
                            .child(FirebaseConstants.PHOTOS)
                            .child(PhotoUtilities.removeWebPFromUrl(mPhoto.getUrl()))
                            .child(FirebaseConstants.OCCASION_SUBTITLE);

                    occasionTextView.setText(newOccasion);
                    photoDBReference.setValue(newOccasion);

                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void saveFile(File destination, byte[] fileSize) {
        FileOutputStream fo;
        try {
            fo = new FileOutputStream(destination);
            fo.write(fileSize);
            fo.close();
            //Toast.makeText(mContext, "File Successfully Saved!!", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            //Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}