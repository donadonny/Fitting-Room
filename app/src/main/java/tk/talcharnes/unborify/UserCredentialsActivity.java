package tk.talcharnes.unborify;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import io.fabric.sdk.android.Fabric;
import java.util.Arrays;
import java.util.Calendar;
import tk.talcharnes.unborify.Models.UserModel;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Khuram Chaudhry on 9/23/17.
 * This activity handles basic User authorization with email or Google Api.
 */

public class UserCredentialsActivity extends AppCompatActivity {

    private static final String TAG = UserCredentialsActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /**
     * Initializes basic initialization of components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        initialize();

    }

    /**
     * Checks if the user is logged in. If not, then the user is prompt to log in.
     */
    private void initialize() {
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = DatabaseContants.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in: " + user.getUid());

                    final DatabaseReference userRef = DatabaseContants.getCurrentUserRef();
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        Log.d(TAG, "New User");
                                        long time =  Calendar.getInstance().getTime().getTime();

                                        UserModel newUser = new UserModel(user.getDisplayName(),
                                                user.getEmail(), null, time);
                                        userRef.setValue(newUser);

                                        DatabaseContants.setToken();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d(TAG, "Failed to Login: " + databaseError.getMessage());
                                }
                            });

                    Intent intent = new Intent(UserCredentialsActivity.this, MainActivity.class);
                    startActivity(intent);
                    Log.d(TAG, "User is signed in starting up MainActivity.");

                } else {
                    // User is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                                            new AuthUI.IdpConfig.TwitterBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                    Log.d(TAG, "Starting up Firebase UI.");
                }

            }
        };
    }

    /**
     * Checks if the user sign in and if not then it checks what was error.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...)
        // when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode != RESULT_OK)
                if (response == null) {
                    Log.d(TAG, "Login failed due to back press.");
                } else {
                    if (response.getError() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(response.getError().getErrorCode())
                                .setMessage(response.getError().getMessage())
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
        }
    }

    /**
     * Adds the Auth Listener when the app is started up.
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Removes the Auth Listener when the app is closed.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
