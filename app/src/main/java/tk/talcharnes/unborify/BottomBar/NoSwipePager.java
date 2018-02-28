package tk.talcharnes.unborify.BottomBar;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Khuram Chaudhry on 9/29/17.
 *
 * This is a custom ViewPager class used to disable/enable the swipe between tabs.
 */

public class NoSwipePager extends ViewPager {

    private boolean enabled;

    /**
     * Default Constructor.
     */
    public NoSwipePager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    /**
     * This function return true if a touch event is registered and enabled is set to true.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.enabled && super.onTouchEvent(event);
    }

    /**
     * This function return true if a touch event is intercepted and enabled is set to true.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.enabled && super.onInterceptTouchEvent(event);
    }

    /**
     * This function sets the enabled property.
     */
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}