package tk.talcharnes.unborify.PhotoCard.Comments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tk.talcharnes.unborify.Models.CommentModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;

/**
 * Created by Tal on 9/3/2017.
 * <p>
 * THANKS TO: https://www.learnhowtoprogram.com/android/data-persistence/firebase-recycleradapter
 */

public class FirebaseCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final String TAG = FirebaseCommentViewHolder.class.getSimpleName();

    private View mView;
    private Context mContext;
    private String mCommenterID;
    private static String mUrl;
    private boolean mOriginalCommenter;
    private String mCommentString;
    private String photoUploader, mCurrentUser;


    public FirebaseCommentViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindComment(final CommentModel commentModel, String currentUser) {
        Log.d(TAG, "Loading CommentModel: " + commentModel.getCommentKey());
        TextView usernameTextView = (TextView) mView.findViewById(R.id.comment_username);
        TextView comment_textview = (TextView) mView.findViewById(R.id.comment_textview);
        ImageButton moreOptionsImageButton = (ImageButton) mView.findViewById(R.id.comment_more_options);

        mCurrentUser = currentUser;
        mCommenterID = commentModel.getCommenterUid();
        mCommentString = commentModel.getCommentMessage();
        if (mCommenterID != null && mCurrentUser != null) {
            if (!mCommenterID.isEmpty() && !mCurrentUser.isEmpty()) {
                mOriginalCommenter = mCommenterID.equals(currentUser);
            }
        } else {
            mOriginalCommenter = false;
        }
        photoUploader = commentModel.getPhotoUploaderUid();

        mUrl = PhotoUtilities.removeWebPFromUrl(commentModel.getPhotoUrl());

            //usernameTextView.setText(commentModel.getCommenter());
            setCommentorsName(mCommenterID, usernameTextView);
            comment_textview.setText(mCommentString);
            moreOptionsImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setUpMoreOptionsButton(view, commentModel, mOriginalCommenter);
                }
            });
        }

    public void setCommentorsName(String uid, final TextView usernameTextView) {
        if (uid != null && mCommentString != null) {
            FirebaseConstants.getRef().child(FirebaseConstants.USERS).child(uid)
                    .child(FirebaseConstants.USERNAME)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                String userName = dataSnapshot.getValue().toString();
                                usernameTextView.setText(userName);
                                if (mCommenterID.equals(photoUploader)) {
                                    usernameTextView.setTextColor(Color.BLUE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            usernameTextView.setText(R.string.anonymous_user);
                        }
                    });
        } else {
            usernameTextView.setText(R.string.anonymous_user);
        }
    }

    @Override
    public void onClick(View view) {
        final ArrayList<CommentModel> commentModels = new ArrayList<>();
//      Reference correct section of database below
        if (mUrl != null && !mUrl.isEmpty()) {
            DatabaseReference ref = FirebaseConstants.getRef().child(FirebaseConstants.PHOTOS)
                    .child(mUrl).child(FirebaseConstants.COMMENTS);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CommentModel commentModel = snapshot.getValue(CommentModel.class);
                        if (commentModel.getCommentMessage() != null) {
                            Log.d("FirebaseCommentVH", "Comment" + commentModel.getCommentMessage());
                            commentModels.add(snapshot.getValue(CommentModel.class));
                        }
                    }

                    int itemPosition = getLayoutPosition();

                    if (mOriginalCommenter) {
                        showEditCommentDialog(commentModels.get(itemPosition));
                    }
//                int itemPosition = getLayoutPosition();

//                Intent intent = new Intent(mContext, RestaurantDetailActivity.class);
//                intent.putExtra("position", itemPosition + "");
//                intent.putExtra("restaurants", Parcels.wrap(restaurants));
//
//                mContext.startActivity(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }
    }

    private void setUpMoreOptionsButton(View view, final CommentModel commentModel, boolean originalCommenter) {
        PopupMenu popup = new PopupMenu(mContext, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_report_comment:
                        FirebaseConstants.setReport(TAG, mView.getContext(),
                                commentModel.getCommentKey(), mCurrentUser);
                        return true;
                    case R.id.action_delete_comment:
                        deleteComment(commentModel);
                        return true;
                    case R.id.action_edit_comment:
                        showEditCommentDialog(commentModel);
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.comment_options, popup.getMenu());

        if (!originalCommenter) {
            popup.getMenu().removeItem(R.id.action_delete_comment);
            popup.getMenu().removeItem(R.id.action_edit_comment);
        }
        popup.show();
    }

    private void deleteComment(CommentModel commentModel) {
        DatabaseReference commentRef = FirebaseConstants.getRef().child(FirebaseConstants.PHOTOS)
                .child(mUrl).child(FirebaseConstants.COMMENTS).child(commentModel.getCommentKey());

        commentRef.removeValue();
        final DatabaseReference reportRef = FirebaseConstants.getRef()
                .child(FirebaseConstants.REPORTS).child(commentModel.getCommentKey());
        reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    reportRef.removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showEditCommentDialog(final CommentModel commentModel) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View dialogView = inflater.inflate(R.layout.dialog_edit_comment, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.comment_edit_dialog_box);
        if (mCommentString != null && !mCommentString.isEmpty()) {
            edt.setText(mCommentString);
        }
        dialogBuilder.setTitle("Edit CommentModel");

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newComment = edt.getText().toString();
                if (newComment.isEmpty() ||
                        newComment.equals("")
                        || newComment == null) {

                    edt.setError("CommentModel can not be empty");
                } else if (newComment.length() < 5) {
                    edt.setError("CommentModel must be longer than 5 characters");
                } else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child(FirebaseConstants.PHOTOS).child(mUrl)
                            .child(FirebaseConstants.COMMENTS).child(commentModel.getCommentKey())
                            .child(FirebaseConstants.COMMENT_STRING);

                    ref.setValue(newComment);

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
