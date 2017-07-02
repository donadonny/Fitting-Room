package tk.talcharnes.unborify;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import static tk.talcharnes.unborify.MainActivityFragment.REQUEST_IMAGE_CAPTURE;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private StorageReference mStorageRef;
    String photoName;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private String mCurrentPhotoPath;
    Uri photoURI;
    FirebaseDatabase database;
    private String imageFileNameNoJPG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            Toast.makeText(this, "Signing out", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            return true;
        }
        if (id == R.id.action_my_photos) {
            Toast.makeText(this, "opening photos", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void takePic(View view) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = getFile();
        } catch (IOException ex) {
            // Error occurred while creating the File

        }
        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    photoFile);
            photoName = photoFile.getName();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }
    private File getFile() throws IOException {
        askForPermission();
        Long timeStamp = System.currentTimeMillis();
        imageFileNameNoJPG = FirebaseAuth.getInstance().getCurrentUser().getUid()+ "_AtTimeInMillis_" + timeStamp;


        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Toast.makeText(getApplicationContext(), "Uploading photo", Toast.LENGTH_SHORT).show();
            StorageReference riversRef = mStorageRef.child("images/" + photoName);


            if(mCurrentPhotoPath != null) {
                UploadTask uploadTask =
                        riversRef.putFile(photoURI);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Toast.makeText(getApplicationContext(), "Upload success!", Toast.LENGTH_SHORT).show();
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        Photo photo = new Photo();
                        photo.setUrl(downloadUrl.toString());
                        photo.setUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        photo.setLikes(0);
                        photo.setDislikes(0);
                        photo.setReports(0);

                        DatabaseReference photoReference = database.getReference("Photos").child(imageFileNameNoJPG);
                        photoReference.setValue(photo);
//                        photoReference.push().setValue(photo);

                        DatabaseReference userPhotosDatabaseReference = database.getReference("users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(imageFileNameNoJPG);
                        userPhotosDatabaseReference.setValue(downloadUrl.toString());
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(getApplicationContext(), "Sending failed", Toast.LENGTH_SHORT).show();

                                // ...
                            }
                        });
            }
            else{
                Log.d(LOG_TAG, "mCurrentPhotoPath was null");
            }

        }
    }
}
