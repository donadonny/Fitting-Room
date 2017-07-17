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
import android.widget.TextView;
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

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static android.app.Activity.RESULT_OK;
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
    String imageFileNameNoJPG;
    FirebaseDatabase database;
    ImageView userImageToUploadView;
    boolean canUpload = false;
    Button submitButton;
    EditText photo_description_edit_text;
    String photoDescription;
    TextView uploadPercent;

    public PhotoUploadActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_upload, container, false);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        photo_description_edit_text = (EditText) rootView.findViewById(R.id.photo_description_edit_text);
        uploadPercent = (TextView) rootView.findViewById(R.id.uploadPercent);


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
        File photoFile = null;
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
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            userImageToUploadView.setImageBitmap(bitmap);
        }
    }



    private void uploadPhoto(){
        submitButton.setVisibility(View.GONE);
        uploadPercent.setVisibility(View.VISIBLE);
        photo_description_edit_text.setVisibility(View.GONE);
        removeImageOnClick();


        StorageReference riversRef = mStorageRef.child("images/" + imageFileNameNoJPG);
        if(mCurrentPhotoPath != null) {
            final UploadTask uploadTask =
                    riversRef.putFile(photoURI);

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    DecimalFormat df = new DecimalFormat("#.#");
                    df.setRoundingMode(RoundingMode.CEILING);
                    String progressPercent = df.format(100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                    uploadPercent.setText(progressPercent + "% completed");

                }
            });


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get a URL to the uploaded content
                    Toast.makeText(getContext(), "Upload success!", Toast.LENGTH_SHORT).show();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Photo photo = new Photo();
                    photo.setUrl(imageFileNameNoJPG);
                    photo.setUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    photo.setLikes(0);
                    photo.setDislikes(0);
                    photo.setReports(0);
                    photo.setOccasion_subtitle(photoDescription);

                    DatabaseReference photoReference = database.getReference("Photos").child(imageFileNameNoJPG);
                    photoReference.setValue(photo);

                    DatabaseReference userPhotosDatabaseReference = database.getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(imageFileNameNoJPG);
                    userPhotosDatabaseReference.setValue(imageFileNameNoJPG);
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(getContext(), "Sending failed", Toast.LENGTH_SHORT).show();
                            submitButton.setVisibility(View.VISIBLE);
                            uploadPercent.setVisibility(View.GONE);
                            photo_description_edit_text.setVisibility(View.VISIBLE);
                            setImageOnClick();
                        }
                    });
        }
        else{
            Log.d(LOG_TAG, "mCurrentPhotoPath was null");
            Toast.makeText(getContext(), "Uh-Oh! Upload failed, please try again later!", Toast.LENGTH_SHORT).show();
        }

    }
    private boolean checkEditTextNotNull(){
         photoDescription =  photo_description_edit_text.getText().toString();
        boolean editTextVerifiedForUpload;
        if (photoDescription != null && !photoDescription.isEmpty() && !photoDescription.equals("") && photoDescription.length() <= 140){
            editTextVerifiedForUpload = true;
        }

        else{
            photo_description_edit_text.setError("Occasion can not be empty");
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
        try {
            takePhoto();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
