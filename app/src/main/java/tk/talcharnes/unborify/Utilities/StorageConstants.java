package tk.talcharnes.unborify.Utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import tk.talcharnes.unborify.R;

/**
 * Created by Khuram Chaudhry on 11/27/17.
 *
 * This class holds key static values and common methods for the Firebase Storage.
 */

public class StorageConstants {

    public final static String IMAGES = "images";
    public final static String PROFILE_IMAGE = "profileImages";

    public static StorageReference getRef() {
        return FirebaseStorage.getInstance().getReference();
    }

    public static StorageReference getUserPhotoRef(String uid) {
        return getRef().child(PROFILE_IMAGE).child(uid + ".webp");
    }

    public static StorageReference getImageRef(String url) {
        return getRef().child(IMAGES).child(url);
    }

    public static void loadProfileImage(Context context, ImageView imageView, String uid) {
        loadImageUsingGlide(context, imageView, getUserPhotoRef(uid));
    }

    public static void loadImageUsingGlide(Context context, ImageView imageView,
                                           StorageReference storageReference) {
        GlideApp.with(context)
                .load(storageReference)
                .transform(new MyTransformation(context, 0))
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);
    }

    public static void loadImageUsingGlide(Context context, ImageView imageView,
                                           StorageReference storageReference,
                                           final ProgressBar progressBar) {
        GlideApp.with(context)
                .load(storageReference)
                .transform(new MyTransformation(context, 0))
                .placeholder(R.mipmap.ic_launcher)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model, Target<Drawable> target,
                                                boolean isFirstResource) {
                        if (progressBar != null) {
                            progressBar.setVisibility(android.view.View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        if (progressBar != null) {
                            progressBar.setVisibility(android.view.View.GONE);
                        }
                        return false;
                    }

                })
                .into(imageView);
    }

    public static void loadImageUsingGlide(Context context, ImageView imageView,
                                           StorageReference storageReference,
                                           final ProgressBar progressBar, int rotation) {
        if (context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            if (rotation != 0) {
                rotation = 0;
            }
        } else if (context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            if (rotation != 0) {
                rotation = 0;
            }
        }
        GlideApp.with(context)
                .load(storageReference)
                .transform(new MyTransformation(context, rotation))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model, Target<Drawable> target,
                                                boolean isFirstResource) {
                        progressBar.setVisibility(android.view.View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        progressBar.setVisibility(android.view.View.GONE);
                        return false;
                    }

                })
                .into(imageView);
    }

}
