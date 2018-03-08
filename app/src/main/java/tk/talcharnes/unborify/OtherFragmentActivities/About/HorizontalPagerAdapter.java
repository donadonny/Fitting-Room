package tk.talcharnes.unborify.OtherFragmentActivities.About;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import tk.talcharnes.unborify.R;

/**
 * Created by Khuram Chaudhry on 10/23/17.
 * This PagerAdapter holds and sets the card views for the about screen.
 */

public class HorizontalPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int[] texts;
    private int[] colorsActive;

    public HorizontalPagerAdapter(final Context context, int[]texts) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.texts = texts;
        colorsActive = context.getResources().getIntArray(R.array.array_dot_active);

    }

    @Override
    public int getCount() {
        return texts.length;
    }

    @Override
    public int getItemPosition(@NonNull final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        View view = mLayoutInflater.inflate(R.layout.card_about, container, false);
        setupItem(view, position);

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

    private void setupItem(final View view, final int position) {
        final TextView textView = (TextView) view.findViewById(R.id.text_item);
        textView.setText(texts[position]);
        textView.setBackgroundColor(colorsActive[position%colorsActive.length]);

    }
}