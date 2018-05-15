package tk.talcharnes.unborify.PhotoUpload;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import tk.talcharnes.unborify.Models.PhotoModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.AdConstants;
import tk.talcharnes.unborify.Utilities.Analytics;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.NotificatonConstants;
import tk.talcharnes.unborify.Utilities.PermissionConstants;
import tk.talcharnes.unborify.Utilities.PhotoUtilities;
import tk.talcharnes.unborify.Utilities.StorageConstants;
import tk.talcharnes.unborify.Utilities.Utils;

/**
 * Created by Tal.
 * This fragment handles the user interaction with the photo upload screen.
 */

public class PhotoUploadActivityFragment extends Fragment {

    private static final String LOG_TAG = PhotoUploadActivityFragment.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST = 123;

    private ImageView userImageToUploadView;
    private TextView imageTextView;
    private Button submitButton;
    private EditText photo_description_edit_text;
    private String photoDescription;
    private ProgressBar progressBar;
    private InterstitialAd mInterstitialAd;
    private byte[] bytes;
    private int rotation;
    private Activity activity;

    /**
     * Initializes basic initialization of components of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_photo_upload, container, false);

        activity = getActivity();

        AdView mAdView = (AdView) rootView.findViewById(R.id.photo_upload_adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        EasyImage
                .configuration(activity)
                .setImagesFolderName("FittingRoom_Data")
                .setAllowMultiplePickInGallery(false);

        photo_description_edit_text = (EditText) rootView.findViewById(R.id.photo_description_edit_text);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        userImageToUploadView = (ImageView) rootView.findViewById(R.id.uploadedPhoto);
        imageTextView = (TextView) rootView.findViewById(R.id.imageViewText);
        submitButton = (Button) rootView.findViewById(R.id.submitButton);
        initialize();

        return rootView;
    }

    /**
     * This method sets the event listeners and initializes the Interstitial Ad.
     */
    public void initialize() {

        mInterstitialAd = AdConstants.getInterstitialAd(activity);

        photo_description_edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) activity
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(photo_description_edit_text.getWindowToken(),
                                0);

                    }
                }
                return true;
            }
        });

        userImageToUploadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions = PermissionConstants.checkIfPermissionsAreGranted(activity,
                        PermissionConstants.READ_STORAGE, PermissionConstants.WRITE_STORGAE,
                        PermissionConstants.CAMERA);
                if(permissions.length > 0) {
                    PermissionConstants.askForPermissions(activity, PERMISSIONS_REQUEST, permissions);
                } else {
                    EasyImage.openChooserWithGallery(PhotoUploadActivityFragment.this,
                            "", 0);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean editTextNotNull = checkEditTextNotNull();
                if (bytes != null && editTextNotNull) {
                    String category = PhotoUploadActivity.getCategory();
                    if (category.isEmpty() || category.equals("All")) {
                        Toast.makeText(activity, "Please selete a category.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        uploadImage(bytes);
                    }
                }
            }
        });

    }

    /**
     * This method shows a dialog if the user refused to grant all the permission required for the
     *  screen.
     */
    private void showGrantPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
                EasyImage.openChooserWithGallery(PhotoUploadActivityFragment.this, "", 0);
            } else {
                showGrantPermissionDialog();
            }
        }
    }

    /**
     * This method takes the selected image from user's camera or photo gallery and compresses it to
     *  a byte array.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            EasyImage.handleActivityResult(requestCode, resultCode, data, activity,
                    new DefaultCallback() {
                        @Override
                        public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                            Log.d(LOG_TAG, "Failed to pick a image, error: " + e.toString());
                        }

                        @Override
                        public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                            File imageFile = imageFiles.get(0);
                            rotation = PhotoUtilities.getCameraPhotoOrientation(activity,
                                    Uri.fromFile(imageFile), imageFile.getAbsolutePath());

                            Log.d(LOG_TAG, "Successfully picked an image, source: " + source.name());
                            try {
                                imageTextView.setVisibility(View.GONE);
                                File compressedFile = new Compressor(activity).compressToFile(imageFile);
                                int size = (int) compressedFile.length();
                                bytes = new byte[size];
                                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(compressedFile));
                                int i = buf.read(bytes, 0, bytes.length);
                                buf.close();
                                Glide.with(activity).asBitmap().load(bytes).into(userImageToUploadView);

                            } catch (IOException e) {
                                Log.d(LOG_TAG, "Failed to compress image, error: " + e.toString());
                            }
                        }

                        @Override
                        public void onCanceled(EasyImage.ImageSource source, int type) {
                            //Cancel handling, you might wanna remove taken photo if it was canceled
                            Log.d(LOG_TAG, "Canceling taken photo");
                            if (source == EasyImage.ImageSource.CAMERA) {
                                File photoFile = EasyImage.lastlyTakenButCanceledPhoto(activity);
                                if (photoFile != null) {
                                    Boolean isDeleted = photoFile.delete();
                                }
                            }
                        }
                    });
        }
    }

    /**
     * This method upload the image to the Firebase Storage and create a photo model and stores in
     *  the database.
     *  @param data - byte array of an image to upload.
     */
    private void uploadImage(byte[] data) {
        submitButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        final String userId = DatabaseContants.getCurrentUser().getUid();
        photo_description_edit_text.setVisibility(View.GONE);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/webp")
                .build();

        final String photoName = System.currentTimeMillis() + "_byUser_" + userId;
        StorageReference imageRef = StorageConstants.getImageRef(photoName + ".webp");
        UploadTask uploadTask = imageRef.putBytes(data, metadata);

        final Resources res = getResources();

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = 100.0 * (taskSnapshot.getBytesTransferred() /
                        taskSnapshot.getTotalByteCount());
                progressBar.setProgress((int) progress);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size,
                // content-type, and download URL.
                Log.d(LOG_TAG, "Image is successfully uploaded: " + taskSnapshot.getMetadata());

                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Analytics.registerUpload(activity, userId);

                PhotoModel photoModel = new PhotoModel(userId, photoDescription, "none",
                        0, 0, rotation, photoName + ".webp");

                DatabaseContants.getPhotoRef().child(photoName).setValue(photoModel);

                Utils.photosUploadedCounter++;
                if (Utils.photosUploadedCounter % 2 == 0) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                NotificatonConstants.sendNotification(activity,
                        res.getString(R.string.failure_title),
                        res.getString(R.string.failure_message),
                        NotificatonConstants.UPLOAD_ID);
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                NotificatonConstants.sendNotification(activity,
                        res.getString(R.string.success_title),
                        res.getString(R.string.success_message),
                        NotificatonConstants.UPLOAD_ID);
            }
        });
    }

    /**
     * This method validates the photoDescription EditText.
     */
    private boolean checkEditTextNotNull() {
        photoDescription = photo_description_edit_text.getText().toString();

        int photoDescriptionLength = photoDescription.length();

        if (photoDescriptionLength == 0) {
            photo_description_edit_text.setError(getString(R.string.occasion_cannot_be_empty_string));
        } else if (photoDescriptionLength > 40) {
            int tooLongByThisManyCharacters = photoDescriptionLength - 40;
            photo_description_edit_text.setError("Please remove " + tooLongByThisManyCharacters +
                    " characters");
        } else {
            return true;
        }
        return false;
    }

}