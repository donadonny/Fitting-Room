package tk.talcharnes.unborify.OtherFragmentActivities.About;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import java.util.Arrays;

import tk.talcharnes.unborify.R;

/**
 * Created by Khuram Chaudhry on 8/31/17.
 * This is the about fragment. Nothing special here.
 */

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager =
                (HorizontalInfiniteCycleViewPager) rootView.findViewById(R.id.hicvp);

        int[] aboutDesc = {R.string.about_desc_1, R.string.about_desc_2, R.string.about_desc_3};

        horizontalInfiniteCycleViewPager.setAdapter(new HorizontalPagerAdapter(
                getActivity(), aboutDesc));

        return rootView;
    }
}