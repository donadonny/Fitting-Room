package tk.talcharnes.unborify.Utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import tk.talcharnes.unborify.MyTransformation;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Report;

/**
 * Created by Tal on 9/4/2017.
 */

public class FirebaseConstants {
    public final static String COMMENTS = "Comments";
    public final static String PHOTOS = "Photos";
    public final static String USERS = "Users";
    public final static String INSTANCEID = "instanceId";
    public final static String USERNAME = "name";
    public final static String USER_CONNECTIONS = "user_connections";
    public final static String USER_FAVORITES= "user_favorites";
    public final static String FAVORITE = "Favorite";
    public final static String URI = "uri";
    public final static String IMAGES = "images";
    public final static String PROFILE_IMAGE = "profileImages";
    public final static String REPORTS = "Reports";
    public final static String NOTIFICATION = "Notifications";
    public final static String VOTES = "votes";
    public final static String PHOTO_REPORTS = "reports";
    public final static String PHOTO_LIST = "photo_list";
    public final static String REPORTED_BY = "reported_by";
    public final static String NUM_REPORTS = "numReports";
    public final static String DATE_JOINED = "date_joined";
    public final static String COMMENT_KEY = "comment_key";
    public final static String COMMENT_STRING = "commentString";
    public final static String OCCASION_SUBTITLE = "occasion_subtitle";

    //    Strings for contact us section
    public final static String CONTACT_US = "Contact_us";
    public final static String CONTACT_TYPE_TIP = "Tip";
    public final static String CONTACT_TYPE = "contact_type";
    public final static String CONTACT_TYPE_BUG = "Bug";
    public final static String CONTACT_TYPE_OTHER = "Other";
    public final static String CONTACT_US_MESSAGE = "message";
    public final static String EMAIL = "email";

    public final static String URL = "url";

    //    Categories of photos
    public final static String CATEGORY_FASHION = "Fashion";

    public static void setToken(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase.getInstance().getReference().child(USERS).
                    child(user.getUid()).child(INSTANCEID).setValue(token);
        }
    }

    public static DatabaseReference getRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static StorageReference getStorRef() {
        return FirebaseStorage.getInstance().getReference();
    }

    public static FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getUserName() {
        return (getUser() != null) ? getUser().getDisplayName() : "Bobby Bob";
    }

    public static void setReport(final String TAG, final Context context, final String reportID,
                                 final String userID) {
        final DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference()
                .child(REPORTS).child(reportID);
        reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(FirebaseConstants.REPORTED_BY).child(userID).exists()) {
                        Log.d(TAG, "Already reported by user: " + userID);
                        Toast.makeText(context, "You have reported this already.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        long numReports = (long) dataSnapshot.child(NUM_REPORTS).getValue();
                        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US)
                                .format(new Date());
                        reportRef.child(NUM_REPORTS).setValue(numReports + 1);
                        reportRef.child(REPORTED_BY).child(userID).setValue(timeStamp);
                        Log.d(TAG, "User made a report. Another report was added.");
                        Toast.makeText(context, "A report was made.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US)
                            .format(new Date());
                    HashMap<String, String> reports = new HashMap<String, String>();
                    reports.put(userID, timeStamp);
                    Report report = new Report(1, reports);
                    reportRef.setValue(report);
                    Log.d(TAG, "User made a report. A new report was made.");
                    Toast.makeText(context, "A report was made.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "cancelled with error: " + databaseError);
                Toast.makeText(context, "Failed to a report.", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public static void loadImageUsingGlide(Context context, ImageView imageView,
                                    StorageReference storageReference,
                                    final ProgressBar progressBar) {
        GlideApp.with(context)
                .load(storageReference)
                .transform(new MyTransformation(context, 0))
                .placeholder(R.mipmap.ic_launcher)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model, Target<Drawable> target,
                                                boolean isFirstResource) {
                        if(progressBar != null) {
                            progressBar.setVisibility(android.view.View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        if(progressBar != null) {
                            progressBar.setVisibility(android.view.View.GONE);
                        }
                        return false;
                    }

                })
                .into(imageView);
    }

    public static void loadImageUsingGlide(Context context, ImageView imageView,
                                           StorageReference storageReference,
                                           final ProgressBar progressBar, int rotation) {
        if (context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            if (rotation != 0) {
                rotation = 0;
            }
        } else if (context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            if (rotation != 0) {
                rotation = 0;
            }
        }
        GlideApp.with(context)
                .load(storageReference)
                .transform(new MyTransformation(context, rotation))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model, Target<Drawable> target,
                                                boolean isFirstResource) {
                        progressBar.setVisibility(android.view.View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        progressBar.setVisibility(android.view.View.GONE);
                        return false;
                    }

                })
                .into(imageView);
    }

}
