package tk.talcharnes.unborify.Utilities;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import agency.tango.android.avatarview.AvatarPlaceholder;
import agency.tango.android.avatarview.ImageLoaderBase;
import agency.tango.android.avatarview.views.AvatarView;

/**
 * Created by khuramchaudhry on 10/19/17.
 * This class is a custom loader for Glide.
 */

public class GlideLoader2 extends ImageLoaderBase {

    public static final String TAG = GlideLoader2.class.getSimpleName();

    public GlideLoader2() {
        super();
    }

    public GlideLoader2(String defaultPlaceholderString) {
        super(defaultPlaceholderString);
    }

    @Override
    public void loadImage(@NonNull AvatarView avatarView,
                          @NonNull AvatarPlaceholder avatarPlaceholder,
                          @NonNull String avatarUrl) {

        Glide.with(avatarView.getContext())
                .load(FirebaseStorage.getInstance().getReference(avatarUrl))
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().placeholder(avatarPlaceholder).fitCenter())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        if(e != null) {
                            Log.d(TAG, "onLoadFailed: " + e.getMessage());
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        return false;
                    }
                })
                .into(avatarView);
    }

}
