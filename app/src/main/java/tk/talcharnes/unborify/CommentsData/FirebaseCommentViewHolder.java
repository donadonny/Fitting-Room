package tk.talcharnes.unborify.CommentsData;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;

/**
 * Created by Tal on 9/3/2017.
 * <p>
 * THANKS TO: https://www.learnhowtoprogram.com/android/data-persistence/firebase-recycleradapter
 */

public class FirebaseCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private View mView;
    private Context mContext;
    private String mphotoUserID;
    private String mUrl;


    public FirebaseCommentViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();

        itemView.setOnClickListener(this);
    }

    public void bindComment(final Comment comment) {
        TextView usernameTextView = (TextView) mView.findViewById(R.id.comment_username);
        TextView comment_textview = (TextView) mView.findViewById(R.id.comment_textview);
        ImageButton moreOptionsImageButton = (ImageButton) mView.findViewById(R.id.comment_more_options);


        mphotoUserID = comment.getCommenter();
        mUrl = PhotoUtilities.removeWebPFromUrl(comment.getPhoto_url());

        //usernameTextView.setText(comment.getCommenter());
        setCommentorsName(comment.getCommenter(), usernameTextView);
        comment_textview.setText(comment.getCommentString());
        moreOptionsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpMoreOptionsButton(view, comment);
            }
        });

    }

    public void setCommentorsName(String uid, final TextView usernameTextView) {
        FirebaseDatabase.getInstance().getReference(FirebaseConstants.USERDATA).child(uid).child(FirebaseConstants.USERNAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            usernameTextView.setText(dataSnapshot.getValue().toString());

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        usernameTextView.setText("BOB");
                    }
                });
    }

    @Override
    public void onClick(View view) {
        final ArrayList<Comment> comments = new ArrayList<>();
//      Reference correct section of database below
        Toast.makeText(mContext, "Item Clicked", Toast.LENGTH_SHORT).show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.PHOTOS)
                .child(mUrl).child(FirebaseConstants.COMMENTS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    comments.add(snapshot.getValue(Comment.class));
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

    private void setUpMoreOptionsButton(View view, final Comment comment) {
        PopupMenu popup = new PopupMenu(mContext, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_report_comment:
                        Toast.makeText(mContext, "set up report function", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_delete_comment:
                        deleteComment(comment);
                        return true;
                    case R.id.action_edit_comment:
                        editComment(comment);
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.comment_options, popup.getMenu());
//      // TODO: 9/4/2017 ensure that only user that posted the comment has option to delete and edit it
//        popup.getMenu().removeItem(R.id.action_delete_comment);
//        popup.getMenu().removeItem(R.id.action_edit_comment);
        popup.show();
    }

    private void editComment(Comment comment) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.PHOTOS)
                .child(mUrl).child(FirebaseConstants.COMMENTS).child(comment.getComment_key())
                .child(FirebaseConstants.COMMENT_STRING);

//       // TODO: 9/4/2017 create dialogue box to get new comment and update both photo comments reference and users
//        photo comments reference
        String newComment = "EDITED";
        ref.setValue(newComment);

    }

    private void deleteComment(Comment comment) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.PHOTOS)
                .child(mUrl).child(FirebaseConstants.COMMENTS).child(comment.getComment_key());
        ref.removeValue();

        DatabaseReference mOtherCommentReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS)
                .child(comment.getPhoto_Uploader()).child(PhotoUtilities.removeWebPFromUrl(mUrl)).child(FirebaseConstants.COMMENTS)
                .child(comment.getComment_key());
        mOtherCommentReference.removeValue();
    }

}
