package tk.talcharnes.unborify.Utilities;

import android.content.Context;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import tk.talcharnes.unborify.R;

/**
 * Created by Khuram Chaudhry on 11/27/17.
 *
 * This class holds key static values and common methods for the Firebase Storage.
 */

public class StorageConstants {

    public final static String PROFILE_IMAGE = "profileImages";

    public static StorageReference getRef() {
        return FirebaseStorage.getInstance().getReference();
    }

    public static StorageReference getUserPhotoRef(String uid) {
        return getRef().child(PROFILE_IMAGE).child(uid + ".webp");
    }

    public static void loadImageUsingGlide(Context context, ImageView imageView,
                                           StorageReference storageReference) {
        GlideApp.with(context)
                .load(storageReference)
                .transform(new MyTransformation(context, 0))
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);
    }

    public static void loadProfileImage(Context context, ImageView imageView, String uid) {
        loadImageUsingGlide(context, imageView, getUserPhotoRef(uid));
    }

}
