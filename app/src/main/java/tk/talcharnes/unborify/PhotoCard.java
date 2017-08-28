package tk.talcharnes.unborify;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Tal on 8/28/2017.
 */

@NonReusable
@Layout(R.layout.swipe_layout)
public class PhotoCard {

    private Photo mPhoto;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private String photoName;
    final private String dislikeStringKey = "dislike";
    final private String likeStringKey = "like";
    private DatabaseReference mPhotoReference;
    private DatabaseReference mUserReference;
    private FirebaseDatabase mFirebaseDatabase;
    private final String LOG_TAG = PhotoCard.class.getSimpleName();
    private String mUserId;
    private Boolean isReported = false;


    public PhotoCard(Context context, Photo photo, SwipePlaceHolderView swipeView, FirebaseDatabase firebaseDatabase,
                     DatabaseReference photoReference, DatabaseReference userReference, String userId) {
        mContext = context;
        mPhoto = photo;
        mSwipeView = swipeView;
        mFirebaseDatabase = firebaseDatabase;
        mPhotoReference = photoReference;
        mUserReference = userReference;
        mUserId = userId;

    }

    @Resolve
    private void onResolved() {
        long amount_likes = mPhoto.getLikes();
        long amount_dislikes = mPhoto.getDislikes();
        String occastion_subtitle_string = mPhoto.getOccasion_subtitle();
        String urlString = mPhoto.getUrl();
        photoName = mPhoto.getUrl().replace(".webp", "");
        ImageView imageView = (ImageView) mSwipeView.findViewById(R.id.userFashionStylePhoto);

        if (urlString != null && !urlString.isEmpty()) {

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(urlString);

            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(storageRef).transform(new MyTransformation(mContext, getRotation()))
                    .into(imageView);
        }

        TextView occastion_subtitle = (TextView) mSwipeView.findViewById(R.id.occasion_subtitle);
        occastion_subtitle.setText(occastion_subtitle_string);
        occastion_subtitle.setTextColor(mContext.getResources().getColor(R.color.colorAccent));


    }


    @SwipeOut
    private void onSwipedOut() {
        Log.d("EVENT", "onSwipedOut");
            if (isReported) {
            final DatabaseReference reportsRef = mFirebaseDatabase.getReference().child("Reports");
            final Query query = reportsRef.child(photoName);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (!snapshot.child("reported_by").child(mUserId).exists()) {
                            Log.d(LOG_TAG, "User already reported photo.");
                        } else {
                            long numReports = (long) snapshot.child("numReports").getValue();
                            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
                            mPhotoReference.child(photoName).child("reports").setValue(numReports + 1);
                            reportsRef.child(photoName).child("numReports").setValue(numReports + 1);
                            reportsRef.child(photoName).child("reported_by").child(mUserId).setValue(timeStamp);
                            Log.d(LOG_TAG, "Another report add.");
                        }
                    } else {
                        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
                        HashMap<String, String> reports = new HashMap<String, String>();
                        reports.put(mUserId, timeStamp);
                        Report report = new Report(1, reports);
                        reportsRef.child(photoName).setValue(report);
                        mPhotoReference.child(photoName).child("reports").setValue(1);
                        Log.d(LOG_TAG, "A new report.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(LOG_TAG, "cancelled with error - " + databaseError);
                }
            });
        } else if (!mUserId.equals(mPhoto.getUser())) {
            Query query = mPhotoReference.child(photoName).child("Votes").child(mUserId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.getValue().toString().equals(likeStringKey)) {
                            mPhoto.setLikes(mPhoto.getLikes() - 1);
                            mPhoto.setDislikes(mPhoto.getDislikes() + 1);
                            mPhotoReference.child(photoName).setValue(mPhoto);
                            mUserReference.child(mPhoto.getUser()).child(photoName).setValue(mPhoto);
                            mPhotoReference.child(photoName).child("Votes").child(mUserId).setValue(dislikeStringKey);

                            Log.d(LOG_TAG, "snapshot value is like");
                        } else {
                            Log.d(LOG_TAG, "snapshot value is already dislike");
                        }

                    } else {
                        mPhoto.setDislikes(mPhoto.getDislikes() + 1);
                        mPhotoReference.child(photoName).setValue(mPhoto);
                        mUserReference.child(mPhoto.getUser()).child(photoName).setValue(mPhoto);
                        mPhotoReference.child(photoName).child("Votes").child(mUserId).setValue(dislikeStringKey);
                        Log.d(LOG_TAG, "snapshot value does not exist");
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
        Log.d(LOG_TAG, "Left card Exit");

    }

    @SwipeCancelState
    private void onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn() {
        Log.d("EVENT", "onSwipedIn");
        if (!mUserId.equals(mPhoto.getUser())) {
            mPhotoReference.child(photoName).child("Votes").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.getValue().toString().equals(likeStringKey)) {
                            Log.d(LOG_TAG, "snapshot value is already like");
                        } else {
                            mPhoto.setLikes(mPhoto.getLikes() + 1);
                            mPhoto.setDislikes(mPhoto.getDislikes() - 1);
                            mPhotoReference.child(photoName).setValue(mPhoto);
                            mPhotoReference.child(photoName).child("Votes").child(mUserId).setValue(likeStringKey);

                            Log.d(LOG_TAG, "snapshot value is dislike");
                        }

                    } else {
                        mPhoto.setLikes(mPhoto.getLikes() + 1);
                        mPhotoReference.child(photoName).setValue(mPhoto);
                        mPhotoReference.child(photoName).child("Votes").child(mUserId).setValue(likeStringKey);
                        Log.d(LOG_TAG, "snapshot value does not exist");
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

    @SwipeInState
    private void onSwipeInState() {
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState() {
        Log.d("EVENT", "onSwipeOutState");
    }


    private int getRotation() {
        int rotation = 0;
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mPhoto.getOrientation() != 0) {
                rotation = 90;
//                imageView.setRotation(90);
            }
        } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (mPhoto.getOrientation() == 0) {
//                imageView.setRotation(90);
                rotation = 90;
            }
        }
        return rotation;
    }
}
