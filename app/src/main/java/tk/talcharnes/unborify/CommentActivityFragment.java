package tk.talcharnes.unborify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tk.talcharnes.unborify.CommentsData.Comment;
import tk.talcharnes.unborify.CommentsData.FirebaseCommentViewHolder;

/**
 * A placeholder fragment containing a simple view.
 *
 *
 *  * THANKS TO: https://www.learnhowtoprogram.com/android/data-persistence/firebase-recycleradapter

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
        mCommentReference = FirebaseDatabase.getInstance().getReference().child("Photos")
                .child(PhotoUtilities.removeWebPFromUrl(mUrl)).child("Comments");
        mOtherCommentReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(mPhotoUploader).child(PhotoUtilities.removeWebPFromUrl(mUrl)).child("Comments");
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mFirebaseAdapter);
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
                    Comment comment = new Comment();
                    comment.setPhoto_url(mUrl);
                    comment.setCommenter(mCurrentUser);
                    comment.setCommentString(mCommentEditText.getText().toString());
                    comment.setPhoto_Uploader(mPhotoUploader);


                    mCommentReference.push().setValue(comment);
                    mOtherCommentReference.push().setValue(comment);

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
