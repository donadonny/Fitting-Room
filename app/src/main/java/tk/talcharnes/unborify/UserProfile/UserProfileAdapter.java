package tk.talcharnes.unborify.UserProfile;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import tk.talcharnes.unborify.PhotoCard.Comments.CommentActivity;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;

/**
 * Created by khuramchaudhry on 10/19/17.
 */

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.SingleItemRowHolder> {

    public static final String LOG_TAG = UserProfileAdapter.class.getSimpleName();

    private Context mContext;
    private String uid, type;
    private List<String> urlList;
    private StorageReference storageReference;

    public UserProfileAdapter(Context context, String uid, List<String> urls, String type) {
        this.urlList = urls;
        this.mContext = context;
        this.uid = uid;
        this.type = type;
        if (type.equals("Photos")) {
            storageReference = FirebaseStorage.getInstance().getReference()
                    .child(FirebaseConstants.IMAGES);
        } else {
            storageReference = FirebaseStorage.getInstance().getReference()
                    .child(FirebaseConstants.PROFILE_IMAGE);
        }
    }

    @Override
    public UserProfileAdapter.SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card, viewGroup, false);
        return new UserProfileAdapter.SingleItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int i) {
        final String url = urlList.get(i) + ".webp";
        final StorageReference photoRef = storageReference.child(url);
        Log.d(LOG_TAG, photoRef.getPath());
        FirebaseConstants.loadImageUsingGlide(mContext, holder.photo, photoRef,
                holder.progressBar);

        FirebaseConstants.getRef().child(FirebaseConstants.PHOTOS).child(urlList.get(i))
                .child(FirebaseConstants.VOTES).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String rating = String.valueOf(dataSnapshot.getValue());
                            if (!type.equals("Photos")) {
                                holder.userRating.setBackgroundColor(ContextCompat
                                        .getColor(mContext, R.color.bg_screen4));
                                holder.userRating.setImageDrawable(ContextCompat
                                        .getDrawable(mContext, R.drawable.ic_following));
                                holder.photo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        openCommentActivity(urlList.get(i), FirebaseConstants.getUser().getUid());
                                    }
                                });
                            } else if (rating.equals("likes")) {
                                holder.userRating.setBackgroundColor(ContextCompat
                                        .getColor(mContext, R.color.bg_screen2));
                                holder.userRating.setImageDrawable(ContextCompat
                                        .getDrawable(mContext, R.drawable.ic_thumb_up_white_24dp));
                                holder.photo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        openCommentActivity(urlList.get(i), FirebaseConstants.getUser().getUid());
                                    }
                                });

                            } else {
                                holder.userRating.setBackgroundColor(ContextCompat
                                        .getColor(mContext, R.color.bg_screen1));
                                holder.userRating.setImageDrawable(ContextCompat
                                        .getDrawable(mContext, R.drawable.ic_thumb_down_white_24dp));
                                holder.photo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        openCommentActivity(urlList.get(i), FirebaseConstants.getUser().getUid());
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
    private void openCommentActivity( String url, String currentUser){
        String deleteThisPartOfUrlAndEverythingBeforeToGetUser = "_byUser_";
        int index =
                url.indexOf(deleteThisPartOfUrlAndEverythingBeforeToGetUser);
        String photoUserID = url.substring(index);
        Log.d(LOG_TAG, "photoUserID = " + photoUserID);


        Intent intent = new Intent(mContext, CommentActivity.class);

       intent.putExtra("photoUserID", photoUserID);
        intent.putExtra("url", url);
        intent.putExtra("currentUser", currentUser);

        mContext.startActivity(intent);


    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        private ImageView photo, userRating;
        private ProgressBar progressBar;

        public SingleItemRowHolder(View view) {
            super(view);
            if (!urlList.isEmpty()) {
                photo = (ImageView) view.findViewById(R.id.photoImageView);
                userRating = (ImageView) view.findViewById(R.id.ratingImageView);
                progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            }
        }

    }
}