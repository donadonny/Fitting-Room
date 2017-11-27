package tk.talcharnes.unborify.BottomBar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khuram Chaudhry on 9/29/17.
 *
 * This class is an adapter that SmartFragmentStatePagerAdapter
 * and holds the fragments for the bottom bar.
 */

public class BottomBarAdapter extends SmartFragmentStatePagerAdapter {

    private final List<Fragment> fragments = new ArrayList<>();

    /**
     * Default Constructor.
     */
    public BottomBarAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    /**
     * This function is a custom method that populates this Adapter with Fragments.
     */
    public void addFragments(Fragment fragment) {
        fragments.add(fragment);
    }

    /**
     * This function returns the fragment for the given position.
     */
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    /**
     * This function returns the number of fragments.
     */
    @Override
    public int getCount() {
        return fragments.size();
    }
}