package tk.talcharnes.unborify;


import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tk.talcharnes.unborify.PhotoCard.Comments.CommentActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CommentsTest {

    @Before
    public void init(){
        mActivityRule.getActivity().getSupportFragmentManager().beginTransaction();
    }

    @Rule
    public ActivityTestRule<CommentActivity> mActivityRule =
            new ActivityTestRule<CommentActivity>(CommentActivity.class);


    @Test
    public void CheckIfOneCommentsSaves(){
        onView(withId(R.id.comment_edittext)).perform(typeText("HELLO!"));
        onView(withId(R.id.submit_comment_button)).perform(click());
        onView(withId(R.id.comments_recyclerView)).check(matches(withText("HELLO!")));
    }

    @Test
    public void SaveMultipleComentsCommentsSave() {
        String demoText = "ABC";
        char x = 'C';
        for (int i = 0; i < 10; i++) {
            onView(withId(R.id.comment_edittext)).perform(typeText(demoText + (x + i)));
            onView(withId(R.id.submit_comment_button)).perform(click());
            onView(withId(R.id.comments_recyclerView)).check(matches(withText(demoText + (x + i))));
        }
    }
        @Test
        public void performCommentsScroll(){
            onView(withId(R.id.comments_recyclerView)).perform(ViewActions.swipeUp());
        }
}

