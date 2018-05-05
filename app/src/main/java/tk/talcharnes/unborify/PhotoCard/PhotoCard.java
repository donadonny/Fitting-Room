
package tk.talcharnes.unborify.PhotoCard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.mindorks.placeholderview.SwipeDirection;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.mindorks.placeholderview.annotations.swipe.SwipingDirection;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import tk.talcharnes.unborify.Models.ReportModel;
import tk.talcharnes.unborify.PhotoCard.Comments.CommentActivity;
import tk.talcharnes.unborify.Models.PhotoModel;
import tk.talcharnes.unborify.Models.UserModel;
import tk.talcharnes.unborify.Profile.ProfileActivity;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.UserProfile.UserProfileActivity;
import tk.talcharnes.unborify.Utilities.Analytics;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.GlideLoader2;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;
import tk.talcharnes.unborify.Utilities.StorageConstants;

/**
 * Created by Khuram Chaudhry on 08/28/2017.
 * Modified by Tal and Mario.
 * This class displays a photo with the uploader's name and photo rating. This class allows the
 *  user to rate, report, and favorite the image.
 */
@NonReusable
@Layout(R.layout.card_photo_view)
public class PhotoCard {

    private final String TAG = PhotoCard.class.getSimpleName();

    @View(R.id.photoImageView)
    ImageView photoImageView;

    @View(R.id.avatarImage)
    AvatarView avatarView;

    @View(R.id.thumbs_up_fab)
    FloatingActionButton likeButton;

    @View(R.id.thumbs_down_fab)
    FloatingActionButton dislikeButton;

    @View(R.id.occasion_textview)
    TextView occasionTextView;

    @View(R.id.ratingbar)
    SimpleRatingBar ratingBar;

    @View(R.id.uploadederNameTxt)
    TextView usernameTextView;

    @View(R.id.progress_bar)
    ProgressBar progressBar;

    @View(R.id.photo_card_options)
    ImageButton photo_card_options;

    private PhotoModel mPhotoModel;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private String mUserId, mUserName;
    private Boolean isReported = false;
    private IImageLoader imageLoader;
    private String photoName;


    public PhotoCard(Context context, PhotoModel photoModel, SwipePlaceHolderView swipeView,
                     String userId, String userName) {
        mContext = context;
        mPhotoModel = photoModel;
        mSwipeView = swipeView;
        mUserId = userId;
        mUserName = userName;
        photoName = PhotoUtilities.removeWebPFromUrl(mPhotoModel.getUrl());
    }

    /**
     * This method is called at the start of the PhotoCard initialization.
     */
    @Resolve
    public void onResolved() {
        imageLoader = new GlideLoader2(mUserName);

        final String url = mPhotoModel.getUrl();

        if (url != null && !url.isEmpty()) {
            StorageReference storageRef = StorageConstants.getImageRef(url);
            StorageConstants.loadImageUsingGlide(mContext, photoImageView, storageRef, progressBar,
                    mPhotoModel.getOrientation());
        }

        setUploader(mPhotoModel.getUserUid());
        occasionTextView.setText(mPhotoModel.getOccasionSubtitle());
        setRatingBar();
        setListeners();
    }

