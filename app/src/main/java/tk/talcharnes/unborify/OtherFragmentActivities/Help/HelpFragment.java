package tk.talcharnes.unborify.OtherFragmentActivities.Help;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import tk.talcharnes.unborify.R;

/**
 * Created by Khuram Chaudhry on 8/31/17.
 * This fragment handles the user interactions with the help screen.
 */

public class HelpFragment extends Fragment {

    private View rootView;
    private Activity activity;
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int[] layouts;
    private Button btnNext; //btnSkip

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_help, container, false);
        activity = getActivity();

        initialize();

        return rootView;
    }

    /**
     * Initializes the basic components.
     */
    public void initialize() {
        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) rootView.findViewById(R.id.layoutDots);
        //btnSkip = (Button) rootView.findViewById(R.id.btn_skip);
        btnNext = (Button) rootView.findViewById(R.id.btn_next);

        layouts = new int[]{R.layout.help_screen1, R.layout.help_screen2, R.layout.help_screen3,
                R.layout.help_screen4};

        addBottomDots(0);

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);

                if (position == layouts.length - 1) {
                    btnNext.setText("");
                    //btnSkip.setVisibility(View.GONE);
                } else {
                    btnNext.setText(getString(R.string.next));
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager.getCurrentItem() + 1;
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                }
            }
        });
    }

    /**
     * This method sets up the bottom dots on the screen.
     */
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getActivity());
            final int upSymbolUnicode = 0x2022;
            final String dot = Character.toString((char)upSymbolUnicode);
            dots[i].setText(dot);
            dots[i].setTextSize(40);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    /**
     * View pager adapter class sets up the multiple help screens.
     */
    class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);

            if(layoutInflater == null) {
                activity.finish();
            }

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}