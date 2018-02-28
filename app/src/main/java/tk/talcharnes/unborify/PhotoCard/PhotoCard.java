
package tk.talcharnes.unborify.PhotoCard;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.GlideLoader2;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;

/**
 * Created by janisharali on 19/08/16.
 * Modified by Khuram Chaudhry on 08/28/2017.
 */
@NonReusable
@Layout(R.layout.photo_card_view)
public class PhotoCard {

    private final String LOG_TAG = PhotoCard.class.getSimpleName();

    @View(R.id.photoImageView)
    private ImageView photoImageView;

    @View(R.id.avatarImage)
    private AvatarView avatarView;

    @View(R.id.realPhotoSwipeCard)
    private CardView realPhotoSwipeCard;

    @View(R.id.thumbs_up_fab)
    private FloatingActionButton likeButton;

    @View(R.id.thumbs_down_fab)
    private FloatingActionButton dislikeButton;

    @View(R.id.nameText)
    private TextView nameTextView;

    @View(R.id.ratingbar)
    private SimpleRatingBar ratingBar;

    @View(R.id.uploadederNameTxt)
    private TextView usernameTextView;

    @View(R.id.zoom_button)
    private ImageButton zoom_button;

    @View(R.id.comment_button)
    private ImageButton comment_button;

    @View(R.id.share_button)
    private ImageButton share_button;

    @View(R.id.progress_bar)
    private ProgressBar progressBar;

    @View(R.id.photo_card_options)
    private ImageButton photo_card_options;

    private PhotoModel mPhotoModel;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private String mUserId, mUserName;
    private DatabaseReference mPhotoReference, mReportsRef;
    private Boolean isReported = false;
    private int width, height;
    private boolean mVisible = true;
    private IImageLoader imageLoader;
    private StorageReference storageRef;


    public PhotoCard(Context context, PhotoModel photoModel, SwipePlaceHolderView swipeView, String userId,
                     String userName, DatabaseReference photoReference,
                     DatabaseReference reportsRef) {
        mContext = context;
        mPhotoModel = photoModel;
        mSwipeView = swipeView;
        mUserId = userId;
        mUserName = userName;
        mPhotoReference = photoReference;
        mReportsRef = reportsRef;
    }

