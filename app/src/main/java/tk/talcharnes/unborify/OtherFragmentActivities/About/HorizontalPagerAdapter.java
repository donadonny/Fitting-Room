package tk.talcharnes.unborify.OtherFragmentActivities.About;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.PhotoCard.ZoomPhotoActivity;
import tk.talcharnes.unborify.Utilities.StorageConstants;

/**
 * Created by Khuram Chaudhry on 10/23/17.
 * This PagerAdapter holds and sets the card views for the user's likes and favorites list.
 */

public class HorizontalPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> urls;

    public HorizontalPagerAdapter(final Context context, ArrayList<String> urls) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public int getItemPosition(@NonNull final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        View view = mLayoutInflater.inflate(R.layout.card_view, container, false);
        setupItem(view, urls.get(position));

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull final View view, @NonNull final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull final ViewGroup container, final int position,
                            @NonNull final Object object) {
        container.removeView((View) object);
    }

    private void setupItem(final View view, final String url) {
        final ImageView img = (ImageView) view.findViewById(R.id.img_item);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        final StorageReference storageReference = StorageConstants.getImageRef(url + ".webp");
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ZoomPhotoActivity.class);
                intent.putExtra("url", url + ".webp");
                intent.putExtra("rotation", 0);
                mContext.startActivity(intent);
            }
        });
        StorageConstants.loadImageUsingGlide(mContext, img, storageReference, progressBar);

    }
}