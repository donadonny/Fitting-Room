package tk.talcharnes.unborify;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by Tal on 4/30/2017.
 */
@RunWith(AndroidJUnit4.class)
public class RetrofitResponseTest {
    private final String LOG_TAG = RetrofitResponseTest.class.getSimpleName();
    @Rule public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void ValidRetrofitResponseOnNextButtonClick(){
        Log.i(LOG_TAG, "Clicking button");
        onView(withId(R.id.forwardButton)).perform(click());

        Log.i(LOG_TAG, "Checking TextView displayed");
        onView(withId(R.id.jokeTextView)).check(matches(isDisplayed()));
        Log.i(LOG_TAG, "Checking TextView not null");
        onView(withId(R.id.jokeTextView)).check(matches(notNullValue()));
        Log.i(LOG_TAG, "Checking TextView not empty");
        onView(allOf(withId(R.id.jokeTextView), not(withText(""))));


    }

}