    /**
     * @param uid
     * @TODO
     */
    private void setUploader(final String uid) {
        FirebaseConstants.getRef().child(FirebaseConstants.USERS).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            if (userModel != null && usernameTextView != null && userModel.getName() != null) {
                                usernameTextView.setText(userModel.getName());
                                imageLoader.loadImage(avatarView, uid, userModel.getName());
                                photoImageView.setContentDescription("Uploaded By "+ userModel.getName()
                                        +" "+" photo name "+ mPhotoModel.getOccasionSubtitle()+" ");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * This function sets up the Card View with an image, name, and the ratings.
     *
     * @TODO this function seems to do a lot of things can we break it up some?
     */
    @Resolve
    private void onResolved() {
        Log.d(LOG_TAG, "mUserId: " + mUserId);
        Log.d(LOG_TAG, "mUserName: " + mUserName);
        final String url = mPhotoModel.getUrl();
        Log.d(LOG_TAG, "url: " + url);
        imageLoader = new GlideLoader2();
        if (url != null && !url.isEmpty()) {
            final int rotation = getRotation();
            storageRef = FirebaseStorage.getInstance().getReference()
                    .child(FirebaseConstants.IMAGES).child(url);

            FirebaseConstants.loadImageUsingGlide(mContext, photoImageView, storageRef,
                    progressBar, mPhotoModel.getOrientation());

            String occasionTitle = mPhotoModel.getOccasionSubtitle();
            nameTextView.setText(occasionTitle);
            Log.d(LOG_TAG, "\toccasion subtitle: " + occasionTitle);
            Log.d(LOG_TAG, "\tcategory: " + mPhotoModel.getCategory());
            float likes = Float.parseFloat("" + mPhotoModel.getLikes());
            Log.d(LOG_TAG, "\tlikes: " + likes);
            float dislikes = Float.parseFloat("" + mPhotoModel.getDislikes());
            Log.d(LOG_TAG, "\tdislikes: " + dislikes);
//            dislikes = (dislikes < 1) ? 1 : dislikes;
//            likes = (likes < 1) ? 1 : likes;
            float totalVotes = likes + dislikes;
            Log.d(LOG_TAG, "\ttotalVotes: " + totalVotes);
            if (totalVotes != 0) {
                float rating = (likes / totalVotes) * 100f;
                Log.d(LOG_TAG, "\trating: " + rating);

                int index = (int) Math.floor(rating / 20f);
                int[] ratingColors = mContext.getResources().getIntArray(R.array
                        .array_rate_colors);
                int[] ratingShadowColors = mContext.getResources().getIntArray(R.array
                        .array_rate_shadow_colors);

                ratingBar.setFillColor(ratingColors[index]);
                ratingBar.setBorderColor(ratingShadowColors[index]);
                ratingBar.setRating(rating / 20f);

            }
            zoom_button.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    Intent intent = new Intent(mContext, ZoomPhotoActivity.class);
                    intent.putExtra("url", mPhotoModel.getUrl());
                    intent.putExtra("rotation", rotation);
                    mContext.startActivity(intent);
                }
            });

            comment_button.setOnClickListener(new android.view.View.OnClickListener() {
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

            share_button.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    Log.d(LOG_TAG, "Share button Clicked");
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
            });

            photo_card_options.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    showPopup(view, 0);
                }
            });

            setUploader(mPhotoModel.getUserUid());

            avatarView.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    Intent intent = new Intent(mContext,
                            (FirebaseConstants.getUser().getUid().equals(mPhotoModel.getUserUid())) ?
                                    ProfileActivity.class : UserProfileActivity.class);
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
    }

    /**
     * This function handles when the Card View is clicked.
     */
    @Click(R.id.photoImageView)
    private void onClick() {
        //Log.d("EVENT", "profileImageView click");
        //mSwipeView.addView(this);
        /*photoImageView.setContentDescription("Uploaded By "+ mUploaderName
                +" "+" photo name "+ mPhotoModel.getOccasionSubtitle()+" ");*/
        togglePhotoAddOns();

    }

    /**
     * This function handles when the Card View is swiped right.
     */
    @SwipeIn
    private void onSwipeIn() {
        //Log.d(LOG_TAG, "onSwipedIn");
        setVote("likes");
        Analytics.registerSwipe(mContext, "right");
    }

    /**
     * This function handles when the Card View is swiped left.
     */
    @SwipeOut
    private void onSwipedOut() {
        //Log.d(LOG_TAG, "onSwipedOut");
        if (isReported != null && isReported) {
            isReported = false;
            setReport(PhotoUtilities.removeWebPFromUrl(mPhotoModel.getUrl()));
        } else {
            setVote("dislikes");
            Analytics.registerSwipe(mContext, "left");
        }
    }

    /**
     * This function handles when the Card View is moving right.
     */
    @SwipeInState
    private void onSwipeInState() {
        //Log.d(LOG_TAG, "onSwipeInState");
    }

    /**
     * This function handles when the Card View is moving left.
     */
    @SwipeOutState
    private void onSwipeOutState() {
        //Log.d(LOG_TAG, "onSwipeOutState");
    }

    /**
     * Don't know what this does.
     */
    @SwipeCancelState
    private void onSwipeCancelState() {
        //Log.d(LOG_TAG, "onSwipeCancelState");
    }

    /**
     * This function records the direction of user touches.
     */
    @SwipingDirection
    private void onSwipingDirection(SwipeDirection direction) {
        //Log.d(LOG_TAG, "SwipingDirection " + direction.name());
    }

    /**
     * This function records the user's vote in the database.
     */
    private void setVote(final String rating) {
        final String userID = mUserId;
        final String name = PhotoUtilities.removeWebPFromUrl(mPhotoModel.getUrl());
        final DatabaseReference chosenPhoto = mPhotoReference.child(name);
        if (!mUserId.equals(mPhotoModel.getUserUid())) {
            chosenPhoto.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(FirebaseConstants.VOTES).child(userID).exists()) {
                        String uRating = (dataSnapshot.child(FirebaseConstants.VOTES)
                                .child(userID).getValue() + "");
                        if (uRating.equals(rating)) {
                            Log.d(LOG_TAG, "The UserModel already " + rating + " the photo.");
                        } else {
                            String rating2 = (rating.equals("likes")) ? "dislikes" : "likes";
                            long ratingValue = (long) dataSnapshot.child(rating).getValue();
                            long ratingValue2 = (long) dataSnapshot.child(rating2).getValue();
                            chosenPhoto.child(rating).setValue(ratingValue + 1);
                            chosenPhoto.child(rating2).setValue(ratingValue2 - 1);
                            chosenPhoto.child(FirebaseConstants.VOTES).child(userID)
                                    .setValue(rating);
                        }
                    } else {
                        final long ratingValue = (long) dataSnapshot.child(rating).getValue();
                        chosenPhoto.child(rating).setValue(ratingValue + 1);
                        chosenPhoto.child(FirebaseConstants.VOTES).child(userID).setValue(rating);
                        Log.d(LOG_TAG, "The UserModel " + rating + " the photo.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(LOG_TAG, "cancelled with error - " + databaseError);
                }
            });
        } else {
            Log.d(LOG_TAG, "UserModel trying to vote on own photo");
        }
    }

    /**
     * This function changes the value of isReported.
     */
    public void setReported() {
        isReported = true;
    }

    /**
     * This function records the user's report in the database.
     */
    private void setReport(final String name) {
        final Query query = mReportsRef.child(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child(FirebaseConstants.REPORTED_BY).child(mUserId).exists()) {
                        Log.d(LOG_TAG, "UserModel already reported photo.");
                    } else {
                        long numReports = (long) snapshot.child(FirebaseConstants.NUM_REPORTS)
                                .getValue();
                        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US)
                                .format(new Date());
                        mPhotoReference.child(name).child(FirebaseConstants.PHOTO_REPORTS)
                                .setValue(numReports + 1);
                        mReportsRef.child(name).child(FirebaseConstants.NUM_REPORTS)
                                .setValue(numReports + 1);
                        mReportsRef.child(name).child(FirebaseConstants.REPORTED_BY)
                                .child(mUserId).setValue(timeStamp);
                        Log.d(LOG_TAG, "Another report add.");
                    }
                } else {
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US)
                            .format(new Date());
                    HashMap<String, String> reports = new HashMap<String, String>();
                    reports.put(mUserId, timeStamp);
                    ReportModel reportModel = new ReportModel(1, reports);
                    mReportsRef.child(name).setValue(reportModel);
                    mPhotoReference.child(name).child(FirebaseConstants.PHOTO_REPORTS).setValue(1);
                    Log.d(LOG_TAG, "A new reportModel.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOG_TAG, "cancelled with error - " + databaseError);
            }
        });
    }

    /**
     * @param px
     * @return
     * @// TODO: 10/25/2017
     */
    private int pxToDp(int px) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    /**
     * @return
     * @// TODO: 10/25/2017
     */
    private int getRotation() {
        int rotation = 0;
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mPhotoModel.getOrientation() != 0) {
                rotation = 90;
            }
        } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (mPhotoModel.getOrientation() == 0) {
                rotation = 90;
            }
        }
        return rotation;
    }

    /**
     * @// TODO: 10/25/2017
     */
    private void togglePhotoAddOns() {
        if (mVisible) {
            zoom_button.setVisibility(android.view.View.GONE);
            comment_button.setVisibility(android.view.View.GONE);
            photo_card_options.setVisibility(android.view.View.GONE);
            share_button.setVisibility(android.view.View.GONE);
            mVisible = false;
        } else {
            zoom_button.setVisibility(android.view.View.VISIBLE);
            comment_button.setVisibility(android.view.View.VISIBLE);
            photo_card_options.setVisibility(android.view.View.VISIBLE);
            share_button.setVisibility(android.view.View.VISIBLE);

            mVisible = true;
        }
    }

    /**
     * @param v
     * @param i
     * @// TODO: 10/25/2017
     */
    private void showPopup(android.view.View v, final int i) {
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
                        addToFavorites();
                        return true;
                    default:
                        return false;
                }
                //return false;
            }
        });
    }

    public void addToFavorites() {
        final DatabaseReference ref = FirebaseConstants.getRef().child(FirebaseConstants.USERS)
                .child(mUserId).child(FirebaseConstants.USER_FAVORITES)
                .child(PhotoUtilities.removeWebPFromUrl(mPhotoModel.getUrl()));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ref.setValue(FirebaseConstants.FAVORITE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
