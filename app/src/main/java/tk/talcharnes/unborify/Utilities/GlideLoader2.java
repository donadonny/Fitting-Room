package tk.talcharnes.unborify.Utilities;

import android.support.annotation.NonNull;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.StorageReference;

import agency.tango.android.avatarview.AvatarPlaceholder;
import agency.tango.android.avatarview.ImageLoaderBase;
import agency.tango.android.avatarview.views.AvatarView;

/**
 * Created by khuramchaudhry on 10/19/17.
 */

public class GlideLoader2 extends ImageLoaderBase {

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

        StorageReference storageReference = StorageConstants.getUserPhotoRef(avatarUrl);

        GlideApp.with(avatarView.getContext())
                .load(storageReference)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().placeholder(avatarPlaceholder).fitCenter())
                .into(avatarView);
    }

}
