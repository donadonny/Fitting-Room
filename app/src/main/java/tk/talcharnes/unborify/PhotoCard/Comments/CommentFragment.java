package tk.talcharnes.unborify.PhotoCard.Comments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import tk.talcharnes.unborify.Models.CommentModel;
import tk.talcharnes.unborify.Models.NotificationModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;

/**
 * Created by Tal on 9/3/2017.
 * This fragment handles comment submission and display comments.
 * THANKS TO: https://www.learnhowtoprogram.com/android/data-persistence/firebase-recycleradapter
 */
public class CommentFragment extends Fragment {

    private final String TAG = CommentFragment.class.getSimpleName();

    private DatabaseReference mCommentReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;
    private String mPhotoUploader, mUrl, mCurrentUser;
    private EditText mCommentEditText;
    private ImageButton mSubmitCommentImageButton;
    private Activity activity;
    private View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_comment, container, false);
        activity = getActivity();

        initialize();

        return rootView;
    }

    /**
     * Initializes the basic components.
     */
    public void initialize() {
        Intent intent = activity.getIntent();
        mPhotoUploader = intent.getStringExtra("photoUserID");
        mUrl = PhotoUtilities.removeWebPFromUrl(intent.getStringExtra("url"));
        mCurrentUser = intent.getStringExtra("currentUser");

        if(mPhotoUploader == null || mUrl == null || mCurrentUser == null) {
            activity.finish();
        }

        mCommentReference = DatabaseContants.getCommentRef().child(mUrl);
        mRecyclerView = rootView.findViewById(R.id.comments_recyclerView);
        mCommentEditText = (EditText) rootView.findViewById(R.id.comment_edittext);
        mSubmitCommentImageButton = (ImageButton) rootView.findViewById(R.id.submit_comment_button);
        mSubmitCommentImageButton.setContentDescription(getString(R.string.submit));

        setUpFirebaseAdapter();
        setUpCommentSubmission();
    }

    /**
     * This method retrieves the comments for the photo and displays them.
     */
    private void setUpFirebaseAdapter() {
        FirebaseRecyclerOptions<CommentModel> options =
                new FirebaseRecyclerOptions.Builder<CommentModel>()
                        .setQuery(mCommentReference, CommentModel.class).build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<CommentModel, FirebaseCommentViewHolder>
                (options) {

            @Override
            public FirebaseCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_template, parent, false);

                return new FirebaseCommentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(FirebaseCommentViewHolder holder, int position,
                                            CommentModel commentModel) {
                if(commentModel.getPhotoUrl() != null) {
                    holder.bindComment(commentModel, mCurrentUser);
                }
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mRecyclerView.setAdapter(mFirebaseAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method sets a clicklistner which handles uploading comment.
     */
    private void setUpCommentSubmission() {
        mSubmitCommentImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String comment = mCommentEditText.getText().toString();

                if (isTheCommentVaild(comment)) {
                    CommentModel commentModel = new CommentModel(mCurrentUser, comment,
                            System.currentTimeMillis(), mUrl, "", mPhotoUploader);

                    mCommentReference.push().setValue(commentModel,
                            new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError,
                                               DatabaseReference databaseReference) {

                            String mCommentKey = databaseReference.getKey();
                            mCommentReference.child(mCommentKey).child(CommentModel.COMMENT_KEY)
                                    .setValue(mCommentKey);

                            if (!mPhotoUploader.equals(mCurrentUser)) {
                                DatabaseReference notificationRef = DatabaseContants
                                        .getNotificationRef(mPhotoUploader);
                                NotificationModel notification = new NotificationModel(false,
                                        mUrl, comment, mCurrentUser);
                                notificationRef.push().setValue(notification);
                            }

                        }
                    });
                    mCommentEditText.setText("");
                }

            }
        });
    }

    /**
     * This checks the user comment is valid comment.
     * @param comment - user comment.
     */
    public boolean isTheCommentVaild(String comment) {
        if(comment.isEmpty()) {
            mCommentEditText.setError(getString(R.string.comment_empty_error));
            return false;
        } else if(comment.length() < 5) {
            mCommentEditText.setError(getString(R.string.comment_too_short_error));
            return false;
        }

        return true;
    }

    /**
     * Override onStart for FirebaseAdapter to start listening.
     */
    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAdapter.startListening();
    }

    /**
     * Override onStop for FirebaseAdapter to stop listening.
     */
    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening();
    }
}