    /**
     * This method gets and sets the user's name and user's profile photo.
     * @param uid - the id of the user.
     */
    private void setUploader(final String uid) {
        DatabaseContants.getUserRef(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            if (userModel != null && userModel.getName() != null
                                    && usernameTextView != null) {
                                String name = userModel.getName();
                                usernameTextView.setText(name);
                                imageLoader.loadImage(avatarView, uid, name);
                                String photoDesc = "The photo" + mPhotoModel.getOccasionSubtitle() +
                                        " uploaded By " + name;
                                photoImageView.setContentDescription(photoDesc);
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
     * This method sets the photo rating.
     */
    private void setRatingBar() {
        float likes = Float.parseFloat("" + mPhotoModel.getLikes());
        float dislikes = Float.parseFloat("" + mPhotoModel.getDislikes());
        float totalVotes = likes + dislikes;
        if (totalVotes != 0) {
            float rating = (likes / totalVotes) * 100f;
            int index = (int) Math.floor(rating / 20f);
            int[] ratingColors = mContext.getResources().getIntArray(R.array.array_rate_colors);
            int[] ratingShadowColors = mContext.getResources().getIntArray(R.array
                    .array_rate_shadow_colors);

            ratingBar.setFillColor(ratingColors[index]);
            ratingBar.setBorderColor(ratingShadowColors[index]);
            ratingBar.setRating(rating / 20f);

            Log.d(TAG, "setRatingBar() rating for the photo is: " + index);
        }
    }

    /**
     * This method sets the listeners.
     */
    private void setListeners() {
        photo_card_options.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                showPopup(view);
            }
        });

        avatarView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("uid", mPhotoModel.getUserUid());
                mContext.startActivity(intent);
            }
        });

        likeButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Analytics.registerSwipe(mContext, "right");
                mSwipeView.doSwipe(true);
            }
        });

        dislikeButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Analytics.registerSwipe(mContext, "left");
                mSwipeView.doSwipe(false);
            }
        });
    }

    /**
     * This method handles when the Card View is clicked.
     */
    @Click(R.id.photoImageView)
    public void onClick() {
        Intent intent = new Intent(mContext, ZoomPhotoActivity.class);
        intent.putExtra("url", mPhotoModel.getUrl());
        intent.putExtra("rotation", mPhotoModel.getOrientation());
        mContext.startActivity(intent);

    }

    /**
     * This method handles when the Card View is swiped right.
     */
    @SwipeIn
    public void onSwipeIn() {
        //Log.d(LOG_TAG, "onSwipedIn");
        setVote("likes");
        Analytics.registerSwipe(mContext, "right");
    }

    /**
     * This method handles when the Card View is swiped left.
     */
    @SwipeOut
    public void onSwipedOut() {
        //Log.d(LOG_TAG, "onSwipedOut");
        if (isReported != null && isReported) {
            isReported = false;
            setReport();
        } else {
            setVote("dislikes");
            Analytics.registerSwipe(mContext, "left");
        }
    }

    /**
     * This method handles when the Card View is moving right.
     */
    @SwipeInState
    public void onSwipeInState() {
        //Log.d(LOG_TAG, "onSwipeInState");
    }

    /**
     * This method handles when the Card View is moving left.
     */
    @SwipeOutState
    public void onSwipeOutState() {
        //Log.d(LOG_TAG, "onSwipeOutState");
    }

    /**
     * Don't know what this does.
     */
    @SwipeCancelState
    public void onSwipeCancelState() {
        //Log.d(LOG_TAG, "onSwipeCancelState");
    }

    /**
     * This method records the direction of user touches.
     */
    @SwipingDirection
    public void onSwipingDirection(SwipeDirection direction) {
        //Log.d(LOG_TAG, "SwipingDirection " + direction.name());
    }

    /**
     * This method records the user's vote in the database.
     * @param rating - the rating the user has chosen. (like or dislike)
     */
    private void setVote(final String rating) {
        if (mUserId.equals(mPhotoModel.getUserUid()) || mPhotoModel == null) {
            Log.e(TAG, "User trying to vote on own photo");
            return;
        }
        final DatabaseReference voteRef = DatabaseContants.getVotesRef().child(photoName)
                .child(mUserId);
        final DatabaseReference photoRef = DatabaseContants.getPhotoRef().child(photoName);
        voteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String userRating = dataSnapshot.getValue(String.class);
                    if(userRating != null && !userRating.equals(rating)) {
                        if(rating.equals("likes")) {
                            photoRef.child("likes").setValue(mPhotoModel.getLikes() + 1);
                            photoRef.child("dislikes").setValue(mPhotoModel.getDislikes() - 1);
                        } else {
                            photoRef.child("likes").setValue(mPhotoModel.getLikes() - 1);
                            photoRef.child("dislikes").setValue(mPhotoModel.getDislikes() + 1);
                        }
                        voteRef.setValue(rating);
                    }
                } else {
                    long currentRatingValue = (rating.equals("likes")) ? mPhotoModel.getLikes() : 
                            mPhotoModel.getDislikes();
                    photoRef.child(rating).setValue(currentRatingValue + 1);
                    voteRef.setValue(rating);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "cancelled with error - " + databaseError.getMessage());
            }
        });
    }

    /**
     * This method changes the value of isReported.
     */
    private void setReported() {
        isReported = true;
    }

    /**
     * This method displays the popup menu and handles the user interactions on the menu.
     */
    private void showPopup(android.view.View v) {
        PopupMenu popup = new PopupMenu(mContext, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo_card, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(
                    android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_report_photo:
                        setReported();
                        mSwipeView.doSwipe(false);
                        return true;
                    case R.id.action_favorite_photo:
                        addImageToFavorites();
                        return true;
                    case R.id.action_share_photo:
                        shareImage();
                        return true;
                    case R.id.action_comment_photo:
                        openCommentPageForImage();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    /**
     * This method records the user's report in the database.
     */
    private void setReport() {
        final DatabaseReference reportRef = DatabaseContants.getReportRef(photoName);
        reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(ReportModel.REPORTED_BY_KEY).child(mUserId).exists()) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot numReportSnap = dataSnapshot.child(ReportModel.NUM_REPORTS_KEY);
                        Long currentNumReports = numReportSnap.getValue(Long.class);
                        if (currentNumReports != null) {
                            reportRef.child(ReportModel.NUM_REPORTS_KEY)
                                    .setValue(currentNumReports + 1L);
                        }
                    } else {
                        reportRef.child(ReportModel.NUM_REPORTS_KEY)
                                .setValue(1L);
                    }
                    reportRef.child(ReportModel.REPORTED_BY_KEY).child(mUserId)
                            .setValue(System.currentTimeMillis());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    /**
     * This method add the image to a favorite's list of the user to the database.
     */
    private void addImageToFavorites() {
        final DatabaseReference favoriteRef = DatabaseContants.getFavoritesRef(mUserId)
                .child(photoName);
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    favoriteRef.setValue(System.currentTimeMillis());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    /**
     * This method takes the image and converts it to a PNG image and sends that image through a
     *  message intent.
     */
    private void shareImage() {
        Bitmap image = ((BitmapDrawable) photoImageView.getDrawable()).getBitmap();
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

    /**
     * This method opens up the CommentActivity for the image.
     */
    private void openCommentPageForImage() {
        Intent intent = new Intent(mContext, CommentActivity.class);
        intent.putExtra("url", mPhotoModel.getUrl());
        intent.putExtra("photoUserID", mPhotoModel.getUserUid());
        intent.putExtra("currentUser", mUserId);
        intent.putExtra("name", mUserName);
        mContext.startActivity(intent);
    }

}
