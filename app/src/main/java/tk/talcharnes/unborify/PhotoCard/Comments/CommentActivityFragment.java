package tk.talcharnes.unborify.PhotoCard.Comments;

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
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import tk.talcharnes.unborify.Models.CommentModel;
import tk.talcharnes.unborify.Models.NotificationModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;

/**
 * A placeholder fragment containing a simple view.
 * <p>
 * <p>
 * * THANKS TO: https://www.learnhowtoprogram.com/android/data-persistence/firebase-recycleradapter
 */
public class CommentActivityFragment extends Fragment {

    private final String LOG_TAG = CommentActivityFragment.class.getSimpleName();

    private DatabaseReference mCommentReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;
    private String mPhotoUploader, mUrl, mCurrentUser;
    private EditText mCommentEditText;
    private ImageButton mSubmitCommentImageButton;
    private String mComment_key;
    private CommentModel commentModel;

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

        Log.d(LOG_TAG, "photoUserID: " + mPhotoUploader);
        Log.d(LOG_TAG, "url: " + mUrl);
        Log.d(LOG_TAG, "currentUser: " + mCurrentUser);

//        Fix reference here
        mCommentReference = DatabaseContants.getCommentRef();
        mRecyclerView = rootView.findViewById(R.id.comments_recyclerView);
        mCommentEditText = (EditText) rootView.findViewById(R.id.comment_edittext);
        mSubmitCommentImageButton = (ImageButton) rootView.findViewById(R.id.submit_comment_button);
        mSubmitCommentImageButton.setContentDescription(getString(R.string.submit));

        setUpFirebaseAdapter();
        setUpCommentSubmitting();

        return rootView;
    }

    private void setUpFirebaseAdapter() {
        Log.d(LOG_TAG, "Loading comments");
        Query query = mCommentReference.orderByChild("photoUrl").equalTo(mUrl);
        Log.d(LOG_TAG, query.getRef().toString());
        FirebaseRecyclerOptions<CommentModel> options =
                new FirebaseRecyclerOptions.Builder<CommentModel>()
                        .setQuery(query, CommentModel.class)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<CommentModel, FirebaseCommentViewHolder>
                (options) {

            @Override
            public FirebaseCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_template, parent, false);

                return new FirebaseCommentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(FirebaseCommentViewHolder holder, int position, CommentModel commentModel) {
                if(commentModel.getPhotoUrl() != null) {
                    holder.bindComment(commentModel, mCurrentUser);
                }
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
                if (mCommentEditText.getText().toString().isEmpty()) {
                    Log.d(LOG_TAG, "UserModel attempted to pass an empty commentModel.");
                    mCommentEditText.setError(getString(R.string.comment_empty_error));
                } else if (mCommentEditText.getText().toString().length() < 5) {
                    Log.d(LOG_TAG, "UserModel attempted to pass a short commentModel.");
                    mCommentEditText.setError(getString(R.string.comment_too_short_error));
                } else {
                    commentModel = new CommentModel();
                    commentModel.setPhotoUrl(mUrl);
                    commentModel.setCommenterUid(mCurrentUser);
                    commentModel.setCommentMessage(mCommentEditText.getText().toString());
                    commentModel.setPhotoUploaderUid(mPhotoUploader);
                    commentModel.setDate(System.currentTimeMillis());

                    Log.d(LOG_TAG, "New CommentModel:");
                    Log.d(LOG_TAG, "\tcommenter: " + commentModel.getCommenterUid());
                    Log.d(LOG_TAG, "\tcommentModel message: " + commentModel.getCommentMessage());
                    Log.d(LOG_TAG, "\tcommentModel url: " + commentModel.getPhotoUrl());
                    Log.d(LOG_TAG, "\tcommentModel photo uploader: " + commentModel.getPhotoUploaderUid());

                    mCommentReference.push().setValue(commentModel, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError,
                                               DatabaseReference databaseReference) {
                            mComment_key = databaseReference.getKey();
                            commentModel.setCommentKey(mComment_key);
                            mCommentReference.child(mComment_key).child(FirebaseConstants.COMMENT_KEY)
                                    .setValue(mComment_key);
                            Log.d(LOG_TAG, "commentkey = " + mComment_key);

                            if (!mPhotoUploader.equals(mCurrentUser)) {
                                Log.d(LOG_TAG, "The commenter is not the photo uploader");
                                Log.d(LOG_TAG, "Pushing commentModel notification");

                                DatabaseReference userNotificationRef = FirebaseConstants.getRef()
                                        .child(FirebaseConstants.USERS).child(mPhotoUploader)
                                        .child(FirebaseConstants.NOTIFICATION);

                                NotificationModel myNotification = new NotificationModel(false, mUrl,
                                        commentModel.getCommentMessage(), mCurrentUser);

                                userNotificationRef.push().setValue(myNotification);
                            }

                        }
                    });
                    mCommentEditText.setText("");
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening();
    }
}
