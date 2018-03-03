package tk.talcharnes.unborify.PhotoCard.Comments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import tk.talcharnes.unborify.Models.CommentModel;
import tk.talcharnes.unborify.Models.ReportModel;
import tk.talcharnes.unborify.Models.UserModel;
import tk.talcharnes.unborify.PhotoCard.ZoomPhotoActivity;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.StorageConstants;

/**
 * Created by Tal on 9/3/2017.
 * This class is to view, edit, and delete comment.
 * THANKS TO: https://www.learnhowtoprogram.com/android/data-persistence/firebase-recycleradapter
 */

public class FirebaseCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final String TAG = FirebaseCommentViewHolder.class.getSimpleName();

    private View mView;
    private Context mContext;
    private boolean mOriginalCommenter;
    private String mCurrentUserUid;
    private CommentModel commentModel;


    FirebaseCommentViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    /**
     * This method binds the comment.
     */
    void bindComment(final CommentModel commentModel, String currentUser) {
        this.commentModel = commentModel;
        mCurrentUserUid = currentUser;
        mOriginalCommenter = commentModel.getCommenterUid().equals(currentUser);
        final Integer orientation = commentModel.getOrientation();
        final String mUrl = commentModel.getPhotoUrl() + ".webp";

        TextView nameTextView = (TextView) mView.findViewById(R.id.comment_username);
        TextView commentTextView = (TextView) mView.findViewById(R.id.comment_textview);
        ImageButton optionsImageButton = (ImageButton) mView.findViewById(R.id.comment_more_options);

        if (orientation == null) {
            String mCommenterId = commentModel.getCommenterUid();
            String mCommentMessage = commentModel.getCommentMessage();

            setCommentersName(currentUser, mCommenterId, mCommentMessage, nameTextView);
            commentTextView.setText(mCommentMessage);

            optionsImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setUpMoreOptionsButton(view, commentModel, mOriginalCommenter);
                }
            });
        }
        else {
            mView.findViewById(R.id.comment_relative_layout).setVisibility(View.GONE);
            mView.findViewById(R.id.comment_photo_view).setVisibility(View.VISIBLE);
            ProgressBar progressBar = mView.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);

            StorageReference storageRef = StorageConstants.getImageRef(mUrl);

            StorageConstants.loadImageUsingGlide(mContext,
                    (ImageView) mView.findViewById(R.id.comment_photo_view), storageRef,
                    progressBar, orientation);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ZoomPhotoActivity.class);
                    intent.putExtra("url", mUrl);
                    intent.putExtra("orientation", orientation);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    /**
     * This method set the commenter's name on the comment.
     */
    private void setCommentersName(String uid, final String mCommenterId,
                                   final String mCommentMessage, final TextView nameTextView) {
        if (uid != null && mCommentMessage != null) {
            DatabaseContants.getUserRef(uid).child(UserModel.NAME_KEY)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name =  dataSnapshot.getValue(String.class);
                    if (name != null) {
                        nameTextView.setText(name);
                        if (mCurrentUserUid.equals(mCommenterId)) {
                            nameTextView.setTextColor(Color.BLUE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, databaseError.getMessage());
                    nameTextView.setText(R.string.anonymous_user);
                }
            });
        } else {
            nameTextView.setText(R.string.anonymous_user);
        }
    }

    /**
     * This method presents an edit comment dialog when the user clicks on the comment.
     */
    @Override
    public void onClick(View view) {
        if (!mOriginalCommenter) {
            editComment(commentModel);
        }
    }

    /**
     * This method show a popup menu to edit, delete, and report comment.
     */
    private void setUpMoreOptionsButton(View view, final CommentModel commentModel,
                                        boolean originalCommenter) {
        PopupMenu popup = new PopupMenu(mContext, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_report_comment:
                        setReport(commentModel.getCommentKey());
                        return true;
                    case R.id.action_delete_comment:
                        deleteComment(commentModel);
                        return true;
                    case R.id.action_edit_comment:
                        editComment(commentModel);
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

    /**
     * This method records the user's report in the database.
     * @param commentKey - the comment key of the comment to report.
     */
    private void setReport(String commentKey) {
        final DatabaseReference reportRef = DatabaseContants.getReportRef(commentKey);
        reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(ReportModel.REPORTED_BY_KEY).child(mCurrentUserUid).exists()) {
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
                    reportRef.child(ReportModel.REPORTED_BY_KEY).child(mCurrentUserUid)
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
     * This method deletes the comment from the database.
     * @param commentModel - the comment Object to delete.
     */
    private void deleteComment(CommentModel commentModel) {
        DatabaseReference commentRef = DatabaseContants.getCommentRef()
                .child(commentModel.getCommentKey());

        commentRef.removeValue();

        final DatabaseReference reportRef = DatabaseContants
                .getReportRef(commentModel.getCommentKey());

        reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    reportRef.removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    /**
     * This method displays a dialog to edit the comment and updates the comment to the database.
     * @param commentModel - the comment Object to edit.
     */
    private void editComment(final CommentModel commentModel) {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_comment, null);
        final EditText commentText = (EditText) dialogView.findViewById(R.id.comment_edit_dialog_box);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Edit Comment")
                .setView(R.id.comment_edit_dialog_box)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String comment = commentText.getText().toString();
                        if(comment.length() < 10) {
                            commentText.setError("CommentModel must be longer than 9 characters");
                        }
                        DatabaseContants.getCommentRef().child(commentModel.getCommentKey())
                                .child(CommentModel.COMMENT_MESSAGE_KEY).setValue(comment);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }
}
