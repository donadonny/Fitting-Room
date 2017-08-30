
package tk.talcharnes.unborify;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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

    @View(R.id.likesText)
    private TextView likeTextView;

    @View(R.id.realPhotoSwipeCard)
    private CardView realPhotoSwipeCard;

    @View(R.id.nameText)
    private TextView nameTextView;

    @View(R.id.dislikesText)
    private TextView dislikeTextView;

    @View(R.id.zoom_button)
    private ImageButton zoom_button;

    private Photo mPhoto;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private String mUserId;
    private DatabaseReference mPhotoReference, mReportsRef;
    static Boolean isReported = false;
    static boolean isAd = false;
    private int width;
    private int height;

    public PhotoCard(Context context, Photo photo, SwipePlaceHolderView swipeView, String userId,
                     DatabaseReference photoReference, DatabaseReference reportsRef) {
        mContext = context;
        mPhoto = photo;
        mSwipeView = swipeView;
        mUserId = userId;
        mPhotoReference = photoReference;
        mReportsRef = reportsRef;
        isAd = photo.isAd();
        Log.d(LOG_TAG, "isAd = " + isAd);

    }

    /**
     * This function sets up the Card View with an image, name, and the ratings.
     */
    @Resolve
    private void onResolved() {
        final boolean itsAnAd = isAd;
        if (!itsAnAd) {
            final String url = mPhoto.getUrl();
            if (url != null && !url.isEmpty()) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                        .child("images").child(url);
                Glide.with(mContext)
                        .using(new FirebaseImageLoader())
                        .load(storageRef)
                        .into(photoImageView);
                String dislikes = "Dislikes: " + mPhoto.getDislikes();
                dislikeTextView.setText(dislikes);
                nameTextView.setText(mPhoto.getOccasion_subtitle());
                String likes = "Likes: " + mPhoto.getLikes();
                likeTextView.setText(likes);

                ImageButton x = realPhotoSwipeCard.findViewById(R.id.zoom_button);
                x.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View view) {
                        Intent intent = new Intent(mContext, ZoomPhoto.class);
                        intent.putExtra("url", mPhoto.getUrl());
                        mContext.startActivity(intent);
                    }
                });
            }
        } else {
            zoom_button.setVisibility(android.view.View.GONE);

            ViewTreeObserver vto = photoImageView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    photoImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    width = pxToDp(photoImageView.getMeasuredWidth());
                    height = pxToDp(photoImageView.getMeasuredHeight());

                    Log.d(LOG_TAG, "Initial Width = " + width +
                            " ----------------------------------- Initial Height = " + height);

                    width = (width < 80) ? 80 : (width > 1200) ? 1200: (int)(width * .9);
                    height = (height < 80) ? 80 : (height > 1200) ? 1200: (int) (height * .9);

                    Log.d(LOG_TAG, "Final Width = " + width +
                            " ----------------------------------- Final Height = " + height);

                    photoImageView.setVisibility(android.view.View.GONE);
                    CardView.LayoutParams params = new CardView.LayoutParams(
                            CardView.LayoutParams.MATCH_PARENT,
                            CardView.LayoutParams.MATCH_PARENT - 75, Gravity.TOP);

                    NativeExpressAdView mAdView = new NativeExpressAdView(mContext);
                    mAdView.setAdSize(new AdSize(width, height));
                    mAdView.setAdUnitId("ca-app-pub-6667404740993831/9531692095");
                    realPhotoSwipeCard.addView(mAdView, params);

                    AdRequest request = new AdRequest.Builder().build();
                    mAdView.loadAd(request);

                }
            });
        }
    }

    /**
     * This function handles when the Card View is clicked.
     */
    @Click(R.id.photoImageView)
    private void onClick() {
        Log.d("EVENT", "profileImageView click");
        //mSwipeView.addView(this);
    }

    /**
     * This function handles when the Card View is swiped right.
     */
    @SwipeIn
    private void onSwipeIn() {
        //Log.d("EVENT", "onSwipedIn");
        final boolean itsAnAd = isAd;
        if (!itsAnAd) {
            setVote("likes");
        }
    }

    /**
     * This function handles when the Card View is swiped left.
     */
    @SwipeOut
    private void onSwipedOut() {
        //Log.d("EVENT", "onSwipedOut");
        final boolean itsAnAd = isAd;
        if (!itsAnAd) {
            if (isReported != null && isReported) {
                isReported = false;
                setReport();
            } else {
                setVote("dislikes");
            }
        }
    }

    /**
     * This function handles when the Card View is moving right.
     */
    @SwipeInState
    private void onSwipeInState() {
        //Log.d("EVENT", "onSwipeInState");
    }

    /**
     * This function handles when the Card View is moving left.
     */
    @SwipeOutState
    private void onSwipeOutState() {
        //Log.d("EVENT", "onSwipeOutState");
    }

    /**
     * Don't know what this does.
     */
    @SwipeCancelState
    private void onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState");
    }

    /**
     * This function records the direction of user touches.
     */
    @SwipingDirection
    private void onSwipingDirection(SwipeDirection direction) {
        Log.d(LOG_TAG, "SwipingDirection " + direction.name());
    }

    /**
     * This function records the user's vote in the database.
     */
    private void setVote(final String rating) {
        final boolean itsAnAd = isAd;
        if (!itsAnAd) {
            final String userID = mUserId;
            final String name = mPhoto.getUrl().replace(".webp", "");
            final DatabaseReference chosenPhoto = mPhotoReference.child(name);
            if (!mUserId.equals(mPhoto.getUser())) {
                chosenPhoto.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("votes").child(userID).exists()) {
                            String uRating = (dataSnapshot.child("votes").child(userID).getValue() + "");
                            if (uRating.equals(rating)) {
                                Log.d(LOG_TAG, "The already User " + rating + " the photo.");
                            } else {
                                String rating2 = (rating.equals("likes")) ? "dislikes" : "likes";
                                long ratingValue = (long) dataSnapshot.child(rating).getValue();
                                long ratingValue2 = (long) dataSnapshot.child(rating2).getValue();
                                chosenPhoto.child(rating).setValue(ratingValue + 1);
                                chosenPhoto.child(rating2).setValue(ratingValue2 - 1);
                                chosenPhoto.child("votes").child(userID).setValue(rating);
                            }
                        } else {
                            final long ratingValue = (long) dataSnapshot.child(rating).getValue();
                            chosenPhoto.child(rating).setValue(ratingValue + 1);
                            chosenPhoto.child("votes").child(userID).setValue(rating);
                            Log.d(LOG_TAG, "The User " + rating + " the photo.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOG_TAG, "cancelled with error - " + databaseError);
                    }
                });
            } else {
                Log.d(LOG_TAG, "User trying to vote on own photo");
            }
        }
    }

    /**
     * This function changes the value of isReported.
     */
    public static void setReported() {
        final boolean itsAnAd = isAd;
        if (!itsAnAd) {
            isReported = true;
        }
    }

    /**
     * This function records the user's report in the database.
     */
    private void setReport() {
        final boolean itsAnAd = isAd;
        if (!itsAnAd) {
            final String userID = mUserId;
            final String name = mPhoto.getUrl().replace(".webp", "");
            System.out.println("------------------------------" + userID + "----------------------------------");
            final Query query = mReportsRef.child(name);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("reported_by").child(userID).exists()) {
                            Log.d(LOG_TAG, "User already reported photo.");
                        } else {
                            long numReports = (long) snapshot.child("numReports").getValue();
                            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
                            mPhotoReference.child(name).child("reports").setValue(numReports + 1);
                            mReportsRef.child(name).child("numReports").setValue(numReports + 1);
                            mReportsRef.child(name).child("reported_by").child(userID).setValue(timeStamp);
                            Log.d(LOG_TAG, "Another report add.");
                        }
                    } else {
                        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
                        HashMap<String, String> reports = new HashMap<String, String>();
                        reports.put(userID, timeStamp);
                        Report report = new Report(1, reports);
                        mReportsRef.child(name).setValue(report);
                        mPhotoReference.child(name).child("reports").setValue(1);
                        Log.d(LOG_TAG, "A new report.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(LOG_TAG, "cancelled with error - " + databaseError);
                }
            });
        }
    }

    private int pxToDp(int px) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
