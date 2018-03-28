package tk.talcharnes.unborify.UserProfile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import tk.talcharnes.unborify.Models.UserModel;
import tk.talcharnes.unborify.Profile.changeNameDialogFragment;
import tk.talcharnes.unborify.Profile.changePasswordDialogFragment;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.GlideLoader2;
import tk.talcharnes.unborify.Utilities.PermissionConstants;
import tk.talcharnes.unborify.Utilities.StorageConstants;

/**
 * Created by Khuram Chaudhry on 10/19/17.
 * This Activity displays the user information.
 */

public class UserProfileActivity extends AppCompatActivity implements changeNameDialogFragment
        .onNameChangeListener {

    private static final String TAG = UserProfileActivity.class.getSimpleName();
    private final int PERMISSIONS_REQUEST = 123;;

    private final int FOLLOWING_TEXT = R.string.following, FOLLOW_TEXT = R.string.follow;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AvatarView avatarView;
    private TextView userNameText, userJoinedText;
    private IImageLoader imageLoader;
    private Button followingButton;
    private ImageButton backButton, settingButton, cameraButton;
    private boolean isFollowing;

    /**
     * This Adapter holds the different fragments for the Activity tabs.
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * Initializes basic initialization of components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initializeViews();
        initializeUserSettings();
        setListeners();
    }

    /**
     * This function initializes the UI elements for the Activity.
     */
    private void initializeViews() {
        avatarView = (AvatarView) findViewById(R.id.avatarImage);
        userNameText = (TextView) findViewById(R.id.user_profile_name);
        userJoinedText = (TextView) findViewById(R.id.user_date_joined);
        followingButton = (Button) findViewById(R.id.following_btn);
        backButton = (ImageButton) findViewById(R.id.back_button);
        settingButton = (ImageButton) findViewById(R.id.settings_button);
        cameraButton = (ImageButton) findViewById(R.id.camera_button);

        cameraButton.setVisibility(View.GONE);
        settingButton.setVisibility(View.GONE);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * This function sets up user info and the following button.
     */
    private void initializeUserSettings() {
        imageLoader = new GlideLoader2();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            final String uid = intent.getStringExtra("uid");
            if(uid.equals(DatabaseContants.getCurrentUser().getUid())) {
                settingButton.setVisibility(View.VISIBLE);
                cameraButton.setVisibility(View.VISIBLE);
                followingButton.setEnabled(false);
            }
            DatabaseContants.getUserRef(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                if (userModel != null) {
                                    String name = userModel.getName();
                                    userNameText.setText((name.length() > 18) ?
                                            name.substring(0, 15) + "...\n..." + name.substring(15)
                                            : name);
                                    String dateJoined = DatabaseContants.convertTime(
                                            userModel.getDateJoined());
                                    userJoinedText.setText(dateJoined);
                                    imageLoader.loadImage(avatarView, uid, userModel.getName());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                        }
                    });

            isFollowing = false;

            final DatabaseReference followingRef = DatabaseContants.getUserFollowingRef().child(uid);

            followingRef.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() ||
                                    uid.equals(DatabaseContants.getCurrentUser().getUid())) {
                                setFollowingButton(FOLLOWING_TEXT);
                                isFollowing = true;
                            } else {
                                setFollowingButton(FOLLOW_TEXT);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, databaseError.getMessage());
                        }
            });

            followingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isFollowing) {
                        followingRef.child(uid).removeValue();
                        setFollowingButton(FOLLOW_TEXT);
                    }
                    else {
                        followingRef.setValue(getString(FOLLOWING_TEXT));
                        setFollowingButton(FOLLOWING_TEXT);
                    }
                    isFollowing = !isFollowing;
                }
            });
        }
    }

    /**
     * This function sets the click listeners for the back, settings, and camera buttons.
     */
    public void setListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePictureDialog();
            }
        });
    }

    /**
     * This function sets the following button's text and color.
     */
    private void setFollowingButton(int buttonText) {
        followingButton.setText(getString(buttonText));

    }

    /**
     * This function adds the fragments to the ViewPager.
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            bundle.putString("uid", intent.getStringExtra("uid"));
        }

        UserPhotoFragment userPhotoFragment = new UserPhotoFragment();
        userPhotoFragment.setArguments(bundle);

        UserFollowingFragment userFollowingFragment = new UserFollowingFragment();
        userFollowingFragment.setArguments(bundle);

        UserLikesFragment userLikesFragment = new UserLikesFragment();
        userLikesFragment.setArguments(bundle);

        adapter.addFragment(userPhotoFragment, "PHOTOS");
        adapter.addFragment(userFollowingFragment, "FOLLOWING");
        adapter.addFragment(userLikesFragment, "LIKES");
        viewPager.setAdapter(adapter);
    }

    /**
     * This method shows a dialog of options for the Users on what they wish to edit on their
     * profile which includes changing their name, picture, and password.
     */
    private void showEditDialog() {
        String[] array = {"Change Name", "Change Password"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("What would you like to change?")
                .setItems(array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                changeNameDialogFragment fragment1
                                        = new changeNameDialogFragment();
                                fragment1.show(getSupportFragmentManager(),
                                        "changeNameDialogFragment");
                                break;
                            case 1:
                                changePasswordDialogFragment fragment2
                                        = new changePasswordDialogFragment();
                                fragment2.show(getSupportFragmentManager(),
                                        "changePasswordDialogFragment");
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
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

    /**
     * This method shows a dialog of options for the Users on how they want to take an image.
     */
    private void showChoosePictureDialog() {
        String[] permissions = PermissionConstants.checkIfPermissionsAreGranted(this,
                PermissionConstants.READ_STORAGE, PermissionConstants.WRITE_STORGAE,
                PermissionConstants.CAMERA);
        if(permissions.length > 0) {
            PermissionConstants.askForPermissions(this, PERMISSIONS_REQUEST, permissions);
        } else {
            EasyImage.openChooserWithGallery(this, "", 0);
        }
    }


    /**
     * This method shows a dialog if the user refused to grant all the permission required for the
     *  screen.
     */
    private void showGrantPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stop!")
                .setMessage("You must grant all the permissions request otherwise you can't use this feature.")
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    /**
     * This method handles the results from the permission asked.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length > 0) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    allPermissionsGranted = false;
                }
            }
            if (allPermissionsGranted) {
                EasyImage.openChooserWithGallery(this, "", 0);
            } else {
                showGrantPermissionDialog();
            }
        }
    }

    /**
     * This method handles results of the gallery or camera intent.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            EasyImage.handleActivityResult(requestCode, resultCode, data, this,
                    new DefaultCallback() {
                        @Override
                        public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                            Log.d(TAG, "Failed to pick a image, error: " + e.toString());
                        }

                        @Override
                        public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                            Log.d(TAG, "Successfully picked an image, source: " + source.name());
                            try {
                                Glide.with(avatarView.getContext())
                                        .load(imageFile)
                                        .transition(new DrawableTransitionOptions().crossFade())
                                        .into(avatarView);
                                saveImage(imageFile);
                            } catch (IOException e) {
                                Log.d(TAG, "onImagePicked: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onCanceled(EasyImage.ImageSource source, int type) {
                            //Cancel handling, you might wanna remove taken photo if it was canceled
                            Log.d(TAG, "Canceling taken photo");
                            if (source == EasyImage.ImageSource.CAMERA) {
                                File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getBaseContext());
                                if (photoFile != null) {
                                    Boolean isDeleted = photoFile.delete();
                                }
                            }
                        }
                    });
        }

    }

    /**
     * This method saves the profile image to the device.
     */
    private void saveImage(File file) throws IOException {
        File compressedImageFile = new Compressor(this).compressToFile(file);
        int size = (int) compressedImageFile.length();
        byte[] bytes = new byte[size];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(compressedImageFile));
        int bytesRead = buf.read(bytes, 0, bytes.length);
        buf.close();
        uploadImage(bytes);
    }

    /**
     * This method uploads the image to FireBase Storage.
     */
    private void uploadImage(byte[] data) {
        final FirebaseUser user = DatabaseContants.getCurrentUser();
        StorageReference profileImageRef = StorageConstants.getUserPhotoRef(user.getUid());
        UploadTask uploadTask = profileImageRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size,
                // content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(downloadUrl)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                }
                            }
                        });
                String profileUri = (downloadUrl != null) ? downloadUrl.toString() : "";
                DatabaseContants.getCurrentUserRef().child(UserModel.URI_KEY).setValue(profileUri);
            }
        });
    }

    /**
     * This is an interface method which retrieves results from the changeNameDialog.
     */
    @Override
    public void onChange(String name) {
        userNameText.setText(name);
    }

}