package tk.talcharnes.unborify.OtherFragmentActivities.FavoritesAndLikes;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.PhotoCard.ZoomPhotoActivity;

/**
 * Created by khuramchaudhry on 10/23/17.
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
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View view = mLayoutInflater.inflate(R.layout.card_view, container, false);
        setupItem(view, urls.get(position));

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }

    public void setupItem(final View view, final String url) {
        final ImageView img = (ImageView) view.findViewById(R.id.img_item);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        final StorageReference storageReference = FirebaseConstants.getStorRef()
                .child(FirebaseConstants.IMAGES).child(url+".webp");
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ZoomPhotoActivity.class);
                intent.putExtra("url", url+".webp");
                intent.putExtra("rotation", 0);
                mContext.startActivity(intent);
            }
        });
        FirebaseConstants.loadImageUsingGlide(mContext, img, storageReference, progressBar);

    }
}