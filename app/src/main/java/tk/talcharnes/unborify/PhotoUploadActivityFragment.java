package tk.talcharnes.unborify;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static java.lang.Math.toIntExact;
import static tk.talcharnes.unborify.MainActivityFragment.REQUEST_IMAGE_CAPTURE;

/**
 * A placeholder fragment containing a simple view.
 */
public class PhotoUploadActivityFragment extends Fragment {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private StorageReference mStorageRef;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private String mCurrentPhotoPath;
    Uri photoURI;
    int photoOrientation;
    String imageFileNameNoJPG;
    FirebaseDatabase database;
    ImageView userImageToUploadView;
    boolean canUpload = false;
    Button submitButton;
    EditText photo_description_edit_text;
    String photoDescription;
    ProgressBar progressBar;
    String user;
    private Uri compressedPhotoUri;
    private File compressedImage;
    File photoFile;


    public PhotoUploadActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        View rootView = inflater.inflate(R.layout.fragment_photo_upload, container, false);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        photo_description_edit_text = (EditText) rootView.findViewById(R.id.photo_description_edit_text);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        userImageToUploadView = (ImageView) rootView.findViewById(R.id.uploadedPhoto);
        setImageOnClick();

        submitButton = (Button) rootView.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean editTextNotNull = checkEditTextNotNull();
                if(canUpload && editTextNotNull) {
                    uploadPhoto();
                }
            }
        });

        return rootView;
    }

    private void takePhoto()throws IOException
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create the File where the photo should go
         photoFile = null;
        try {
            photoFile = getFile();

        } catch (IOException ex) {
            // Error occurred while creating the File

        }
        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(getContext(),
                    "com.example.android.fileprovider",
                    photoFile);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }
    private File getFile() throws IOException {
        askForPermission();
        Long timeStamp = System.currentTimeMillis();
        imageFileNameNoJPG = timeStamp + "_byUser_" + FirebaseAuth.getInstance().getCurrentUser().getUid();


        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileNameNoJPG,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void askForPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Explain to the user why we need to read the contacts
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            canUpload = true;
            Uri imageUri = photoURI;
            compressPhotoFactory(photoFile);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
           photoOrientation = PhotoUtilities.getCameraPhotoOrientation(getContext(), photoURI, mCurrentPhotoPath);

            userImageToUploadView.setImageBitmap(bitmap);
//          Ensure image is set the right way
            userImageToUploadView.setRotation(90);
        }
    }



    private void uploadPhoto(){
        submitButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        photo_description_edit_text.setVisibility(View.GONE);
        removeImageOnClick();

        final String compressedImageFileName = imageFileNameNoJPG + ".webp";
        StorageReference riversRef = mStorageRef.child("images/" + compressedImageFileName);
        if(mCurrentPhotoPath != null) {
            final UploadTask uploadTask =
//                    The following line makes app upload original photo
//                    riversRef.putFile(photoURI);
                    riversRef.putFile(compressedPhotoUri);

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    
                    int prog;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        prog = toIntExact(100* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    }
                    else{
                        prog = (int)(100* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                    }
                    progressBar.setProgress(prog);

                }
            });


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get a URL to the uploaded content
                    if(getContext() != null) {
                        Toast.makeText(getContext(), R.string.upload_success, Toast.LENGTH_SHORT).show();
                    }
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Photo photo = new Photo();
                    photo.setUrl(compressedImageFileName);
                    photo.setUser(user);
                    photo.setLikes(0);
                    photo.setDislikes(0);
                    photo.setReports(0);
                    photo.setOccasion_subtitle(photoDescription);
                    photo.setOrientation(photoOrientation);
                    photo.setAd(false);

                    DatabaseReference photoReference = database.getReference("Photos").child(imageFileNameNoJPG);
                    DatabaseReference userReference = database.getReference().child("users").child(user).child(imageFileNameNoJPG);

                    photoReference.setValue(photo);
                    userReference.setValue(photo);

                    if(getContext() != null) {
                        NavUtils.navigateUpFromSameTask(getActivity());
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            if(getContext() != null) {
                                Toast.makeText(getContext(), R.string.sending_failed, Toast.LENGTH_SHORT).show();
                            }
                            submitButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            photo_description_edit_text.setVisibility(View.VISIBLE);
                            setImageOnClick();
                        }
                    });
        }
        else{
            Log.d(LOG_TAG, "mCurrentPhotoPath was null");
            if(getContext() != null) {
                Toast.makeText(getContext(), R.string.upload_failed_error_string, Toast.LENGTH_SHORT).show();
            }
        }

    }
    private boolean checkEditTextNotNull(){
         photoDescription =  photo_description_edit_text.getText().toString();

        int photoDescriptionLength = photoDescription.length();
        boolean editTextVerifiedForUpload;
        if (photoDescription != null && !photoDescription.isEmpty() && !photoDescription.equals("") && photoDescriptionLength <= 40){
            editTextVerifiedForUpload = true;
        }
        else if (photoDescriptionLength > 40){
            int tooLongByThisManyCharacters = photoDescriptionLength - 40;
            photo_description_edit_text.setError("Please remove " + tooLongByThisManyCharacters + " characters");
            editTextVerifiedForUpload = false;
        }
        else{
            photo_description_edit_text.setError(getString(R.string.occasion_cannot_be_empty_string));
            editTextVerifiedForUpload = false;
        }
            return  editTextVerifiedForUpload;
    }
  
    private void removeImageOnClick(){
        userImageToUploadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    private void setImageOnClick(){
        userImageToUploadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    takePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void compressPhotoFactory(File photoFile){
        try {
            compressedImage = new Compressor(getContext()).setCompressFormat(Bitmap.CompressFormat.WEBP).compressToFile(photoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        compressedPhotoUri = Uri.fromFile(new File(compressedImage.getAbsolutePath()));
        Log.d(LOG_TAG, "COMPRESSED PHOTO URI = " + compressedPhotoUri.toString());

    }

}
