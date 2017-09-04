package tk.talcharnes.unborify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tk.talcharnes.unborify.CommentsData.Comment;
import tk.talcharnes.unborify.CommentsData.FirebaseCommentViewHolder;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;

/**
 * A placeholder fragment containing a simple view.
 * <p>
 * <p>
 * * THANKS TO: https://www.learnhowtoprogram.com/android/data-persistence/firebase-recycleradapter
 */
public class CommentActivityFragment extends Fragment {
    private DatabaseReference mCommentReference;
    private DatabaseReference mOtherCommentReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;
    private String mPhotoUploader;
    private String mUrl;
    private String mCurrentUser;
    private EditText mCommentEditText;
    private ImageButton mSubmitCommentImageButton;
    private final String LOG_TAG = CommentActivityFragment.class.getSimpleName();
    String mComment_key;
    Comment comment;
    public CommentActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comment, container, false);

        Intent intent = getActivity().getIntent();
        mPhotoUploader = intent.getStringExtra("photoUserID");
        mUrl = intent.getStringExtra("url");
        mCurrentUser = intent.getStringExtra("currentUser");
//        Fix reference here
        mCommentReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.PHOTOS)
                .child(PhotoUtilities.removeWebPFromUrl(mUrl)).child(FirebaseConstants.COMMENTS);
        mOtherCommentReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS)
                .child(mPhotoUploader).child(PhotoUtilities.removeWebPFromUrl(mUrl)).child(FirebaseConstants.COMMENTS);
        mRecyclerView = rootView.findViewById(R.id.comments_recyclerView);
        mCommentEditText = (EditText) rootView.findViewById(R.id.comment_edittext);
        mSubmitCommentImageButton = (ImageButton) rootView.findViewById(R.id.submit_comment_button);


        setUpFirebaseAdapter();
        setUpCommentSubmitting();

        return rootView;
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Comment, FirebaseCommentViewHolder>
                (Comment.class, R.layout.comment_template, FirebaseCommentViewHolder.class,
                        mCommentReference) {

            @Override
            protected void populateViewHolder(FirebaseCommentViewHolder viewHolder,
                                              Comment model, int position) {
                viewHolder.bindComment(model);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mFirebaseAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setUpCommentSubmitting() {
        mSubmitCommentImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCommentEditText.getText().toString().isEmpty() ||
                        mCommentEditText.getText().toString().equals("")
                        || mCommentEditText.getText().toString() == null) {

                    mCommentEditText.setError("Comment can not be empty");
                } else if (mCommentEditText.getText().toString().length() < 5) {
                    mCommentEditText.setError("Comment must be longer than 5 characters");
                } else {
                    comment = new Comment();
                    comment.setPhoto_url(mUrl);
                    comment.setCommenter(mCurrentUser);
                    comment.setCommentString(mCommentEditText.getText().toString());
                    comment.setPhoto_Uploader(mPhotoUploader);


                    mCommentReference.push().setValue(comment, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError,
                                               DatabaseReference databaseReference) {
                            mComment_key = databaseReference.getKey();
                            comment.setComment_key(mComment_key);
                            mCommentReference.child(mComment_key).child(FirebaseConstants.COMMENT_KEY)
                                    .setValue(mComment_key);
                            Log.d(LOG_TAG, "commentkey = " + mComment_key);
                            mOtherCommentReference.child(mComment_key).setValue(comment);

                        }
                    });
                    mCommentEditText.setText("");
                }

            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
    }
}
