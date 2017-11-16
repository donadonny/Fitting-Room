package tk.talcharnes.unborify;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

/**
 * Created by khuramchaudhry on 10/30/17.
 * Make sure Window Animation, Transition Animation, and Animator Duration are
 * turn off in the developer setting of your device.
 */

@RunWith(AndroidJUnit4.class)
public class PhotoModelCardTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Checks swiping.
     * */
   @Test
    public void checkSwiping() {
        for(int i = 0; i < 4; i++) {
            onView(allOf(withParent(withId(R.id.main_fragment)), withId(R.id.swipeView)))
                    .perform(ViewActions.swipeLeft());
            onView(allOf(withParent(withId(R.id.main_fragment)), withId(R.id.swipeView)))
                    .perform(ViewActions.swipeRight());
        }

    }

    /**
     * Checks tapping on the PhotoModel.
     * */
    @Test
    public void checkTap() {
        onView(allOf(withParent(withId(R.id.main_fragment)), withId(R.id.swipeView)))
                .perform(ViewActions.click());

    }

}
